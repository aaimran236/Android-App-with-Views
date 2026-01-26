package com.example.juicetracker

import android.R
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.widget.doOnTextChanged
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
import kotlinx.coroutines.flow.filterNotNull
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
    private val entryViewModel by viewModels<EntryViewModel> { AppViewModelProvider.Factory }
    ///var selectedColor = JuiceColor.Red.name
    var selectedColor = JuiceColor.Red

    /// onCreateView() function creates the View for this Fragment.
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ///return super.onCreateView(inflater, container, savedInstanceState)
        return FragmentEntryDialogBinding.inflate(inflater, container, false).root
    }

    ///private val spinnerColors: List<String> = JuiceColor.entries.map { it.name }

    /*
    The onViewCreated() method is called after onCreateView() in the lifecycle. The
    onViewCreated() method is the recommended place to access and modify the Views within
    the layout.

    After you inflate the View binding, you can access and modify the Views in the layout.
     */
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val colorLabelMap = JuiceColor.entries.associateBy { getString(it.label) }
        val binding = FragmentEntryDialogBinding.bind(view)
        val args: EntryDialogFragmentArgs by navArgs()
        val juiceId = args.itemId

         binding.colorSpinner.adapter = ArrayAdapter(
             requireContext(),
             R.layout.simple_spinner_dropdown_item,
            ///R.layout.simple_spinner_item,
            colorLabelMap.map { it.key }
        )

        binding.colorSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selected =parent?.getItemAtPosition(position).toString()
                ///selectedColor = spinnerColors[position]
                selectedColor = colorLabelMap[selected] ?: selectedColor
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        /// the value of juiceId >0 indicate existing item , which we are editing
        if (juiceId > 0) {
            // Request to edit an existing item, whose id was passed in as an argument.
            // Retrieve that item and populate the UI with its details
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    entryViewModel.getJuiceStream(juiceId)
                        .filterNotNull().collect { juice ->
                            binding.name.setText(juice.name)
                            binding.description.setText(juice.description)

                            ///val position = spinnerColors.indexOf(juice.color)
                            val position = findColorIndex(juice.color)
                            binding.colorSpinner.setSelection(position)


                            binding.ratingBar.rating = juice.rating.toFloat()
                        }
                }
            }
        }

        binding.name.doOnTextChanged { _, start, _, count ->
            // Enable Save button if the current text is longer than 3 characters
            binding.saveButton.isEnabled = (start+count) > 0
        }

        binding.saveButton.setOnClickListener {
            entryViewModel.saveJuice(
                juiceId,
                binding.name.text.toString(),
                binding.description.text.toString(),
                selectedColor.name,
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

    private fun findColorIndex(color: String): Int{
        val juiceColor= JuiceColor.valueOf(color)
        return JuiceColor.entries.indexOf(juiceColor)
    }

}