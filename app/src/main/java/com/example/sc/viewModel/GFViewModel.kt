package com.example.sc.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sc.model.GoogleFitDaily
import com.example.sc.model.GoogleFitMonthly
import com.example.sc.model.GoogleFitWeekly
import com.example.sc.repository.GoogleFitRepository
import com.example.sc.repository.GoogleFitRepositoryImpl

class GFViewModel: ViewModel() {
    val gfRepository: GoogleFitRepository = GoogleFitRepositoryImpl()

    fun getDailyFitnessData(context: Context): LiveData<GoogleFitDaily> {
        var dailyFitnessLiveData = gfRepository.getDailyFitnessData(context)
        return dailyFitnessLiveData
    }

    fun getWeeklyFitnessData(context: Context): MutableLiveData<GoogleFitWeekly> {
        var weeklyFitnessLiveData = gfRepository.getWeeklyFitnessData(context)
        return weeklyFitnessLiveData
    }

    fun getMonthlyFitnessData(context: Context): MutableLiveData<GoogleFitMonthly> {
        var monthlyFitnessLiveData = gfRepository.getMonthlyFitnessData(context)
        return monthlyFitnessLiveData
    }
}