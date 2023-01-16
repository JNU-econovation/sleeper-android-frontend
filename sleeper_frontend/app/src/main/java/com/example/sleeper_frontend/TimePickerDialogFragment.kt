package com.example.sleeper_frontend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.sleeper_frontend.databinding.FragmentTimePickerDialogBinding

class TimePickerDialogFragment : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //false로 설정해 주면 화면밖 혹은 뒤로가기 버튼시 다이얼로그라 dismiss 되지 않는다.
        isCancelable = true
    }

    private lateinit var binding: FragmentTimePickerDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTimePickerDialogBinding.inflate(inflater, container, false)

        binding.btnYes.setOnClickListener {
            clickBtnYes()
        }

        binding.btnNo.setOnClickListener {
            clickBtnNo()
        }

        val s = arguments?.getString("key")
//        Toast.makeText(context,s, Toast.LENGTH_SHORT).show()

        return binding.root
    }

    interface ButtonClickListener {
        fun onClicked(hour: Int, minute : Int)
    }

    private lateinit var onClickedListener: ButtonClickListener

    fun setOnClickedListener(listener: ButtonClickListener) {
        onClickedListener = listener
    }

    private fun clickBtnYes() {

        onClickedListener.onClicked(binding.timepicker.hour, binding.timepicker.minute)
        dismiss()
    }

    private fun clickBtnNo() {
        dismiss()
    }

}