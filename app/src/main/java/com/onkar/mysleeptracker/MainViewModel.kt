package com.onkar.mysleeptracker

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.android.example.sleepcodelab.data.db.SleepClassifyEventEntity
import com.android.example.sleepcodelab.data.db.SleepSegmentEventEntity
import com.onkar.mysleeptracker.data.SleepRepository
import kotlinx.coroutines.launch

/**
 * Created by Onkar Pendse  pendseomkar92@gmail.com
 *  on 01-08-2022.
 *  For More Information Contact me!!!.
 *          !!  Thank You  !!
 */
class MainViewModel(private val repository: SleepRepository) : ViewModel() {


    val subscribedToSleepDataLiveData = repository.subscribedToSleepDataFlow.asLiveData()

    fun updateSubscribedToSleepData(subscribed: Boolean) = viewModelScope.launch {
        repository.updateSubscribedToSleepData(subscribed)
    }

    // Using LiveData and caching what allWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allSleepSegments: LiveData<List<SleepSegmentEventEntity>> =
        repository.allSleepSegmentEvents.asLiveData()

    val allSleepClassifyEventEntities: LiveData<List<SleepClassifyEventEntity>> =
        repository.allSleepClassifyEvents.asLiveData()
}