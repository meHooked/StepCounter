package com.example.sc.model

import android.content.Context
import android.os.AsyncTask
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Goals::class], version = 1, exportSchema = false)
abstract class GoalsDatabase: RoomDatabase() {
    abstract fun goalsDao(): GoalsDao

    companion object {
        var instance: GoalsDatabase? = null

        //Create DB
        @Synchronized
        fun getInstance(context: Context): GoalsDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    GoalsDatabase::class.java,
                    "goals"
                )
                    .fallbackToDestructiveMigration()
                    //.allowMainThreadQueries() - will block UI, use only for testing/prototyping purposes
                    .addCallback(roomCallback)
                    .build()

            }
            return instance as GoalsDatabase
        }


        //After DB creation, populate it with sample data
        val roomCallback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                instance?.let { PopulateDbAsyncTask(it).execute() }
            }
        }

        class PopulateDbAsyncTask(db: GoalsDatabase) : AsyncTask<Unit, Unit, Unit>() {
            val goalsDao: GoalsDao = db.goalsDao()

            override fun doInBackground(vararg p0: Unit?) {

               goalsDao.insert(Goals(1,0,0,0))
            }

        }
    }

    }



