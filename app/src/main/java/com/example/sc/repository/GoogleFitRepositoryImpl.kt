package com.example.sc.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.sc.model.GoogleFitDaily
import com.example.sc.model.GoogleFitMonthly
import com.example.sc.model.GoogleFitWeekly
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.request.DataReadRequest
import java.util.*
import java.util.concurrent.TimeUnit

class GoogleFitRepositoryImpl() : GoogleFitRepository {

    private val fitnessOptions = FitnessOptions.builder()
        .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
        .build()

    override fun getDailyFitnessData(context: Context): MutableLiveData<GoogleFitDaily> {
        val dailyFitnessLiveData = MutableLiveData<GoogleFitDaily>()

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val startTime = calendar.timeInMillis

        val readRequest = DataReadRequest.Builder()
            .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, System.currentTimeMillis(), TimeUnit.MILLISECONDS)
            .build()

        Fitness.getHistoryClient(context, getGoogleAccount(context))
            .readData(readRequest)
            .addOnSuccessListener { data ->
                val buckets = data.buckets
                val bucket = if (buckets.isNotEmpty()) buckets[0] else null
                var stepCount = 0
                bucket?.dataSets?.forEach { dataSet ->
                    dataSet.dataPoints.forEach { dataPoint ->
                        when (dataPoint.dataType) {
                            DataType.TYPE_STEP_COUNT_DELTA -> {
                                stepCount = dataPoint.getValue(Field.FIELD_STEPS).asInt()
                            }

                        }
                    }
                }
                val dailyFitness = GoogleFitDaily(stepCount)
                dailyFitnessLiveData.postValue(dailyFitness)
            }
            .addOnFailureListener { exception ->
                // Handle error
            }

        return dailyFitnessLiveData
    }


    override fun getWeeklyFitnessData(context: Context): MutableLiveData<GoogleFitWeekly> {
        val weeklyFitnessLiveData = MutableLiveData<GoogleFitWeekly>()

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        val startTime = calendar.timeInMillis

        // Build data read request for the last 7 days
        val readRequest = DataReadRequest.Builder()
            .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, System.currentTimeMillis(), TimeUnit.MILLISECONDS)
            .build()
        Fitness.getHistoryClient(context, getGoogleAccount(context))
            .readData(readRequest)
            .addOnSuccessListener { data ->
                val buckets = data.buckets
                val dailyFitnessList = mutableListOf<GoogleFitDaily>()

                // Process each bucket of fitness data
                buckets.forEach { bucket ->
                    var stepCount = 0

                    bucket.dataSets.forEach { dataSet ->
                        dataSet.dataPoints.forEach { dataPoint ->
                            when (dataPoint.dataType) {
                                DataType.TYPE_STEP_COUNT_DELTA -> {
                                    stepCount = dataPoint.getValue(Field.FIELD_STEPS).asInt()
                                }
                            }
                        }
                    }

                    val dailyFitness = GoogleFitDaily(stepCount)
                    dailyFitnessList.add(dailyFitness)
                }

                val weeklyFitness = GoogleFitWeekly(dailyFitnessList)
                weeklyFitnessLiveData.postValue(weeklyFitness)
            }
            .addOnFailureListener { exception ->
                // Handle error
            }

        return weeklyFitnessLiveData
    }

    override fun getMonthlyFitnessData(context: Context): MutableLiveData<GoogleFitMonthly> {
        val monthlyFitnessLiveData = MutableLiveData<GoogleFitMonthly>()

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.add(Calendar.DAY_OF_YEAR, -30)
        val startTime = calendar.timeInMillis

        // Build data read request for the last 30 days
        val readRequest = DataReadRequest.Builder()
            .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, System.currentTimeMillis(), TimeUnit.MILLISECONDS)
            .build()
        Fitness.getHistoryClient(context, getGoogleAccount(context))
            .readData(readRequest)
            .addOnSuccessListener { data ->
                val buckets = data.buckets
                val dailyFitnessList = mutableListOf<GoogleFitDaily>()

                // Process each bucket of fitness data
                buckets.forEach { bucket ->
                    var stepCount = 0

                    bucket.dataSets.forEach { dataSet ->
                        dataSet.dataPoints.forEach { dataPoint ->
                            when (dataPoint.dataType) {
                                DataType.TYPE_STEP_COUNT_DELTA -> {
                                    stepCount = dataPoint.getValue(Field.FIELD_STEPS).asInt()
                                }
                            }
                        }
                    }

                    val dailyFitness = GoogleFitDaily(stepCount)
                    dailyFitnessList.add(dailyFitness)
                }

                val monthlyFitness = GoogleFitMonthly(dailyFitnessList)
                monthlyFitnessLiveData.postValue(monthlyFitness)
            }
            .addOnFailureListener { exception ->
                // Handle error
            }

        return monthlyFitnessLiveData
    }

    override fun getGoogleAccount(context: Context): GoogleSignInAccount  =  GoogleSignIn.getAccountForExtension(context,fitnessOptions)
}