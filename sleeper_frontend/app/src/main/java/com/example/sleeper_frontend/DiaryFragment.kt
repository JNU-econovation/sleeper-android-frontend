package com.example.sleeper_frontend

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import com.example.sleeper_frontend.dto.diary.*
import com.example.sleeper_frontend.dto.login.LoginRequest
import com.example.sleeper_frontend.dto.login.LoginResponse
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.CookieManager

class DiaryFragment : Fragment(R.layout.fragment_diary) {

    private lateinit var binding: FragmentDiaryBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentDiaryBinding.inflate(inflater, container, false)

        binding.btnSaveDiary.isEnabled = false


        if(arguments != null) {
            binding.diary.setText(requireArguments().getString("content").toString())
            binding.btnSaveDiary.isEnabled = true
            val diaryPk : Long = requireArguments().getLong("diaryPk", 1)

            binding.btnSaveDiary.setOnClickListener {

                continueDiary(diaryPk)

                val sharedPref = requireActivity().getSharedPreferences(("user_info"),Context.MODE_PRIVATE)
                sharedPref.edit()
                    .putBoolean("isSleep", true)
                    .apply()

            }
        } else {
            binding.btnSaveDiary.setOnClickListener {

                saveDiary()

                val sharedPref = requireActivity().getSharedPreferences(("user_info"),Context.MODE_PRIVATE)
                sharedPref.edit()
                    .putBoolean("isSleep", true)
                    .apply()

            }
        }

        binding.btnShowMore.setOnClickListener {
            clickBtnPopup()
        }

        binding.diary.addTextChangedListener {
            enableBtn(binding.diary)
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

        val content : String = binding.diary.text.toString()
        val sharedPref = activity?.getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val userPk : Long = sharedPref!!.getLong("userPk", 1L)
        val accessToken : String = sharedPref.getString("accessToken", " ").toString()

        val saveDiaryResponseCall = getNetworkService().getDiaryPk(
            accessToken = accessToken, SaveDiaryRequest(content = content, userPk = userPk))


        saveDiaryResponseCall.enqueue(object : Callback<SaveDiaryResponse> {
            override fun onResponse( call: Call<SaveDiaryResponse>, response: Response<SaveDiaryResponse>) {

                Log.d("다이어리 저장", "통신 상태 : 성공")
                if (response.isSuccessful && response.body() != null) {
                    val result: SaveDiaryResponse? = response.body()
                    val resultCode: String = response.code().toString()

                    Log.d("다이어리 저장", "결과 코드 : $resultCode")
                    val success: String = "200"; //로그인 성공
                    val badRequest: String = "300"
                    val internalServerError: String = "500"


                    if (resultCode == "201") {

                        Log.d("다이어리 저장", "통신 상태 : 정상 통신")

                        val homeBFragment = HomeBFragment()

                        requireFragmentManager().popBackStack("HomeFragment", POP_BACK_STACK_INCLUSIVE)

                        val transaction : FragmentTransaction = requireFragmentManager().beginTransaction()
                        transaction
                            .replace(R.id.fl_container, homeBFragment).commit()
                    }
                }
            }

            override fun onFailure(call: Call<SaveDiaryResponse>, t: Throwable) {
                Log.d("다이어리 저장", "통신 상태 : 실패")
                val string = t.message.toString()
                Log.d("다이어리 저장", "예외 메세지 : $string")
                Log.d("다이어리 저장", "요청 내용 : $saveDiaryResponseCall")

            }
        })
    }

    private fun enableBtn(v : EditText) {
        binding.btnSaveDiary.isEnabled = v.length() > 0

    }

    private fun getNetworkService(): INetworkService {
        val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .cookieJar(JavaNetCookieJar(CookieManager()))
            .build()

        val gson : Gson = GsonBuilder()
            .setLenient()
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.0.110:8080/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()

        return retrofit.create(INetworkService::class.java)
    }

    private fun continueDiary(diaryPk : Long) {


        val sharedPref = activity?.getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val accessToken : String = sharedPref!!.getString("accessToken", " ").toString()
        val content = binding.diary.text.toString()
        val userPk : Long = sharedPref.getLong("userPk", 1L)


        val continueDiaryResponseCall = getNetworkService().continueDiary(
            accessToken, diaryPk, ContinueDiaryRequest(content = content, userPk = userPk))


        continueDiaryResponseCall.enqueue(object : Callback<ContinueDiaryResponse> {
            override fun onResponse( call: Call<ContinueDiaryResponse>, response: Response<ContinueDiaryResponse>) {

                Log.d("다이어리 이어쓰기", "통신 상태 : 성공")

                if (response.isSuccessful && response.body() != null) {
                    val result: ContinueDiaryResponse? = response.body()
                    val resultCode: String = response.code().toString()

                    Log.d("다이어리 이어쓰기", "결과 코드 : $resultCode")

                    val success: String = "200"
                    val badRequest: String = "300"
                    val internalServerError: String = "500"

                    if (resultCode == "201") {
                        Log.d("다이어리 이어쓰기", "통신 상태 : 정상 통신")
                        val homeBFragment = HomeBFragment()

                        val transaction : FragmentTransaction = requireFragmentManager().beginTransaction()
                        transaction
                            .replace(R.id.fl_container, homeBFragment).commit()
                    }
                }
            }

            override fun onFailure(call: Call<ContinueDiaryResponse>, t: Throwable) {
                Log.d("다이어리 이어쓰기", "통신 상태 : 실패")
                val string = t.message.toString()
                Log.d("다이어리 이어쓰기", "예외 메세지 : $string")
                Log.d("다이어리 이어쓰기", "요청 내용 : $continueDiaryResponseCall")
            }
        })
    }

}