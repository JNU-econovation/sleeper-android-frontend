package com.example.sleeper_frontend

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import com.example.sleeper_frontend.api.INetworkService
import com.example.sleeper_frontend.databinding.FragmentHomeBinding
import com.example.sleeper_frontend.dto.diary.CheckDiaryResponse
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.btnShowMore.setOnClickListener {
            clickBtnPopup()
        }

        binding.btnStartSleep.setOnClickListener {
            checkDiary()
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

    private fun changeFragment() {
        val diaryFragment = DiaryFragment()
        val transaction : FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()

        transaction?.replace(R.id.fl_container, diaryFragment)?.addToBackStack("HomeFragment")?.commit()
    }

    private fun getNetworkService(): INetworkService {
        val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        val gson : Gson = GsonBuilder()
            .setLenient()
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.21.2:8082/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()

        return retrofit.create(INetworkService::class.java)
    }

    private fun checkDiary() {
        Log.d("hyeon", "tryNetwork작동")
        val sharedPref = activity?.getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val userPk : Long = sharedPref?.getLong("userPk", 1L)

        Log.d("hyeon","변수 초기화")


        val checkDiaryResponseCall : Call<CheckDiaryResponse> = getNetworkService().checkDiaryExistence(
            userPk = userPk
        )

        Log.d("hyeon","call객체 초기화")

        checkDiaryResponseCall.enqueue(object : Callback<CheckDiaryResponse> {
            override fun onResponse(call : Call<CheckDiaryResponse>, response: Response<CheckDiaryResponse>) {
                Log.d("hyeon", "통신 성공")
                if (response.isSuccessful && response.body() != null) {

                    val result: CheckDiaryResponse? = response.body()
                    val resultCode: String = response.code().toString()

                    val existence : Boolean = result!!.existence
                    val content : String = result.content

                    Log.d("hyeon", resultCode)
                    val success: String = "200";
                    val badRequest: String = "300"
                    val internalServerError: String = "500"


                    if (resultCode == success) {
                        val bundle = Bundle()
                        if(existence != null) {
                            bundle.putString("content", content)
                        }

                        val diaryFragment = DiaryFragment()
                        diaryFragment.arguments = bundle
                        val transaction : FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()

                        transaction?.replace(R.id.fl_container, diaryFragment)?.addToBackStack("HomeFragment")?.commit()

                    }
                }
            }
            override fun onFailure(call: Call<CheckDiaryResponse>, t: Throwable) {
                Log.d("hyeon", "통신 실패")
                val string = t.message.toString()
                Log.d("hyeon", string)
                Log.d("hyeon", checkDiaryResponseCall.toString())
            }
        })

    }
}