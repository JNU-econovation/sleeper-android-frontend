package com.example.sleeper_frontend



import android.content.Context
import android.os.Bundle
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.sleeper_frontend.databinding.FragmentHomeBBinding
import com.google.android.material.bottomnavigation.BottomNavigationView


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

        binding.btnStopSleep.setOnClickListener {
            val homeFragment = HomeFragment()
            val transaction : FragmentTransaction = requireFragmentManager().beginTransaction()
            transaction.replace(R.id.fl_container, homeFragment).commit()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mainActivity.hideBottomNavigation(false)
    }


}