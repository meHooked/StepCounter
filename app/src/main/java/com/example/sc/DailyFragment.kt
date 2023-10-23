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
import androidx.lifecycle.*
import com.example.sc.databinding.FragmentDailyBinding
import com.example.sc.model.GoalDaily
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate


class DailyFragment : Fragment() {

    private var binding: FragmentDailyBinding? = null
    private val goalsViewModel: GoalsViewModel by viewModels()
    private val gfViewModel: GFViewModel by viewModels()
    private lateinit var textViewSteps: TextView
    private lateinit var goal: TextView
    lateinit var barChart: BarChart
    private lateinit var list: ArrayList<BarEntry>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val rootView =
            inflater.inflate(R.layout.fragment_daily, container, false)//inflates fragment

        barChart = rootView.findViewById(R.id.idBarChart)

        textViewSteps = rootView.findViewById(R.id.sample_logview)
        gfViewModel.getDailyFitnessData(rootView.context)//gets dailysteps from GoogleFit, sets them to textview
            .observe(viewLifecycleOwner) { DailyFitness ->
                textViewSteps.text = DailyFitness.dailyStepsMade.toString()

            }

        goal = rootView.findViewById<TextView>(R.id.tvSavedGoalSteps)
        goalsViewModel.getDaily().observe(this) {//gets daily goal from database,sets it to textview
            goal.text = it.toString()
        }

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bGetDSteps = view.findViewById<Button>(R.id.bCheckDailySteps)
        val bSaveGoal = view.findViewById<Button>(R.id.bSaveDailyGoal)
        val etDailyG = view.findViewById<EditText>(R.id.etDailyGoalSteps)
        // val goal = view.findViewById<TextView>(R.id.tvSavedGoalSteps)
        // val stepsTakenD = view.findViewById<TextView>(R.id.sample_logview)


        bSaveGoal.setOnClickListener {//saves entered daily goal to database
            val dailyGoal = etDailyG.text.toString().toInt()
            goalsViewModel.updateDaily(GoalDaily(1, dailyGoal))
            etDailyG.text?.clear()
            it.hideKeyboard()
            returnChart()
            list.clear()
        }

        bGetDSteps.setOnClickListener {
            returnChart()
        }


    }

    private fun returnChart(): ArrayList<BarEntry> {//returns chart of goals and steps taken

        list = ArrayList()

        goalsViewModel.getDaily().observe(this) {//gets goal from database, adds it to chart
            val yGoalD = it.toFloat()
            list.add(BarEntry(1f, yGoalD))
            barChart.animateY(1000)

        }

        this.context?.let {
            gfViewModel.getDailyFitnessData(it)
                .observe(viewLifecycleOwner, Observer { DailyFitness ->
                    textViewSteps.text = DailyFitness.dailyStepsMade.toString()
                    val yDailyStepsTaken = DailyFitness.dailyStepsMade.toString().toFloat()
                    list.add(BarEntry(2f, yDailyStepsTaken))

                    val dataSet = BarDataSet(list, "")
                    dataSet.setColors(ColorTemplate.COLORFUL_COLORS, 255)
                    dataSet.valueTextSize = 12f

                    //if dark mode is on, changes text color in chart
                    if (isDarkModeOn()) {
                        dataSet.valueTextColor = Color.WHITE
                        barChart.description.textColor = Color.WHITE
                        barChart.xAxis.textColor = Color.WHITE
                        barChart.axisLeft.textColor = Color.WHITE
                    }

                    val barData = BarData(dataSet)
                    barChart.setFitBars(true)
                    barChart.data = barData
                    barChart.description.text = "Daily goals/steps"
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

        barChart.xAxis.setDrawLabels(true)

        return list

    }

    fun View.hideKeyboard() { //hides keyboard
        val inputManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }

    //checks if app is in dark mode and returns boolean
    fun isDarkModeOn(): Boolean {
        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isDarkModeOn = nightModeFlags == Configuration.UI_MODE_NIGHT_YES
        return isDarkModeOn
    }

}









