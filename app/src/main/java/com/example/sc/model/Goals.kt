package com.example.sc.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")

data class Goals(
    @PrimaryKey val id: Int = 1,
    var dailyGoal: Int,
    val weeklyGoal: Int,
    val monthlyGoal: Int
)

class GoalDaily(
    val id: Int = 1,
    val dailyGoal: Int
)

class GoalWeekly(
    val id: Int = 1,
    val weeklyGoal: Int
)

class GoalMonthly(
    val id: Int = 1,
    val monthlyGoal: Int
)