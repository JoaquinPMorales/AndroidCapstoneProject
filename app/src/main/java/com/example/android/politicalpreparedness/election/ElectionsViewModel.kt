package com.example.android.politicalpreparedness.election

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.launch
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.CivicsHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//TODO: Construct ViewModel and provide election datasource
class ElectionsViewModel(application: Application,
                        val database: ElectionDao): AndroidViewModel(application) {

    //TODO: Create live data val for upcoming elections
    private val _upcomingElections = MutableLiveData<List<Election>>()

    val upcomingElections: LiveData<List<Election>>
        get() = _upcomingElections

    //TODO: Create live data val for saved elections
    private val _savedElections = MutableLiveData<List<Election>>()

    val savedElections: LiveData<List<Election>>
        get() = _savedElections

    //TODO: Create val and functions to populate live data for upcoming elections from the API and saved elections from local database
    private suspend fun getUpcomingElections(){
        _upcomingElections.value = CivicsApi.retrofitService.getElections(CivicsHttpClient.API_KEY).elections
        Log.i("ElectionsViewModel", "elections list: ${_upcomingElections.value}")
    }


    init {
        viewModelScope.launch {
            val savedElectionsFromDb = database.getElections()
            _savedElections.value = savedElectionsFromDb
            getUpcomingElections()
        }
    }

    //TODO: Create functions to navigate to saved or upcoming election voter info
    private val _navigateToSavedElection = MutableLiveData<Election?>()
    val navigateToSavedElection : LiveData<Election?>
        get() = _navigateToSavedElection

    fun onSavedElectionClicked(election: Election){
        Log.i("ElectionsViewModel", "electionSavedClicked: $election")
        _navigateToSavedElection.value = election
    }

    fun onSavedElectionNavigated() {
        _navigateToSavedElection.value = null
    }

    private val _navigateToUpcomingElection = MutableLiveData<Election?>()
    val navigateToUpcomingElection : LiveData<Election?>
        get() = _navigateToUpcomingElection

    fun onUpcomingElectionClicked(election: Election){
        Log.i("ElectionsViewModel", "electionUpcomingClicked: $election")
        _navigateToUpcomingElection.value = election
    }

    fun onUpcomingElectionNavigated() {
        _navigateToUpcomingElection.value = null
    }

}