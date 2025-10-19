package com.example.voluntra_mad_project // Use your actual package name

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

class MapOverlayFragment : Fragment() {

    private lateinit var mapView: MapView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Load the OSMDroid configuration
        Configuration.getInstance().load(context, context?.getSharedPreferences("osmdroid", 0))
        Configuration.getInstance().userAgentValue = context?.packageName

        // Inflate the layout for this fragment (Must contain the MapView)
        val view = inflater.inflate(R.layout.fragment_map_overlay, container, false)
        mapView = view.findViewById(R.id.map_view)

        setupMap()
        return view
    }

    // --- Logic from old MainActivity ---
    private fun setupMap() {
        val mapController = mapView.controller
        mapController.setZoom(12.5)
        val startPoint = GeoPoint(19.0760, 72.8777) // Mumbai
        mapController.setCenter(startPoint)

        // TODO: Add logic here to fetch event locations and add OSMDroid Markers
    }

    // --- Map Lifecycle (Moved from old MainActivity) ---
    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Important: Properly clean up the map view to avoid memory leaks
        mapView.onDetach()
    }
}