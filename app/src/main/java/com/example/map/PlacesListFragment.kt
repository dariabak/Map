package com.example.map

import android.content.Intent
import android.content.Intent.getIntent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

private const val PLACES_KEY = "PLACES_KEY"
class PlacesListFragment: Fragment() {

    private lateinit var placeRecyclerView: RecyclerView
    private var adapter: PlaceAdapter? = null
    var places: Array<Place> = emptyArray()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        places = arguments?.getParcelableArray("PLACES_KEY") as Array<Place>
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_place_list, container, false)
        placeRecyclerView =
            view.findViewById(R.id.place_recycler_view) as RecyclerView
        placeRecyclerView.layoutManager = LinearLayoutManager(context)

        updateUI()


        return view
    }

    private fun updateUI() {
        adapter = PlaceAdapter(places as Array<Place>)
        placeRecyclerView.adapter = adapter
    }

    private inner class PlaceHolder(view: View)
        : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = itemView.findViewById(R.id.place_title)

    }

    private inner class PlaceAdapter(var placesArray: Array<Place>)
        : RecyclerView.Adapter<PlaceHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
                : PlaceHolder {
            val view = layoutInflater.inflate(R.layout.list_items_place, parent,false)
            return PlaceHolder(view)
        }

        override fun getItemCount() = placesArray.size

        override fun onBindViewHolder(holder: PlaceHolder, position: Int) {
            val place = placesArray[position]
            holder.apply {
                titleTextView.text = place.name
            }
        }
    }
        companion object {
            fun newInstance(places: List<Place>): PlacesListFragment {
                val bundle = Bundle()
                bundle.putParcelableArray(
                    PLACES_KEY, places.toTypedArray())
                val fragment = PlacesListFragment()
                fragment.arguments = bundle
                return fragment
            }
        }
}