package com.example.sc.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.sc.model.GoogleFitDaily
import com.example.sc.model.GoogleFitMonthly
import com.example.sc.model.GoogleFitWeekly
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

interface GoogleFitRepository {
    fun getDailyFitnessData(context: Context): MutableLiveData<GoogleFitDaily>
    fun getWeeklyFitnessData(context: Context): MutableLiveData<GoogleFitWeekly>
    fun getMonthlyFitnessData(context: Context): MutableLiveData<GoogleFitMonthly>
    fun getGoogleAccount(context: Context): GoogleSignInAccount
}