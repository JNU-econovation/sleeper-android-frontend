package com.example.sleeper_frontend



import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.sleeper_frontend.api.INetworkService
import com.example.sleeper_frontend.databinding.FragmentHomeBBinding
import com.example.sleeper_frontend.dto.CharacterInfoResponse
import com.example.sleeper_frontend.dto.register.RegisterRequest
import com.example.sleeper_frontend.dto.register.RegisterResponse
import com.example.sleeper_frontend.dto.sleep.SetWakeTimeRequest
import com.example.sleeper_frontend.dto.sleep.SetWakeTimeResponse
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime
import java.time.ZonedDateTime


class HomeBFragment : Fragment(R.layout.fragment_home_b) {

    private lateinit var binding: FragmentHomeBBinding
    private lateinit var mainActivity : MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHomeBBinding.inflate(inflater, container, false)

        mainActivity.hideBottomNavigation(true)

        getCharacter()

        binding.btnStopSleep.setOnClickListener {
            val sharedPref = activity?.getSharedPreferences(("user_info"),Context.MODE_PRIVATE)
            sharedPref!!.edit()
                .putBoolean("isSleep", false)
                .apply()

            tryNetwork()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mainActivity.hideBottomNavigation(false)
    }

    private fun getCharacter() {
        Log.d("hyeon", "tryNetwork작동")
        val sharedPref = activity?.getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val userPk: Long = sharedPref!!.getLong("userPk", 1L)
        val accessToken : String = sharedPref.getString("accessToken", " ").toString()

        Log.d("hyeon", "변수 초기화")


        val characterInfoResponseCall: Call<CharacterInfoResponse> =
            getNetworkService().getCharacterInfo(
                accessToken = accessToken, userpk = userPk, userPk = userPk
            )

        Log.d("hyeon", "call객체 초기화")

        characterInfoResponseCall.enqueue(object : Callback<CharacterInfoResponse> {
            override fun onResponse(
                call: Call<CharacterInfoResponse>,
                response: Response<CharacterInfoResponse>
            ) {
                Log.d("hyeon", "통신 성공")
                if (response.isSuccessful && response.body() != null) {

                    val result: CharacterInfoResponse? = response.body()
                    val resultCode: String = response.code().toString()

                    val speechBubble: String = result!!.speechBubble

                    Log.d("hyeon", resultCode)
                    val success: String = "200";
                    val badRequest: String = "300"
                    val internalServerError: String = "500"


                    if (resultCode == success) {
                        Log.d("hyeon", "정상 통신")
                    }
                }
            }

            override fun onFailure(call: Call<CharacterInfoResponse>, t: Throwable) {
                Log.d("hyeon", "통신 실패")
                val string = t.message.toString()
                Log.d("hyeon", string)
                Log.d("hyeon", characterInfoResponseCall.toString())
            }
        })
    }

    private fun getNetworkService(): INetworkService {
        val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .addInterceptor(RequestInterceptor())
            .addInterceptor(ResponseInterceptor())
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

    inner class RequestInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            val sharedPref = activity?.getSharedPreferences("user_info", Context.MODE_PRIVATE)
            val accessToken : String = sharedPref!!.getString("accessToken", " ").toString()

            val builder = chain.request()
                .newBuilder()
                .addHeader("Authorization", accessToken)
                .build()

            return chain.proceed(builder)
        }
    }

    inner class ResponseInterceptor : Interceptor {

        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            val request = chain.request()
            val response = chain.proceed(request)

            val sharedPref = activity?.getSharedPreferences("user_info", Context.MODE_PRIVATE)
            val refreshToken : String = sharedPref!!.getString("refreshToken", " ").toString()

            when (response.code) {
                400 -> {
                    // todo Control Error
                }
                401 -> {
                    val builder = response.request
                        .newBuilder()
                        .removeHeader("Authorization")
                        .addHeader("Authorization", refreshToken)
                        .build()

                    return chain.proceed(builder)
                }
                402 -> {
                    // todo Control Error
                }
            }
            return response
        }
    }

    private fun tryNetwork() {
        Log.d("hyeon", "tryNetwork작동")
        val temp = ZonedDateTime.now()
        val actualWakeTime = temp.toString()
        val sharedPref = activity?.getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val userPk : Long = sharedPref!!.getLong("userPk", 1L)
        val sleepPk : Long = sharedPref.getLong("sleepPk", 1L)
        val accessToken : String = sharedPref.getString("accessToken", " ").toString()

        Log.d("hyeon","변수 초기화")

        val setWakeTimeResponseCall : Call<SetWakeTimeResponse> = getNetworkService().putActualWakeTime(
           accessToken = accessToken, sleepPk = sleepPk, SetWakeTimeRequest(actualWakeTime = actualWakeTime, userPk = userPk)
        )

        Log.d("hyeon","call객체 초기화")
        setWakeTimeResponseCall.enqueue(object : Callback<SetWakeTimeResponse> {
            override fun onResponse(call : Call<SetWakeTimeResponse>, response: Response<SetWakeTimeResponse>) {
                Log.d("hyeon", "통신 성공")
                if (response.isSuccessful && response.body() != null) {

                    val result: SetWakeTimeResponse = response.body()!!
                    val resultCode: String = response.code().toString()

                    val sleepPk : Long = result.sleepPk

                    Log.d("hyeon", resultCode)
                    val success: String = "200";
                    val badRequest: String = "300"
                    val internalServerError: String = "500"


                    if (resultCode == success) {
                        sharedPref.edit().run{
                            putLong("sleepPk", sleepPk)
                            commit()
                        }

                        val homeFragment = HomeFragment()
                        val transaction : FragmentTransaction = requireFragmentManager().beginTransaction()
                        transaction.replace(R.id.fl_container, homeFragment).commit()
                    }
                }
            }
            override fun onFailure(call: Call<SetWakeTimeResponse>, t: Throwable) {
                Log.d("hyeon", "통신 실패")
                val string = t.message.toString()
                Log.d("hyeon", string)
                Log.d("hyeon", setWakeTimeResponseCall.toString())
            }
        })
    }


}