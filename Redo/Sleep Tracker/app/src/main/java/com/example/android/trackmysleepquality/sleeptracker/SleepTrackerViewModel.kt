/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.trackmysleepquality.sleeptracker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.formatNights
import kotlinx.coroutines.*

/**
 * ViewModel for SleepTrackerFragment.
 */
class SleepTrackerViewModel(
        val database: SleepDatabaseDao,
        application: Application) : AndroidViewModel(application) {
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private var tonight = MutableLiveData<SleepNight?>()
    private val nights = database.getAllNights()
    val nightsString = Transformations.map(nights) { nights ->
        formatNights(nights, application.resources)
    }

    val startButtonVisible = Transformations.map(tonight) {
        null == it
    }

    val stopButtonVisible = Transformations.map(tonight) {
        null != it
    }

    val clearButtonVisible = Transformations.map(nights) {
        it?.isNotEmpty()
    }

    private var _showSnackbarEvent = MutableLiveData<Boolean>()

    val showSnackBarEvent: LiveData<Boolean>
        get() = _showSnackbarEvent

    private val _navigateToSleepQuality = MutableLiveData<SleepNight>()

    fun doneShowingSnackbar() {
        _showSnackbarEvent.value = false
    }

    /**
     * If this is non-null, immediately navigate to [SleepQualityFragment] and call [doneNavigating]
     */
    val navigateToSleepQuality: LiveData<SleepNight>
        get() = _navigateToSleepQuality

    /**
     * Call this immediately after navigating to [SleepQualityFragment]
     *
     * It will clear the navigation request, so if the user rotates their phone it won't navigate
     * twice.
     */
    fun doneNavigating() {
        _navigateToSleepQuality.value = null
    }

    init {
        initializeTonight()
    }

    private fun initializeTonight() {
        uiScope.launch {
            tonight.value = getTonightFromDatabase()
        }
    }

    private suspend fun getTonightFromDatabase(): SleepNight? {
        return withContext(Dispatchers.IO) {
            var night = database.getTonight()
            if (night?.endTimeMilli != night?.startTimeMilli) {
                night = null
            }
            night
        }
    }

    private suspend fun clear() {
        withContext(Dispatchers.IO) {
            database.clear()
        }
    }
    private suspend fun update(night: SleepNight) {
        withContext(Dispatchers.IO) {
            database.update(night)
        }
    }

    private suspend fun insert(night: SleepNight) {
        withContext(Dispatchers.IO) {
            database.insert(night)
        }
    }

    fun onStartTracking() {
        uiScope.launch {
            val newNight = SleepNight()

            insert(newNight)

            tonight.value = getTonightFromDatabase()
        }
    }

    fun onStopTracking() {
        uiScope.launch {
            val oldNight = tonight.value ?: return@launch

            oldNight.endTimeMilli = System.currentTimeMillis()

            update(oldNight)

            _navigateToSleepQuality.value = oldNight
        }
    }

    fun onClear() {
        uiScope.launch {
            clear()

            tonight.value = null
        }
        _showSnackbarEvent.value = true
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}

