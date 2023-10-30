package com.example.sc.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.sc.R
import com.example.sc.databinding.FragmentStartBinding


class StartFragment : Fragment() {

    private var binding: FragmentStartBinding? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val fragmentBinding = FragmentStartBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.ivImage?.setImageResource(R.drawable.purple)
        binding?.apply {
            // Set up the button click listeners

            bDaily.setOnClickListener {
                activity?.supportFragmentManager?.beginTransaction()?.apply {
                    val dailyFragment = DailyFragment()
                    replace(R.id.nav_host_fragment, dailyFragment)
                    addToBackStack(null)
                    commit()
                }
            }

            bWeekly.setOnClickListener {
                activity?.supportFragmentManager?.beginTransaction()?.apply {
                    val weeklyFragment = WeeklyFragment()
                    replace(R.id.nav_host_fragment, weeklyFragment)
                    addToBackStack(null)
                    commit()
                }
            }

            bMonthly.setOnClickListener {
                activity?.supportFragmentManager?.beginTransaction()?.apply {
                    val monthlyFragment = MonthlyFragment()
                    replace(R.id.nav_host_fragment, monthlyFragment)
                    addToBackStack(null)
                    commit()

                }
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }


}







