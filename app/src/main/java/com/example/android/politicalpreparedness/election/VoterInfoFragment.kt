package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBindingImpl
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.example.android.politicalpreparedness.network.models.Election
import java.sql.Date

class VoterInfoFragment : Fragment() {

    private lateinit var viewModel: VoterInfoViewModel

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val args = VoterInfoFragmentArgs.fromBundle(requireArguments())
//        Toast.makeText(context, "ElectionId: ${args.argElectionId}, Division: ${args.argDivision}", Toast.LENGTH_LONG).show()

        //TODO: Add ViewModel values and create ViewModel
        val application = requireNotNull(this.activity).application
        val dataSource = ElectionDatabase.getInstance(application).electionDao
        val viewModelFactory = VoterInfoViewModelFactory(application, args.argElectionId, args.argDivision.state + ", " + args.argDivision.country, dataSource)
        viewModel = ViewModelProvider(this, viewModelFactory).get(VoterInfoViewModel::class.java)

        //TODO: Add binding values
        val binding = FragmentVoterInfoBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.voterInfoViewModel = viewModel

        //TODO: Populate voter info -- hide views without provided data.
        /**
        Hint: You will need to ensure proper data is provided from previous fragment.
        */


        //TODO: Handle loading of URLs
        viewModel.votingLocationsClicked.observe(viewLifecycleOwner, Observer {
            //Intent to open url
            Log.i("VoterInfoFragment", "Voting locations clicked")
            Log.i("VoterInfoFragment", "Voting locations URL: ${viewModel.votingLocationsUrl.value}")
            if(viewModel.votingLocationsUrl.value != "")
            {
                viewModel.votingLocationsUrl.value?.let { it1 -> startIntentToOpenUrl(it1) }
                viewModel.onVotingLocationsOpened()
            }
        })

        viewModel.ballotInfoClicked.observe(viewLifecycleOwner, Observer {
            //Intent to open url
            Log.i("VoterInfoFragment", "Ballot information clicked")
            Log.i("VoterInfoFragment", "Ballot information URL: ${viewModel.ballotInfoUrl.value}")
            if(viewModel.ballotInfoUrl.value != "")
            {
                viewModel.ballotInfoUrl.value?.let { it1 -> startIntentToOpenUrl(it1) }
                viewModel.onBallotInfoOpened()
            }
        })

        //TODO: Handle save button UI state
        //TODO: cont'd Handle save button clicks

        return binding.root

    }

    private fun startIntentToOpenUrl(url: String)
    {
        val implicitIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(implicitIntent)
    }

    //TODO: Create method to load URL intents

}