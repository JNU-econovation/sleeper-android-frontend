package com.example.sleeper_frontend



import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.sleeper_frontend.api.INetworkService
import com.example.sleeper_frontend.databinding.FragmentHomeBBinding
import com.example.sleeper_frontend.dto.character.CharacterInfoResponse
import com.example.sleeper_frontend.dto.sleep.SetWakeTimeRequest
import com.example.sleeper_frontend.dto.sleep.SetWakeTimeResponse
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.CookieManager
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
            val sharedPref = requireActivity().getSharedPreferences(("user_info"),Context.MODE_PRIVATE)
            sharedPref.edit()
                .putBoolean("isSleep", false)
                .apply()

            setWakeTime()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mainActivity.hideBottomNavigation(false)
    }

    private fun getCharacter() {

        val sharedPref = requireActivity().getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val userPk: Long = sharedPref.getLong("userPk", 1L)
        val accessToken : String = sharedPref.getString("accessToken", " ").toString()


        val characterInfoResponseCall: Call<CharacterInfoResponse> =
            getNetworkService().getCharacterInfo(
                accessToken = accessToken,  userPk = userPk
            )


        characterInfoResponseCall.enqueue(object : Callback<CharacterInfoResponse> {
            override fun onResponse(call: Call<CharacterInfoResponse>, response: Response<CharacterInfoResponse>) {
                Log.d("캐릭터 읽기", "통신 상태 : 성공")
                if (response.isSuccessful && response.body() != null) {

                    val result: CharacterInfoResponse? = response.body()
                    val resultCode: String = response.code().toString()

                    val speechBubble: String = result!!.speechBubble

                    Log.d("캐릭터 읽기", "결과 코드 : $resultCode")
                    val success: String = "200";
                    val badRequest: String = "300"
                    val internalServerError: String = "500"


                    if (resultCode == success) {
                        Log.d("캐릭터 읽기", "통신 상태 : 정상 통신")
                    }
                }
            }

            override fun onFailure(call: Call<CharacterInfoResponse>, t: Throwable) {
                Log.d("캐릭터 읽기", "통신 상태 : 실패")
                val string = t.message.toString()
                Log.d("캐릭터 읽기", "예외 메세지 : $String")
                Log.d("캐릭터 읽기","요청 내용 : $characterInfoResponseCall")
            }
        })
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

    private fun setWakeTime() {

        val temp = ZonedDateTime.now()
        val actualWakeTime = temp.toString()
        val sharedPref = requireActivity().getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val userPk : Long = sharedPref.getLong("userPk", 1L)
        val sleepPk : Long = sharedPref.getLong("sleepPk", 1L)
        val accessToken : String = sharedPref.getString("accessToken", " ").toString()


        val setWakeTimeResponseCall : Call<SetWakeTimeResponse> = getNetworkService().putActualWakeTime(
           accessToken = accessToken, sleepPk = sleepPk, SetWakeTimeRequest(actualWakeTime = actualWakeTime, userPk = userPk)
        )

        setWakeTimeResponseCall.enqueue(object : Callback<SetWakeTimeResponse> {
            override fun onResponse(call : Call<SetWakeTimeResponse>, response: Response<SetWakeTimeResponse>) {
                Log.d("수면 종료", "통신 상태 : 성공")

                if (response.isSuccessful && response.body() != null) {

                    val result: SetWakeTimeResponse = response.body()!!
                    val resultCode: String = response.code().toString()

                    val sleepPk : Long = result.sleepPk

                    Log.d("수면 종료", "결과 코드 : $resultCode")
                    val success: String = "200";
                    val badRequest: String = "300"
                    val internalServerError: String = "500"


                    if (resultCode == success) {
                        Log.d("수면 종료", "통신 상태 : 정상 통신")
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
                Log.d("수면 종료", "통신 상태 : 실패")
                val string = t.message.toString()
                Log.d("수면 종료", "예외 메세지 : $string")
                Log.d("수면 종료", "요청 내용 : $setWakeTimeResponseCall")
            }
        })
    }


}