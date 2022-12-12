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
import com.example.android.politicalpreparedness.R
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

        //TODO: Add ViewModel values and create ViewModel
        val application = requireNotNull(this.activity).application
        val dataSource = ElectionDatabase.getInstance(application).electionDao
        var address = ""
        if (args.argDivision.state == ""){
            address = "dc, " + args.argDivision.country
        }
        else {
            address = args.argDivision.state + ", " + args.argDivision.country
        }
        val viewModelFactory = VoterInfoViewModelFactory(application, args.argElectionId, address, dataSource)
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
            Log.i("VoterInfoFragment", "Voting locations clicked")
            Log.i("VoterInfoFragment", "Voting locations URL: ${viewModel.votingLocationsUrl.value}")
            if((viewModel.votingLocationsUrl.value != "") && (it != false))
            {
                viewModel.votingLocationsUrl.value?.let { it1 -> startIntentToOpenUrl(it1) }
                viewModel.onVotingLocationsOpened()
            }
        })

        viewModel.ballotInfoClicked.observe(viewLifecycleOwner, Observer {
            Log.i("VoterInfoFragment", "Ballot information clicked")
            Log.i("VoterInfoFragment", "Ballot information URL: ${viewModel.ballotInfoUrl.value}")
            if((viewModel.ballotInfoUrl.value != "") && (it != false))
            {
                viewModel.ballotInfoUrl.value?.let { it1 -> startIntentToOpenUrl(it1) }
                viewModel.onBallotInfoOpened()
            }
        })

        //TODO: Handle save button UI state
        //TODO: cont'd Handle save button clicks
        viewModel.isElectionFollowed.observe(viewLifecycleOwner, Observer { isElectionSaved ->
            Log.i("VoterInfoFragment", "Button follow state: $isElectionSaved")
            if(isElectionSaved && isElectionSaved != null) {
                binding.saveElectionButton.text = getText(R.string.save_election_btn_unfollow_text)
            } else {
                binding.saveElectionButton.text = getText(R.string.save_election_btn_follow_text)
            }
        })

        return binding.root

    }

    //TODO: Create method to load URL intents
    //Get it from stackOverFlow: https://stackoverflow.com/questions/3004515/sending-an-intent-to-browser-to-open-specific-url
    private fun startIntentToOpenUrl(url: String)
    {
        val implicitIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(implicitIntent)
    }
}