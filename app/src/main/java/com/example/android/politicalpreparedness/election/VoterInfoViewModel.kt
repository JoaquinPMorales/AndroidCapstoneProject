package com.example.android.politicalpreparedness.election

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.CivicsHttpClient
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.launch

class VoterInfoViewModel(application: Application,
                         private val electionId: Int,
                         private val electionAddress: String,
                         private val dataSource: ElectionDao) : AndroidViewModel(application) {

    //TODO: Add live data to hold voter info
    private val _voterInfo = MutableLiveData<VoterInfoResponse>()

    val voterInfo: LiveData<VoterInfoResponse>
        get() = _voterInfo

    //TODO: Add var and methods to populate voter info
    private suspend fun getVoterInfo(){
        Log.i("VoterInfoViewModel", "electionID: $electionId")
        Log.i("VoterInfoViewModel", "electionAddr: $electionAddress")
        try {
            _voterInfo.value = CivicsApi.retrofitService.getVoterInfo(electionAddress, electionId.toString())
            Log.i("VoterInfoViewModel", "voterInfo election: ${_voterInfo.value!!.election}")
            Log.i("VoterInfoViewModel", "voterInfo state: ${_voterInfo.value!!.state}")
            Log.i("VoterInfoViewModel", "voterInfo list: ${_voterInfo.value!!.electionElectionOfficials}")
//            _response.value = "Success"
        } catch (e: Exception) {
            Log.i("VoterInfoViewModel", "Failure: ${e.message}")
//            _response.value = "Failure: ${e.message}"
        }
    }

    init {
        viewModelScope.launch {
            getVoterInfo()
        }
    }

    //TODO: Add var and methods to support loading URLs
    private val _votingLocationsUrl = MutableLiveData<String>()

    val votingLocationsUrl: LiveData<String>
        get() = _votingLocationsUrl

    private val _votingLocationsClicked = MutableLiveData<Boolean>()

    val votingLocationsClicked: LiveData<Boolean>
        get() = _votingLocationsClicked

    fun onVotingLocationsClicked(url: String) {
        Log.i("VoterInfoViewModel", "votingLocationsUrl: $url")
        _votingLocationsUrl.value = url
        _votingLocationsClicked.value = true
        Log.i("VoterInfoViewModel", "votingLocationsUrl: ${_votingLocationsUrl.value}")
    }

    fun onVotingLocationsOpened() {
        _votingLocationsClicked.value = false
        _votingLocationsUrl.value = ""
    }

    private val _ballotInfoUrl = MutableLiveData<String>()

    val ballotInfoUrl: LiveData<String>
        get() = _ballotInfoUrl

    private val _ballotInfoClicked = MutableLiveData<Boolean>()

    val ballotInfoClicked: LiveData<Boolean>
        get() = _ballotInfoClicked

    fun onBallotInfoClicked(url: String) {
        Log.i("VoterInfoViewModel", "BallotInfoUrl: $url")
        _ballotInfoUrl.value = url
        _ballotInfoClicked.value = true
    }

    fun onBallotInfoOpened() {
        _ballotInfoClicked.value = false
        _ballotInfoUrl.value = ""
    }

    //TODO: Add var and methods to save and remove elections to local database
    //TODO: cont'd -- Populate initial state of save button to reflect proper action based on election saved status

    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */

}