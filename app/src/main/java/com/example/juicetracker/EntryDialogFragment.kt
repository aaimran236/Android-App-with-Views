package com.example.juicetracker

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.example.juicetracker.data.JuiceColor
import com.example.juicetracker.databinding.FragmentEntryDialogBinding
import com.example.juicetracker.ui.AppViewModelProvider
import com.example.juicetracker.ui.EntryViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch


/*
 In Compose, you build layouts declaratively using Kotlin or Java. You can access different
 "screens" by navigating to different Composables, typically within the same activity. When
 building an app with Views, a Fragment that hosts the XML layout replaces the concept of a
 Composable "screen."
 */

/*
 The DialogFragment is a Fragment that displays a floating dialog. BottomSheetDialogFragment
 inherits from the DialogFragment class, but displays a sheet the width of the screen pinned
 to the bottom of the screen.
 */

class EntryDialogFragment : BottomSheetDialogFragment() {
    var selectedColor = JuiceColor.Red.name

    /// onCreateView() function creates the View for this Fragment.
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ///return super.onCreateView(inflater, container, savedInstanceState)
        return FragmentEntryDialogBinding.inflate(inflater, container, false).root
    }

    private val entryViewModel by viewModels<EntryViewModel> { AppViewModelProvider.Factory }

    private val spinnerColors: List<String> = JuiceColor.entries.map { it.name }

    /*
    The onViewCreated() method is called after onCreateView() in the lifecycle. The
    onViewCreated() method is the recommended place to access and modify the Views within
    the layout.

    After you inflate the View binding, you can access and modify the Views in the layout.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentEntryDialogBinding.bind(view)

        val args: EntryDialogFragmentArgs by navArgs()
        val juiceId = args.itemId

        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.simple_spinner_item,
            spinnerColors
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.colorSpinner.adapter = adapter

        binding.colorSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedColor = spinnerColors[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        if (juiceId > 0) {
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    entryViewModel.getJuiceStream(juiceId).collect { juice ->
                        binding.name.setText(juice?.name)
                        binding.description.setText(juice?.description)

                        val position = spinnerColors.indexOf(juice?.color)
                        binding.colorSpinner.setSelection(position)

                        binding.ratingBar.rating = juice?.rating?.toFloat() ?: 0F
                    }
                }
            }
        }

        binding.saveButton.setOnClickListener {
            entryViewModel.saveJuice(
                juiceId,
                binding.name.text.toString(),
                binding.description.text.toString(),
                selectedColor,
                binding.ratingBar.rating.toInt()
            )

            ///After the data is saved, dismiss the dialog with the dismiss() method.
            dismiss()
        }

        binding.cancelButton.setOnClickListener {
            ///when cancelButton is clicked dismiss the dialog with the dismiss() method
            dismiss()
        }
    }

}