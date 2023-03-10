package com.example.sleeper_frontend

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import com.example.sleeper_frontend.api.INetworkService
import com.example.sleeper_frontend.databinding.FragmentHomeBinding
import com.example.sleeper_frontend.dto.character.CharacterInfoResponse
import com.example.sleeper_frontend.dto.diary.CheckDiaryResponse
import com.example.sleeper_frontend.dto.sleep.GetSettingTimeResponse
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
import java.util.*

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val sharedPref = requireActivity().getSharedPreferences(("user_info"),Context.MODE_PRIVATE)
        val isSleep = sharedPref.getBoolean("isSleep", false)
        var isListening = false
        /*if(isSleep) {
            val homeBFragment = HomeBFragment()
            val transaction : FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.fl_container, homeBFragment)?.commit()
        }*/

        binding = FragmentHomeBinding.inflate(inflater, container, false)

        getCharacter()

        binding.btnShowMore.setOnClickListener {
            clickBtnPopup()
        }

        binding.btnStartSleep.setOnClickListener {
            setAlarmTime()
            checkDiary()
        }

        binding.btnCharacter.setOnClickListener {
            isListening = !isListening
            if(isListening) {
                binding.imgCharacter.setImageResource(R.drawable.star1_music)
            } else {
                binding.imgCharacter.setImageResource(R.drawable.star1_basic)
            }

        }

        return binding.root
    }

    private fun getCharacter() {
        val sharedPref = requireActivity().getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val userPk : Long = sharedPref.getLong("userPk", 1L)
        val accessToken : String = sharedPref.getString("accessToken", "").toString()

        Log.d("????????? ?????????", "?????? ?????? : ?????? ?????????")


        val characterInfoResponseCall : Call<CharacterInfoResponse> = getNetworkService().getCharacterInfo(
            accessToken = accessToken, userPk = userPk
        )

        Log.d("????????? ?????????", "?????? ?????? : Call ?????? ??????")

        characterInfoResponseCall.enqueue(object : Callback<CharacterInfoResponse> {
            override fun onResponse(call : Call<CharacterInfoResponse>, response: Response<CharacterInfoResponse>) {
                Log.d("????????? ?????????", "?????? ?????? : ??????")
                if (response.isSuccessful && response.body() != null) {

                    val result: CharacterInfoResponse? = response.body()
                    val resultCode: String = response.code().toString()

                    val speechBubble : String = result!!.speechBubble

//                    binding.messageHome.text = speechBubble

                    Log.d("????????? ?????????", "?????? ?????? : $resultCode")

                    val success: String = "200";
                    val badRequest: String = "300"
                    val internalServerError: String = "500"


                    if (resultCode == success) {
                        Log.d("????????? ?????????", "?????? ?????? : ?????? ??????")
                    }
                }
            }
            override fun onFailure(call: Call<CharacterInfoResponse>, t: Throwable) {
                Log.d("????????? ?????????", "?????? ?????? : ??????")
                val string = t.message.toString()
                Log.d("????????? ?????????", "?????? ????????? : $string")
                Log.d("????????? ?????????", "?????? ?????? : $characterInfoResponseCall")
            }
        })
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

    private fun checkDiary() {

        val sharedPref = requireActivity().getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val userPk : Long = sharedPref.getLong("userPk", 1L)
        val accessToken : String = sharedPref.getString("accessToken", "").toString()


        val checkDiaryResponseCall : Call<CheckDiaryResponse> = getNetworkService().checkDiaryExistence(
            accessToken = accessToken, userPk = userPk
        )


        Log.d("???????????? ??????", "?????? ?????? : Call ?????? ?????????")


        checkDiaryResponseCall.enqueue(object : Callback<CheckDiaryResponse> {
            override fun onResponse(call : Call<CheckDiaryResponse>, response: Response<CheckDiaryResponse>) {

                Log.d("???????????? ??????", "?????? ?????? : ??????")

                if (response.isSuccessful && response.body() != null) {

                    val result: CheckDiaryResponse? = response.body()
                    val resultCode: String = response.code().toString()

                    val diaryPk : Long = result!!.diaryPk
                    val content : String = result.content
                    val existence : Boolean = result.existence

                    Log.d("???????????? ??????", "?????? ?????? : $resultCode")
                    val success: String = "200";
                    val badRequest: String = "300"
                    val internalServerError: String = "500"


                    if (resultCode == success) {

                        Log.d("???????????? ??????", "?????? ?????? : ?????? ??????")


                        val bundle = Bundle()
                        val diaryFragment = DiaryFragment()

                        if(existence) {
                            bundle.putLong("diaryPk", diaryPk)
                            bundle.putString("content", content)

                            diaryFragment.arguments = bundle
                        }

                        val transaction : FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
                        transaction?.replace(R.id.fl_container, diaryFragment)?.addToBackStack("HomeFragment")?.commit()

                    }
                }
            }
            override fun onFailure(call: Call<CheckDiaryResponse>, t: Throwable) {
                Log.d("???????????? ??????", "?????? ?????? : ??????")
                val string = t.message.toString()
                Log.d("???????????? ??????", "?????? ????????? : $string ")
                Log.d("???????????? ??????", "?????? ?????? : $checkDiaryResponseCall ")
            }
        })

    }

    private fun setAlarmTime() {

        val sharedPref = activity?.getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val userPk : Long = sharedPref!!.getLong("userPk", 1L)
        val accessToken : String = sharedPref.getString("accessToken", " ").toString()

        Log.d("?????? ?????? ??????", "?????? ?????? : ?????? ?????????")

        val getSettingTimeResponseCall : Call<GetSettingTimeResponse> = getNetworkService().getSettingTime(accessToken = accessToken, userPk = userPk)

        Log.d("?????? ?????? ??????", "?????? ?????? : Call ?????? ?????????")

        getSettingTimeResponseCall.enqueue(object : Callback<GetSettingTimeResponse> {
            override fun onResponse(call : Call<GetSettingTimeResponse>, response: Response<GetSettingTimeResponse>) {

                Log.d("?????? ?????? ??????", "?????? ?????? : ??????")
                if (response.isSuccessful && response.body() != null) {

                    val result: GetSettingTimeResponse = response.body()!!
                    val resultCode: String = response.code().toString()

                    Log.d("?????? ?????? ??????", "?????? ?????? : $resultCode")
                    val success: String = "200"
                    val badRequest: String = "300"
                    val internalServerError: String = "500"


                    if (resultCode == success) {
                        Log.d("?????? ?????? ??????", "?????? ?????? : ?????? ??????")
                        val setWakeTime = result.setWakeTime

                        getSettingTime(setWakeTime)
                        Log.d("?????? ?????? ??????", "?????? : ?????? ?????? ?????? ??????")
                    }
                }
            }
            override fun onFailure(call: Call<GetSettingTimeResponse>, t: Throwable) {
                Log.d("?????? ?????? ??????", "?????? ?????? : ??????")
                val string = t.message.toString()
                Log.d("?????? ?????? ??????", "?????? ????????? : $string")
                Log.d("?????? ?????? ??????", "?????? ?????? : $getSettingTimeResponseCall")
            }
        })
    }

    private fun getSettingTime(wakeTime : String){
        val hourString = if (wakeTime.substring(0) == "0") {
            wakeTime.substring(1 until 2)
        } else {
            wakeTime.substring(0 until 2)
        }

        val minString = wakeTime.substring(3 until 5)

        val hourInt = Integer.parseInt(hourString)
        val minInt = Integer.parseInt(minString)
        val calendar = Calendar.getInstance()

        calendar.set(Calendar.HOUR_OF_DAY, hourInt)
        calendar.set(Calendar.MINUTE, minInt)

        setAlarm(calendar)
    }

    private fun setAlarm(c : Calendar) {
        val alarmManager : AlarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val receiverIntent = Intent(activity, AlertReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(activity?.applicationContext, 0, receiverIntent, FLAG_IMMUTABLE)

        if(c.before(Calendar.getInstance())){
            c.add(Calendar.DATE, 1)
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            c.timeInMillis,
            pendingIntent
        )
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

}