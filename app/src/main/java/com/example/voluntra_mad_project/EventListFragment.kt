package com.example.voluntra_mad_project

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.voluntra_mad_project.models.Event // Ensure this path is correct!
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class EventListFragment : Fragment() {

    // Use nullable property, initialized lazily or in setup
    private var eventsAdapter: EventsAdapter? = null
    private var recyclerView: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Find the RecyclerView within the fragment's inflated view
        recyclerView = view.findViewById(R.id.recycler_view_events)

        // 2. Setup the RecyclerView and Adapter
        setupRecyclerView()

        // 3. Load data
        loadEventsFromFirestore()
    }

    // Safety check for cleanup
    override fun onDestroyView() {
        super.onDestroyView()
        // Release references to views to avoid memory leaks
        recyclerView = null
        eventsAdapter = null
    }

    // --- Core Logic ---
    private fun setupRecyclerView() {
        // Initialize the adapter if it's null
        if (eventsAdapter == null) {
            eventsAdapter = EventsAdapter(emptyList()) // Initialize with an empty list
        }

        recyclerView?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = eventsAdapter
            setHasFixedSize(true) // Optimization
        }
    }

    private fun loadEventsFromFirestore() {
        val db = Firebase.firestore
        db.collection("events")
            .get()
            .addOnSuccessListener { result ->
                // IMPORTANT: Ensure your Event data class has NO-ARG CONSTRUCTOR
                // and mutable properties OR all fields match Firestore document fields EXACTLY.
                try {
                    val eventsList = result.toObjects(Event::class.java)
                    eventsAdapter?.submitList(eventsList)
                    Log.d("EventListFragment", "Successfully fetched ${eventsList.size} events.")
                } catch (e: Exception) {
                    // Log the exception if object mapping fails
                    Log.e("EventListFragment", "Error mapping Firestore objects to Event data class.", e)
                    Toast.makeText(context, "Data format error: Cannot load events.", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.w("EventListFragment", "Error getting documents from Firestore.", exception)
                // Use requireContext() or context to ensure context is available
                Toast.makeText(context, "Failed to load events. Check connection/rules.", Toast.LENGTH_SHORT).show()
            }
    }
}