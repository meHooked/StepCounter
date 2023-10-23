package com.example.sc

import android.content.res.Configuration
import android.os.Bundle
import android.text.TextUtils.replace
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
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


        binding?.apply {
            // Set up the button click listeners

            bDaily.setOnClickListener {
                activity?.supportFragmentManager?.beginTransaction()?.apply {
                    val dailyFragment = DailyFragment()
                    // val mBundle = Bundle()
                    //val stepsD = readData()
                    // Log.i(TAG, "stepsD: $stepsD")

                    //mBundle.putString("mText", sl.text.toString())
                    // dailyFragment.arguments = mBundle
                    replace(R.id.nav_host_fragment, dailyFragment)
                    addToBackStack(null)
                    commit()
                }
            }

                bWeekly.setOnClickListener {
                    activity?.supportFragmentManager?.beginTransaction()?.apply {
                        val weeklyFragment = WeeklyFragment()
                        // val mBundle = Bundle()
                        //val stepsD = readData()
                        // Log.i(TAG, "stepsD: $stepsD")

                        //mBundle.putString("mText", sl.text.toString())
                        // dailyFragment.arguments = mBundle
                        replace(R.id.nav_host_fragment, weeklyFragment)
                        addToBackStack(null)
                        commit()
                        // Log.i(TAG, "steps: $stepsD")
                    }
                }

                    bMonthly.setOnClickListener {
                        activity?.supportFragmentManager?.beginTransaction()?.apply {
                            val monthlyFragment = MonthlyFragment()
                            // val mBundle = Bundle()
                            //val stepsD = readData()
                            // Log.i(TAG, "stepsD: $stepsD")

                            //mBundle.putString("mText", sl.text.toString())
                            // dailyFragment.arguments = mBundle
                            replace(R.id.nav_host_fragment, monthlyFragment)
                            addToBackStack(null)
                            commit()
                            // Log.i(TAG, "steps: $stepsD")
                        }
                    }


                    }
                }

    override fun onDestroyView() {
          super.onDestroyView()
          binding = null
         }



}







