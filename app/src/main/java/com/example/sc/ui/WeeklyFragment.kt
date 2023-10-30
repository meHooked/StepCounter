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
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.sc.R
import com.example.sc.databinding.FragmentWeeklyBinding
import com.example.sc.model.GoalWeekly
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

class WeeklyFragment : Fragment() {

    private lateinit var binding: FragmentWeeklyBinding
    private val goalsViewModel: GoalsViewModel by viewModels()
    private val gfViewModel: GFViewModel by viewModels()
    private lateinit var list: ArrayList<BarEntry>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentWeeklyBinding.inflate(inflater, container, false)

        gfViewModel.getWeeklyFitnessData(binding.root.context)
            .observe(viewLifecycleOwner) { WeeklyFitness ->
                val summedWeekly = WeeklyFitness.weeklyStepsMade.sumOf { it.dailyStepsMade }
                val ws = getString(R.string.steps_weekly)
                binding.sampleLogview2.text = String.format(ws, summedWeekly)
                val averageWeekly = summedWeekly/7.00
                val avgWRound = (averageWeekly * 100.0).roundToInt() / 100.0
                val wa = getString(R.string.avg_weekly)
                binding.tvWeeklyAverage.text = String.format(wa, avgWRound)
            }

        goalsViewModel.getWeekly().observe(this) {
            val wg = getString(R.string.goal_weekly)
            binding.tvSavedGoalSteps.text = String.format(wg, it.toString())
        }

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bSaveWeeklyGoal.setOnClickListener {
            if (binding.etWeeklyGoalSteps.length() == 0){
                binding.etWeeklyGoalSteps.error = "You need to enter weekly goal"
            } else {
                val weeklyGoal = binding.etWeeklyGoalSteps.text.toString().toInt()
                goalsViewModel.updateWeekly(GoalWeekly(1, weeklyGoal))
                binding.etWeeklyGoalSteps.text.clear()
                it.hideKeyboard()
                returnChart()
                list.clear()
            }

        }

        binding.bCheckWeeklySteps.setOnClickListener {
            gfViewModel.getWeeklyFitnessData(view.context)
                .observe(viewLifecycleOwner) { WeeklyFitness ->
                    val summedWeekly = WeeklyFitness.weeklyStepsMade.sumOf { it.dailyStepsMade }
                    val ws = getString(R.string.steps_weekly)
                    binding.sampleLogview2.text = String.format(ws, summedWeekly)
                    val averageWeekly = summedWeekly/7.00
                    val avgWRound = (averageWeekly * 100.0).roundToInt() / 100.0
                    val wa = getString(R.string.avg_weekly)
                    binding.tvWeeklyAverage.text = String.format(wa, avgWRound)
                    it.hideKeyboard()
                }

            returnChart()

        }


    }

    fun returnChart(): ArrayList<BarEntry> {

        list = ArrayList()

        goalsViewModel.getWeekly().observe(this) {
            var yGoalW = it.toFloat()
            list.add(BarEntry(1f, yGoalW))
        }

        this.context?.let {
            gfViewModel.getWeeklyFitnessData(it)
                .observe(viewLifecycleOwner, Observer { WeeklyFitness ->
                    val summedWeekly =
                        WeeklyFitness.weeklyStepsMade.sumOf { it.dailyStepsMade }.toString()
                            .toFloat()
                    list.add(BarEntry(2f, summedWeekly))
                    val dataSet = BarDataSet(list, "")
                    dataSet.setColors(ColorTemplate.COLORFUL_COLORS, 255)
                    dataSet.valueTextColor = Color.BLACK
                    dataSet.valueTextSize = 12f

                    if (isDarkModeOn()){
                        dataSet.valueTextColor = Color.WHITE
                        binding.idBarChartWeekly.description.textColor = Color.WHITE
                        binding.idBarChartWeekly.xAxis.textColor = Color.WHITE
                        binding.idBarChartWeekly.axisLeft.textColor = Color.WHITE
                    }

                    val barData = BarData(dataSet)
                    binding.idBarChartWeekly.setFitBars(true)
                    binding.idBarChartWeekly.data = barData
                    binding.idBarChartWeekly.description.text = "Weekly goal/steps"
                    binding.idBarChartWeekly.description.textSize = 12f
                    binding.idBarChartWeekly.description.yOffset = 500f

                    barData.setValueFormatter(object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return "${value.toInt()}"
                        }
                    })

                    binding.idBarChartWeekly.data.notifyDataChanged()
                    binding.idBarChartWeekly.notifyDataSetChanged()
                    binding.idBarChartWeekly.animateY(1000)
                    binding.idBarChartWeekly.legend.isEnabled = false
                })
        }

        binding.idBarChartWeekly.xAxis.position = XAxis.XAxisPosition.BOTTOM
        binding.idBarChartWeekly.axisLeft.axisMinimum = 0f
        binding.idBarChartWeekly.axisRight.isEnabled = false
        binding.idBarChartWeekly.axisRight.setDrawGridLines(false)


        val xAxisLabels = listOf("", "Goals", "Steps")
        binding.idBarChartWeekly.xAxis.valueFormatter = IndexAxisValueFormatter(xAxisLabels)


        binding.idBarChartWeekly.xAxis.setDrawLabels(true)


        return list

    }
    fun View.hideKeyboard() {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }

    fun isDarkModeOn(): Boolean {
        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isDarkModeOn = nightModeFlags == Configuration.UI_MODE_NIGHT_YES
        return isDarkModeOn
    }
}
