package com.example.sc

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.sc.databinding.FragmentMonthlyBinding
import com.example.sc.model.GoalMonthly
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate

class MonthlyFragment : Fragment() {

    private var binding: FragmentMonthlyBinding? = null
    private val goalsViewModel: GoalsViewModel by activityViewModels()
    private val gfViewModel: GFViewModel by viewModels()
    private lateinit var textViewSteps: TextView
    lateinit var barChart: BarChart
    private lateinit var list: ArrayList<BarEntry>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflates the custom fragment layout
        val rootView = inflater.inflate(R.layout.fragment_monthly, container, false)
        //requireContext()

        val monthlySteps: TextView? = view?.findViewById(R.id.tvSavedGoalSteps)

        barChart = rootView.findViewById(R.id.idBarChartMonthly)

        textViewSteps = rootView.findViewById(R.id.sample_logview3)

        gfViewModel.getMonthlyFitnessData(rootView.context)
            .observe(viewLifecycleOwner, Observer { MonthlyFitness ->
                val summedMonthly = MonthlyFitness.monthlyStepsMade.sumOf { it.dailyStepsMade }
                textViewSteps.text = summedMonthly.toString()
            })

        goalsViewModel.getMonthly().observe(this) {
            monthlySteps?.text = it.toString()
        }

       // returnChart()

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bGetMSteps = view.findViewById<Button>(R.id.bCheckMonthlySteps)
        val bSaveMonthlyGoal = view.findViewById<Button>(R.id.bSaveMonthlyGoal)
        val etMonthlyG = view.findViewById<EditText>(R.id.etMonthlyGoalSteps)
        val goal = view.findViewById<TextView>(R.id.tvSavedGoalSteps)

        goalsViewModel.getMonthly().observe(this) {
            goal.text = it.toString()
        }

        bSaveMonthlyGoal.setOnClickListener {
            val monthlyGoal = etMonthlyG.text.toString().toInt()
            goalsViewModel.updateMonthly(GoalMonthly(1, monthlyGoal))
           etMonthlyG.text?.clear()
            it.hideKeyboard()
            returnChart()
            list.clear()
        }
        bGetMSteps.setOnClickListener {
            returnChart()
        }

    }

    fun returnChart(): ArrayList<BarEntry> {

        list = ArrayList()

        goalsViewModel.getMonthly().observe(this) {
            var yGoalW = it.toFloat()
            list.add(BarEntry(1f, yGoalW))
            barChart.animateY(0)
        }


        context?.let {
            gfViewModel.getMonthlyFitnessData(it)
                .observe(viewLifecycleOwner, Observer { MonthlyFitness ->
                    val summedMonthly = MonthlyFitness.monthlyStepsMade.sumOf { it.dailyStepsMade }
                        .toFloat()
                    list.add(BarEntry(2f, summedMonthly))
                    val dataSet = BarDataSet(list, "")
                    dataSet.setColors(ColorTemplate.COLORFUL_COLORS, 255)
                    //dataSet.valueTextColor = Color.BLACK
                    dataSet.valueTextSize = 12f

                    if (isDarkModeOn()){
                        dataSet.valueTextColor = Color.WHITE
                        barChart.description.textColor = Color.WHITE
                        barChart.xAxis.textColor = Color.WHITE
                        barChart.axisLeft.textColor = Color.WHITE

                    }

                    val barData = BarData(dataSet)
                    barChart.setFitBars(true)
                    barChart.data = barData
                    barChart.description.text = "Monthly goal/steps"
                    barChart.description.textSize = 12f
                    barChart.description.yOffset = 500f




                    barData.setValueFormatter(object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return "${value.toInt()}"
                        }
                    })

                    barChart.data.notifyDataChanged()
                    barChart.notifyDataSetChanged()
                    barChart.animateY(1000)
                    barChart.legend.isEnabled = false
                })

        }
        barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        barChart.axisLeft.axisMinimum = 0f
        barChart.axisRight.isEnabled = false
        barChart.axisRight.setDrawGridLines(false)

        val xAxisLabels = listOf("", "Goals", "Steps")
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(xAxisLabels)

        //barChart.xAxis.setCenterAxisLabels(true)
        barChart.xAxis.setDrawLabels(true)

       // list.clear()
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



