package com.example.sc.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*

@Dao
interface GoalsDao {
   // val dGoal: MutableLiveData<GoalDaily>

    @Insert
    fun insert(goals: Goals)

    //  @Update
    // fun update(goals: Goals)

    //@Delete
    // fun delete(goals: Goals)

    @Update(entity = Goals::class)
    fun updateDaily(daily: GoalDaily)

    @Update(entity = Goals::class)
    fun updateWeekly(weekly: GoalWeekly)

    @Update(entity = Goals::class)
    fun updateMonthly(monthly: GoalMonthly)



    @Query("SELECT dailyGoal FROM goals")
    fun getDailyGoal(): LiveData<Int>

    @Query("SELECT weeklyGoal FROM goals")
    fun getWeeklyGoal(): LiveData<Int>

    @Query("SELECT monthlyGoal FROM goals")
    fun getMonthlyGoal(): LiveData<Int>


}