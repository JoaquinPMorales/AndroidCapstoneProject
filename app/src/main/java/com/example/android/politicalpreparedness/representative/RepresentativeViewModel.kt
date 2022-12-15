package com.example.android.politicalpreparedness.representative

import android.util.Log
import androidx.databinding.BaseObservable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.network.models.RepresentativeResponse
import com.example.android.politicalpreparedness.representative.model.Representative
import retrofit2.HttpException
import java.io.IOException

class RepresentativeViewModel: ViewModel(){

    //TODO: Establish live data for representatives and address
    private val _representatives = MutableLiveData<List<Representative>>()

    val representatives: LiveData<List<Representative>>
        get() = _representatives

    private val _address = MutableLiveData<Address>()

    val address: MutableLiveData<Address>
        get() = _address

    /**
     *  The following code will prove helpful in constructing a representative from the API. This code combines the two nodes of the RepresentativeResponse into a single official :

    val (offices, officials) = getRepresentativesDeferred.await()
    _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }

    Note: getRepresentatives in the above code represents the method used to fetch data from the API
    Note: _representatives in the above code represents the established mutable live data housing representatives

     */
    //TODO: Create function to fetch representatives from API from a provided address
    suspend fun getRepresentatives(){
        try {
            val (offices, officials) = CivicsApi.retrofitService.getRepresentatives(
                _address.value!!.toFormattedString())
            _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }
            Log.i("RepresentativeViewModel", "representatives list: ${officials.toString()}")
        } catch (e: IOException) {
            Log.e("RepresentativeViewModel", "IOException, error: ${e.stackTrace}")
        } catch (e: HttpException) {
            Log.e("RepresentativeViewModel", "HttpException, please check your internet connection")
        }
    }

    init {
        _address.value = Address("", "", "", "", "");
    }

    //TODO: Create function get address from geo location

    //TODO: Create function to get address from individual fields

}
