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
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.sc.databinding.FragmentWeeklyBinding
import com.example.sc.model.GoalWeekly
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate

class WeeklyFragment : Fragment() {

    private var binding: FragmentWeeklyBinding? = null
    private val goalsViewModel: GoalsViewModel by viewModels()
    private val gfViewModel: GFViewModel by viewModels()
    private lateinit var weeklyGoal: TextView

    private lateinit var textViewSteps: TextView
    lateinit var barChart: BarChart
    private lateinit var list: ArrayList<BarEntry>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflates the custom fragment layout
        val rootView = inflater.inflate(R.layout.fragment_weekly, container, false)
        requireContext()




        barChart = rootView.findViewById(R.id.idBarChartWeekly)

        textViewSteps = rootView.findViewById(R.id.sample_logview2)

        gfViewModel.getWeeklyFitnessData(rootView.context)
            .observe(viewLifecycleOwner) { WeeklyFitness ->
                val summedWeekly = WeeklyFitness.weeklyStepsMade.sumOf { it.dailyStepsMade }
                textViewSteps.text = WeeklyFitness.weeklyStepsMade.toString()

                //  Toast.makeText(context, text, duration).show()

                textViewSteps.text = summedWeekly.toString()
            }


        weeklyGoal = rootView.findViewById(R.id.tvSavedGoalSteps)
        // val weeklySteps: TextView? = view?.findViewById(R.id.tvSavedGoalSteps)
        goalsViewModel.getWeekly().observe(this) {
            weeklyGoal?.text = it.toString()
        }

        returnChart()

        return rootView

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bSaveWeeklyGoal = view.findViewById<Button>(R.id.bSaveWeeklyGoal)
        val etWeeklyG = view.findViewById<EditText>(R.id.etWeeklyGoalSteps)
        val goal = view.findViewById<TextView>(R.id.tvSavedGoalSteps)



        bSaveWeeklyGoal.setOnClickListener {
            val weeklyGoal = etWeeklyG.text.toString().toInt()
            goalsViewModel.updateWeekly(GoalWeekly(1, weeklyGoal))
            etWeeklyG.text.clear()
            it.hideKeyboard()
            list.clear()
            returnChart()
        }


    }

    fun returnChart(): ArrayList<BarEntry> {

        // var
        list = ArrayList()

        goalsViewModel.getWeekly().observe(this) {
            var yGoalW = it.toFloat()
            list.add(BarEntry(1f, yGoalW))
            barChart.animateY(0)
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

                    //if dark mode is on, changes text color in chart
                    if (isDarkModeOn()){
                        dataSet.valueTextColor = Color.WHITE
                        barChart.description.textColor = Color.WHITE
                        barChart.xAxis.textColor = Color.WHITE
                        barChart.axisLeft.textColor = Color.WHITE
                    }

                    val barData = BarData(dataSet)
                    barChart.setFitBars(true)
                    barChart.data = barData
                    barChart.description.text = "Weekly goal/steps"
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

        list.clear()
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
