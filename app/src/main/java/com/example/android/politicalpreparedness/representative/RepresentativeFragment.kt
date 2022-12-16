package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListener
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.util.*


class DetailFragment : Fragment() {

    //TODO: Declare ViewModel
    private lateinit var viewModel: RepresentativeViewModel
    private lateinit var binding : FragmentRepresentativeBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //TODO: Establish bindings
        binding = FragmentRepresentativeBinding.inflate(inflater)
        viewModel = RepresentativeViewModel()
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        //TODO: Define and assign Representative adapter
        val representativeListAdapter = RepresentativeListAdapter(RepresentativeListener {  })
        binding.representativeRecyclerView.adapter = representativeListAdapter

        //TODO: Populate Representative adapter
        viewModel.representatives.observe(viewLifecycleOwner, Observer {
            Log.i("RepresentativeFragment", "submit representative's list")
            representativeListAdapter.submitList(it)
        })

        //TODO: Establish button listeners for field and location search
        binding.buttonSearch.setOnClickListener {
            Log.i("RepresentativeFragment", "address: ${viewModel.address.value?.toFormattedString()}")
            hideKeyboard()
            if (viewModel.address.value?.let { it1 -> checkAddress(it1) } == true)
            {
                Log.i("RepresentativeFragment", "valid address")
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.getRepresentatives();
                }
            }
        }

        binding.buttonLocation.setOnClickListener {
            hideKeyboard()
            getLocation()

        }

        //Listener for Spinner selection and set the value
        binding.state.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                viewModel.address.value!!.state = (requireContext().resources.getStringArray(R.array.states)[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        return binding.root
    }

    private fun getLocation() {
        //TODO: Get location from LocationServices
        //TODO: The geoCodeLocation method is a helper function to change the lat/long location to a human readable street address
        Log.i("RepresentativeFragment", "getLocation")
        // Check if the device has the necessary hardware and permissions to provide location data
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            // If not, ask the user for permission to access location data
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                1)
            Log.i("RepresentativeFragment", "getLocation is NOT possible, asking about permission")
        } else {
            Log.i("RepresentativeFragment", "getLocation is possible")
            // If the device has the necessary hardware and permissions, request the current location
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                Log.i("RepresentativeFragment", "successListener: $location")
                    if (location != null) {
                        Log.i("RepresentativeFragment", "latitude: ${location.latitude}")
                        Log.i("RepresentativeFragment", "longitude: ${location.longitude}")
                        var addressLocation = Address("", "", "", "", "")
                        addressLocation = geoCodeLocation(location)
                        Log.i("RepresentativeFragment", "address location: ${addressLocation.toFormattedString()}")
                        if(checkAddress(addressLocation)) {
                            viewModel.address.postValue(addressLocation)
                        }
                    }
                }
        }
    }

    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
                .map { address ->
                    Address(address.thoroughfare, address.subThoroughfare, address.locality, address.adminArea, address.postalCode)
                }
                .first()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

    private fun checkAddress(address: Address) : Boolean {
        var isValid = false;
        if(address.line1.isNullOrEmpty()) {
            showSnackbar(getString(R.string.address_line1_empty_error))
        } else if (address.state.isNullOrEmpty()) {
            showSnackbar(getString(R.string.address_state_empty_error))
        } else if (address.city.isNullOrEmpty()) {
            showSnackbar(getString(R.string.address_city_empty_error))
        } else if (address.zip.isNullOrEmpty()) {
            showSnackbar(getString(R.string.address_zip_empty_error))
        }
        else {
            isValid = true
        }
        return isValid
    }

    private fun showSnackbar(text: String) {
        Snackbar.make(binding.root, text, Snackbar.LENGTH_LONG).show()
    }

}