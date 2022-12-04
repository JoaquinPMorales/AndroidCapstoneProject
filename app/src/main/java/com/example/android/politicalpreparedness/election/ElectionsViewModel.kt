package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.launch

//TODO: Construct ViewModel and provide election datasource
class ElectionsViewModel(application: Application,
                        val database: ElectionDao): AndroidViewModel(application) {

    //TODO: Create live data val for upcoming elections
    val upcomingElections = MutableLiveData<List<Election>>()

    //TODO: Create live data val for saved elections
    val savedElections = MutableLiveData<List<Election>>()

    init {
        viewModelScope.launch {
            val savedElectionsFromDb = database.getElections()
        }
    }

    //TODO: Create val and functions to populate live data for upcoming elections from the API and saved elections from local database

    //TODO: Create functions to navigate to saved or upcoming election voter info

}