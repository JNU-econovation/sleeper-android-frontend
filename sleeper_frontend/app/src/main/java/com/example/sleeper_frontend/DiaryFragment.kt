package com.example.sleeper_frontend

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentTransaction
import com.example.sleeper_frontend.databinding.FragmentDiaryBinding

class DiaryFragment : Fragment(R.layout.fragment_diary) {

    private lateinit var binding: FragmentDiaryBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentDiaryBinding.inflate(inflater, container, false)

        binding.btnSaveDiary.isEnabled = false

        binding.btnShowMore.setOnClickListener {
            clickBtnPopup()
        }

        binding.diary.addTextChangedListener {
            enableBtn(binding.diary)
        }

        binding.btnSaveDiary.setOnClickListener {
            saveDiary()

            val homeBFragment = HomeBFragment()
            val transaction : FragmentTransaction = requireFragmentManager().beginTransaction()
            transaction.replace(R.id.fl_container, homeBFragment).commit()
        }

        return binding.root
    }


    private fun clickBtnPopup() {
        val popup : PopupDialogFragment = PopupDialogFragment().getInstance()
        activity?.supportFragmentManager?.let { fragmentManager ->
            popup.show(
                fragmentManager,
                "tag"
            )
        }
    }

    private fun saveDiary() {

    }

    private fun enableBtn(v : EditText) {
        binding.btnSaveDiary.isEnabled = v.length() > 0

    }

}