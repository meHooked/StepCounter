package com.example.sc.ui

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.sc.R
import com.example.sc.databinding.FragmentMonthlyBinding
import com.example.sc.model.GoalMonthly
import com.example.sc.viewModel.GFViewModel
import com.example.sc.viewModel.GoalsViewModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import kotlin.math.roundToInt

class MonthlyFragment : Fragment() {

    private lateinit var binding: FragmentMonthlyBinding
    private val goalsViewModel: GoalsViewModel by activityViewModels()
    private val gfViewModel: GFViewModel by viewModels()

    private lateinit var list: ArrayList<BarEntry>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMonthlyBinding.inflate(inflater, container, false)

        gfViewModel.getMonthlyFitnessData(binding.root.context)
            .observe(viewLifecycleOwner, Observer { MonthlyFitness ->
                val summedMonthly = MonthlyFitness.monthlyStepsMade.sumOf { it.dailyStepsMade }
                val ms = getString(R.string.steps_monthly)
                binding.sampleLogview3.text = String.format(ms, summedMonthly)
                val averageMonthly = summedMonthly / 30.00
                val avgMRound = (averageMonthly * 100.0).roundToInt() / 100.0
                val wa = getString(R.string.avg_monthly)
                binding.tvMonthlyAverage.text = String.format(wa, avgMRound)
            })

        goalsViewModel.getMonthly().observe(this) {
            val mg = getString(R.string.goal_monthly)
            binding.tvSavedGoalSteps?.text = String.format(mg, it.toString())
        }

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bSaveMonthlyGoal.setOnClickListener {
            if (binding.etMonthlyGoalSteps.length() == 0) {
                binding.etMonthlyGoalSteps.error = "You need to enter monthly goal"
            } else {
                val monthlyGoal = binding.etMonthlyGoalSteps.text.toString().toInt()
                goalsViewModel.updateMonthly(GoalMonthly(1, monthlyGoal))
                binding.etMonthlyGoalSteps.text?.clear()
                it.hideKeyboard()
                binding.idBarChartMonthly.notifyDataSetChanged()
            }
            returnChart()
            list.clear()
        }

        binding.bCheckMonthlySteps.setOnClickListener {
            gfViewModel.getMonthlyFitnessData(view.context)
                .observe(viewLifecycleOwner, Observer { MonthlyFitness ->
                    val summedMonthly = MonthlyFitness.monthlyStepsMade.sumOf { it.dailyStepsMade }
                    val ms = getString(R.string.steps_monthly)
                    binding.sampleLogview3.text = String.format(ms, summedMonthly)
                    val averageMonthly = summedMonthly / 30.00
                    val avgMRound = (averageMonthly * 100.0).roundToInt() / 100.0
                    val wa = getString(R.string.avg_monthly)
                    binding.tvMonthlyAverage.text = String.format(wa, avgMRound)
                    it.hideKeyboard()
                })

            returnChart()
        }

    }

    fun returnChart(): ArrayList<BarEntry> {

        list = ArrayList()

        goalsViewModel.getMonthly().observe(this) {
            var yGoalW = it.toFloat()
            list.add(BarEntry(1f, yGoalW))
        }


        context?.let {
            gfViewModel.getMonthlyFitnessData(it)
                .observe(viewLifecycleOwner, Observer { MonthlyFitness ->
                    val summedMonthly = MonthlyFitness.monthlyStepsMade.sumOf { it.dailyStepsMade }
                        .toFloat()
                    list.add(BarEntry(2f, summedMonthly))
                    val dataSet = BarDataSet(list, "")
                    dataSet.setColors(ColorTemplate.COLORFUL_COLORS, 255)
                    dataSet.valueTextSize = 12f

                    if (isDarkModeOn()) {
                        dataSet.valueTextColor = Color.WHITE
                        binding.idBarChartMonthly.description.textColor = Color.WHITE
                        binding.idBarChartMonthly.xAxis.textColor = Color.WHITE
                        binding.idBarChartMonthly.axisLeft.textColor = Color.WHITE

                    }

                    val barData = BarData(dataSet)
                    binding.idBarChartMonthly.setFitBars(true)
                    binding.idBarChartMonthly.data = barData
                    binding.idBarChartMonthly.description.text = "Monthly goal/steps"
                    binding.idBarChartMonthly.description.textSize = 12f
                    binding.idBarChartMonthly.description.yOffset = 500f


                    barData.setValueFormatter(object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return "${value.toInt()}"
                        }
                    })

                    binding.idBarChartMonthly.data.notifyDataChanged()
                    binding.idBarChartMonthly.notifyDataSetChanged()
                    binding.idBarChartMonthly.animateY(1000)
                    binding.idBarChartMonthly.legend.isEnabled = false
                })

        }
        binding.idBarChartMonthly.xAxis.position = XAxis.XAxisPosition.BOTTOM
        binding.idBarChartMonthly.axisLeft.axisMinimum = 0f
        binding.idBarChartMonthly.axisRight.isEnabled = false
        binding.idBarChartMonthly.axisRight.setDrawGridLines(false)

        val xAxisLabels = listOf("", "Goals", "Steps")
        binding.idBarChartMonthly.xAxis.valueFormatter = IndexAxisValueFormatter(xAxisLabels)

        binding.idBarChartMonthly.xAxis.setDrawLabels(true)

        return list
    }

    fun View.hideKeyboard() {
        val inputManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }

    fun isDarkModeOn(): Boolean {
        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isDarkModeOn = nightModeFlags == Configuration.UI_MODE_NIGHT_YES

        return isDarkModeOn
    }

}



