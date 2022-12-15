package com.example.android.politicalpreparedness.representative

import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBindingImpl
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListener
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.util.Locale

class DetailFragment : Fragment() {

    companion object {
        //TODO: Add Constant for Location request
    }

    //TODO: Declare ViewModel
    private lateinit var viewModel: RepresentativeViewModel
    private lateinit var binding : FragmentRepresentativeBinding

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
        return binding.root
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //TODO: Handle location permission result to get location on permission granted
    }

    private fun checkLocationPermissions(): Boolean {
        return if (isPermissionGranted()) {
            true
        } else {
            //TODO: Request Location permissions
            false
        }
    }

    private fun isPermissionGranted() : Boolean {
        //TODO: Check if permission is already granted and return (true = granted, false = denied/other)
        return false
    }

    private fun getLocation() {
        //TODO: Get location from LocationServices
        //TODO: The geoCodeLocation method is a helper function to change the lat/long location to a human readable street address
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