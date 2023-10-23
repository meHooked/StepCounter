package com.example.sc

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.sc.GoalsRepository
import com.example.sc.model.*


class GoalsViewModel(application: Application) : AndroidViewModel(application) {
    var repository: GoalsRepository
    var dailyGoal: LiveData<Int>


    init {
        repository = GoalsRepository(application)
        dailyGoal = repository.getDaily
    }

    fun updateDaily(daily: GoalDaily){
        repository.updateDaily(daily)
    }

    fun updateWeekly(weekly: GoalWeekly){
        repository.updateWeekly(weekly)
    }

    fun updateMonthly(monthly: GoalMonthly){
        repository.updateMonthly(monthly)
    }

    fun getDaily(): LiveData<Int> {
        return repository.getDaily()
    }

    fun getWeekly(): LiveData<Int> {
        return repository.getWeekly()
    }

    fun getMonthly(): LiveData<Int> {
        return repository.getMonthly()
    }


}

