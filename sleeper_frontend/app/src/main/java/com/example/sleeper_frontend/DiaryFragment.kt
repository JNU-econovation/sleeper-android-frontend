package com.example.sleeper_frontend

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import androidx.fragment.app.FragmentTransaction
import com.example.sleeper_frontend.api.INetworkService
import com.example.sleeper_frontend.databinding.FragmentDiaryBinding
import com.example.sleeper_frontend.dto.diary.SaveDiaryRequest
import com.example.sleeper_frontend.dto.diary.SaveDiaryResponse
import com.example.sleeper_frontend.dto.login.LoginRequest
import com.example.sleeper_frontend.dto.login.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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

            /*activity?.supportFragmentManager?.popBackStack("HomeFragment", POP_BACK_STACK_INCLUSIVE)

            val homeBFragment = HomeBFragment()
            val transaction : FragmentTransaction = requireFragmentManager().beginTransaction()
            transaction.replace(R.id.fl_container, homeBFragment).commit()*/
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
        getNetworkService()

        var content : String = binding.diary.text.toString()
        var userPk : Long = 1 //수정

        val saveDiaryResponseCall = getNetworkService().getDiaryPk(SaveDiaryRequest(content = content, userPk = userPk))

        saveDiaryResponseCall.enqueue(object : Callback<SaveDiaryResponse> {
            override fun onResponse(
                call: Call<SaveDiaryResponse>,
                response: Response<SaveDiaryResponse>) {

                if (response.isSuccessful && response.body() != null) {
                    val result: SaveDiaryResponse? = response.body()
                    //받은 코드 저장
                    val resultCode: String = response.code().toString()

                    val success: String = "200"; //로그인 성공
                    val badRequest: String = "300"
                    val internalServerError: String = "500"


                    if (resultCode == success) {
                        activity?.supportFragmentManager?.popBackStack("HomeFragment", POP_BACK_STACK_INCLUSIVE)

                        val homeBFragment = HomeBFragment()
                        val transaction : FragmentTransaction = requireFragmentManager().beginTransaction()
                        transaction.replace(R.id.fl_container, homeBFragment).commit()
                    }
                }
            }

            override fun onFailure(call: Call<SaveDiaryResponse>, t: Throwable) {}
        })
    }

    private fun enableBtn(v : EditText) {
        binding.btnSaveDiary.isEnabled = v.length() > 0

    }

    private fun getNetworkService(): INetworkService {
        val retrofit = Retrofit.Builder()
            .baseUrl("localhost:8080")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(INetworkService::class.java)
    }

}