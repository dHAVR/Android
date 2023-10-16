package com.dar_hav_projects.gpshelper.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dar_hav_projects.gpshelper.MainApp
import com.dar_hav_projects.gpshelper.MainViewModel
import com.dar_hav_projects.gpshelper.databinding.FragmentTracksBinding
import com.dar_hav_projects.gpshelper.db.TrackAdapter
import com.dar_hav_projects.gpshelper.db.TrackItem
import com.dar_hav_projects.gpshelper.utils.openFragment

class TracksFragment : Fragment(), TrackAdapter.Listener {
    private lateinit var binding: FragmentTracksBinding
    private lateinit var adapter: TrackAdapter
    private val mainViewModel: MainViewModel by activityViewModels {
        MainViewModel.ViewModelFactory((requireContext().applicationContext as MainApp).database)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iniRcView()
        observer()
    }

    private fun iniRcView() = with(binding) {
        adapter = TrackAdapter(this@TracksFragment)
        rcView.layoutManager = LinearLayoutManager(requireContext())
        rcView.adapter = adapter
    }

    private fun observer() {
        mainViewModel.tracks.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            binding.tvEmpty.visibility = if (it.isEmpty()) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    override fun onClick(trackItem: TrackItem, type: TrackAdapter.ClickType) {
        when (type) {
            TrackAdapter.ClickType.DELETE -> {
                mainViewModel.deleteTrack(trackItem)
            }
            TrackAdapter.ClickType.OPEN -> {
                mainViewModel.currentTrack.value = trackItem
                openFragment(SelectedRouteFragment.newInstance())
            }

        }

    }

    companion object {
        @JvmStatic
        fun newInstance() = TracksFragment()
    }


}