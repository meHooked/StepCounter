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
import androidx.lifecycle.*
import com.example.sc.R
import com.example.sc.databinding.FragmentDailyBinding
import com.example.sc.model.GoalDaily
import com.example.sc.viewModel.GFViewModel
import com.example.sc.viewModel.GoalsViewModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate


class DailyFragment : Fragment() {

    private lateinit var binding: FragmentDailyBinding
    private val goalsViewModel: GoalsViewModel by viewModels()
    private val gfViewModel: GFViewModel by viewModels()
    private lateinit var list: ArrayList<BarEntry>



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentDailyBinding.inflate(inflater, container, false)//inflates fragment
        //val ds = getString(R.string.steps_daily)
        //binding.sampleLogview.text = "aaaa"

       gfViewModel.getDailyFitnessData(binding.root.context)//gets dailysteps from GoogleFit, sets them to textview
            .observe(viewLifecycleOwner) { DailyFitness ->
                val ds = getString(R.string.steps_daily)
                binding.sampleLogview.text =
                    String.format(ds, DailyFitness.dailyStepsMade.toString())
            }


        goalsViewModel.getDaily().observe(this) {//gets daily goal from database,sets it to textview
            val dg = getString(R.string.goal_daily)
            binding.tvSavedGoalSteps.text = String.format(dg, it.toString())
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val ds = getString(R.string.steps_daily)

        binding.bSaveDailyGoal.setOnClickListener { //saves entered daily goal to database

            gfViewModel.getDailyFitnessData(view.context)//gets dailysteps from GoogleFit, sets them to textview
                .observe(viewLifecycleOwner) { DailyFitness ->
                    binding.sampleLogview.text = String.format(ds, DailyFitness.dailyStepsMade.toString())

                }
            if (binding.etDailyGoalSteps.length() == 0) {
                binding.etDailyGoalSteps.error = "You need to enter daily goal"
            } else {
                val dailyGoal = binding.etDailyGoalSteps.text.toString().toInt()
                goalsViewModel.updateDaily(GoalDaily(1, dailyGoal))
                binding.etDailyGoalSteps.text?.clear()
                it.hideKeyboard()
                returnChart()
                list.clear()
            }

        }


        binding.bCheckDailySteps.setOnClickListener {
         gfViewModel.getDailyFitnessData(view.context)//gets dailysteps from GoogleFit, sets them to textview
                .observe(viewLifecycleOwner) { DailyFitness ->
                    binding.sampleLogview.text = String.format(ds, DailyFitness.dailyStepsMade.toString())
                }

           returnChart()

        }


    }

    private fun returnChart(): ArrayList<BarEntry> {//returns list that contains goals and steps taken

        list = ArrayList()

        goalsViewModel.getDaily().observe(this) {//gets goal from database, adds it to chart
            val yGoalD = it.toFloat()
            list.add(BarEntry(1f, yGoalD))

        }

        this.context?.let {//gets daily steps from google fit, adds them to chart
            gfViewModel.getDailyFitnessData(it)
                .observe(viewLifecycleOwner, Observer { DailyFitness ->
                    val yDailyStepsTaken = DailyFitness.dailyStepsMade.toString().toFloat()
                    list.add(BarEntry(2f, yDailyStepsTaken))

                    val dataSet = BarDataSet(list, "")//creates dataset for chart
                    dataSet.setColors(
                        ColorTemplate.COLORFUL_COLORS,
                        255
                    ) //sets chart color and opacity
                    dataSet.valueTextSize = 12f  //sets text size for chart

                    //if dark mode is on, changes text color in chart
                    if (isDarkModeOn()) {
                        dataSet.valueTextColor = Color.WHITE
                        binding.idBarChart.description.textColor = Color.WHITE
                        binding.idBarChart.xAxis.textColor = Color.WHITE
                        binding.idBarChart.axisLeft.textColor = Color.WHITE
                    }

                    val barData = BarData(dataSet)
                    binding.idBarChart.setFitBars(true) //adds half of the bar width to each side to allow the bars to be fully displayed
                    binding.idBarChart.data = barData  //sets data for chart
                    binding.idBarChart.description.text =
                        "Daily goals/steps"  //sets chart description
                    binding.idBarChart.description.textSize =
                        12f //sets font size for chart description
                    binding.idBarChart.description.yOffset = 500f  //sets y-axis fordescription


                    barData.setValueFormatter(object :
                        ValueFormatter() { //changes float values on chart to int values
                        override fun getFormattedValue(value: Float): String {
                            return "${value.toInt()}"
                        }
                    })

                    binding.idBarChart.data.notifyDataChanged() //notifies chart when data is changed
                    binding.idBarChart.notifyDataSetChanged() //notifies chart when dataset is changed
                    binding.idBarChart.animateY(1000) //duration of animation on y-axis
                    binding.idBarChart.legend.isEnabled = false
                })
        }

        binding.idBarChart.xAxis.position = XAxis.XAxisPosition.BOTTOM //sets position of x-axis
        binding.idBarChart.axisLeft.axisMinimum = 0f    //min value of left y-axis
        binding.idBarChart.axisRight.isEnabled = false  //y-axis on right is disabled
        binding.idBarChart.axisRight.setDrawGridLines(false)  //disabled gridlines drawing for right y-axis

        val xAxisLabels = listOf("", "Goals", "Steps")
        binding.idBarChart.xAxis.valueFormatter =
            IndexAxisValueFormatter(xAxisLabels)    //sets labels on x-axis

        binding.idBarChart.xAxis.setDrawLabels(true)   //draws labels on x-axis

        return list

    }

    //hides keyboard
    fun View.hideKeyboard() {
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









