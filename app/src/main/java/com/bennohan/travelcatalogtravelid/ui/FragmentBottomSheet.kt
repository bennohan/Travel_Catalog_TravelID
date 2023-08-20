package com.bennohan.travelcatalogtravelid.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.bennohan.travelcatalogtravelid.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FragmentBottomSheet : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val button = view.findViewById<Button>(R.id.button)

        button.setOnClickListener {
            val toast = Toast.makeText(requireActivity(), "tes", Toast.LENGTH_LONG)
            toast.show()

        }

    }

}