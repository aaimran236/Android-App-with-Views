package com.example.juicetracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.juicetracker.databinding.FragmentEntryDialogBinding
import com.example.juicetracker.ui.AppViewModelProvider
import com.example.juicetracker.ui.EntryViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

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

    /// onCreateView() function creates the View for this Fragment.
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ///return super.onCreateView(inflater, container, savedInstanceState)
        return FragmentEntryDialogBinding.inflate(inflater,container,false).root
    }

    private val entryViewModel by viewModels<EntryViewModel> { AppViewModelProvider.Factory }

    /*
    The onViewCreated() method is called after onCreateView() in the lifecycle. The
    onViewCreated() method is the recommended place to access and modify the Views within
    the layout.

    After you inflate the View binding, you can access and modify the Views in the layout.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentEntryDialogBinding.bind(view)
        ///super.onViewCreated(view, savedInstanceState)
    }

}