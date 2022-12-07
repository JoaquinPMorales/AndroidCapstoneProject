package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentElectionBindingImpl
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.databinding.FragmentLaunchBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener

class ElectionsFragment: Fragment() {

    //TODO: Declare ViewModel
    private lateinit var viewModel : ElectionsViewModel

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //TODO: Add ViewModel values and create ViewModel

        // Create an instance of the ViewModel Factory.
        val application = requireNotNull(this.activity).application
        val dataSource = ElectionDatabase.getInstance(application).electionDao
        val viewModelFactory = ElectionsViewModelFactory(application,dataSource)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ElectionsViewModel::class.java)


        //TODO: Add binding values
        val binding = FragmentElectionBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel


        //TODO: Link elections to voter info

        //TODO: Initiate recycler adapters
        val upcomingElectionsAdapter = ElectionListAdapter(ElectionListener { election ->
//            Toast.makeText(context, "${electionId}", Toast.LENGTH_LONG).show()
            Log.i("ElectionsFragment", "electionUpcomingClicked: $election")
            viewModel.onUpcomingElectionClicked(election)
        })

        val savedElectionsAdapter = ElectionListAdapter(ElectionListener { election ->
//            Toast.makeText(context, "${electionId}", Toast.LENGTH_LONG).show()
            Log.i("ElectionsFragment", "electionSavedClicked: $election")
            viewModel.onSavedElectionClicked(election)
        })

        binding.upcomingElectionsList.adapter = upcomingElectionsAdapter
        binding.savedElectionsList.adapter = savedElectionsAdapter

        viewModel.navigateToSavedElection.observe(viewLifecycleOwner, Observer { election ->
            election?.let {
                this.findNavController().navigate(ElectionsFragmentDirections
                    .actionElectionsFragmentToVoterInfoFragment(it.id, it.division))
                viewModel.onSavedElectionNavigated()
            }
        })

        viewModel.navigateToUpcomingElection.observe(viewLifecycleOwner, Observer { election ->
            election?.let {
                Log.i("ElectionsFragment", "electionId send to  VoterInfo: ${election.id}")
                Log.i("ElectionsFragment", "election division send to  VoterInfo: ${election.division}")
                this.findNavController().navigate(ElectionsFragmentDirections
                    .actionElectionsFragmentToVoterInfoFragment(it.id, it.division))
                viewModel.onUpcomingElectionNavigated()
            }
        })

        //TODO: Populate recycler adapters
        viewModel.savedElections.observe(viewLifecycleOwner, Observer {
            savedElectionsAdapter.submitList(it);
        })
        viewModel.upcomingElections.observe(viewLifecycleOwner, Observer {
            Log.i("ElectionsFragment", "elections list size: ${it.size}")
            Log.i("ElectionsFragment", "elections list: $it")
            upcomingElectionsAdapter.submitList(it);
        })

        return binding.root

    }

    //TODO: Refresh adapters when fragment loads

}