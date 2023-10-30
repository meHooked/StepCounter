package com.example.sc.repository

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import com.example.sc.model.*

class GoalsRepository(application: Application) {
    var gDaoDao: GoalsDao
     val getDaily: LiveData<Int>
    val getWeekly: LiveData<Int>
    val getMonthly: LiveData<Int>

    init {
        val database = GoalsDatabase.getInstance(application)
        gDaoDao = database.goalsDao()
         getDaily= gDaoDao.getDailyGoal()
        getWeekly = gDaoDao.getWeeklyGoal()
        getMonthly = gDaoDao.getMonthlyGoal()

        //allUsers = userDao.getAllUsers()
    }

    /* fun update(user: User) {
         UpdateUserAsyncTask(userDao).execute(user)
     }*/
    fun updateDaily(daily: GoalDaily) {
        UpdateDailyAsyncTask(gDaoDao).execute(daily)

    }

    fun updateWeekly(weekly: GoalWeekly) {
        UpdateWeeklyAsyncTask(gDaoDao).execute(weekly)
    }

    fun updateMonthly(monthly: GoalMonthly) {
        UpdateMonthlyAsyncTask(gDaoDao).execute(monthly)
    }

    fun getDaily(): LiveData<Int> {
        return getDaily
    }

    fun getWeekly(): LiveData<Int>{
        return getWeekly
    }

    fun getMonthly(): LiveData<Int>{
        return getMonthly
    }



    companion object {

        class UpdateDailyAsyncTask(val goalsDao: GoalsDao) : AsyncTask<GoalDaily, Unit, Unit>() {
            override fun doInBackground(vararg daily: GoalDaily?) {
                daily[0]?.let { goalsDao.updateDaily(it) }
            }
        }

        class UpdateWeeklyAsyncTask(val goalsDao: GoalsDao) : AsyncTask<GoalWeekly, Unit, Unit>() {
            override fun doInBackground(vararg weekly: GoalWeekly?) {
                weekly[0]?.let { goalsDao.updateWeekly(it) }
            }
        }

        class UpdateMonthlyAsyncTask(val goalsDao: GoalsDao) :
            AsyncTask<GoalMonthly, Unit, Unit>() {
            override fun doInBackground(vararg monthly: GoalMonthly?) {
                monthly[0]?.let { goalsDao.updateMonthly(it) }
            }
        }






    }
}



