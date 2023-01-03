package com.onkar.mysleeptracker

import android.app.Application
import com.android.example.sleepcodelab.data.db.SleepDatabase
import androidx.datastore.preferences.createDataStore
import com.onkar.mysleeptracker.data.SleepRepository
import com.onkar.mysleeptracker.data.datastore.SLEEP_PREFERENCES_NAME
import com.onkar.mysleeptracker.data.datastore.SleepSubscriptionStatus

/**
 * Sets up repository for all sleep data.
 */
class MainApplication : Application() {
    // Both database and repository use lazy so they aren't created when the app starts, but only
    // when repository is first needed.
    private val database by lazy {
        SleepDatabase.getDatabase(applicationContext)
    }

    val repository by lazy {
        SleepRepository(
            sleepSubscriptionStatus = SleepSubscriptionStatus(
                applicationContext.createDataStore(name = SLEEP_PREFERENCES_NAME)),
            sleepSegmentEventDao = database.sleepSegmentEventDao(),
            sleepClassifyEventDao = database.sleepClassifyEventDao()
        )
    }
}
