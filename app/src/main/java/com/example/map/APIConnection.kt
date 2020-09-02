package com.example.map

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley


private const val TAG = "Connection"

class ObjectsConnection{
        var onPlacesDownloadedListener: ((places: Array<Place>) -> Unit)? = null

        fun getData(context: Context, lat: Double, lon: Double){
        val queue = Volley.newRequestQueue(context)
        val url = "https://api.tomtom.com/search/2/nearbySearch/.JSON?key=ClS4WWvEOaRLcESyJH6upMaEpPDc3plX&lat=$lat&lon=$lon"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                val array = response.getJSONArray("results")
                var lon = 0e10
                var lat = 0e10
                var name = ""
                val places = Array(array.length()){Place(name, lon, lat)}
                for(i in 0 until array.length()) {
                    name = array.getJSONObject(i).getJSONObject("poi").getString("name")
                    lat = array.getJSONObject(i).getJSONObject("position").getDouble("lat")
                    lon = array.getJSONObject(i).getJSONObject("position").getDouble("lon")
                    val newPlace = Place(name, lon, lat)
                    places[i] = newPlace
                    Log.d(TAG, places[i].toString())
                }
                onPlacesDownloadedListener?.invoke(places)
            },
            Response.ErrorListener { error ->
                Log.e(TAG, "Error")
            }
        )
// Access the RequestQueue through your singleton class.
        queue.add(jsonObjectRequest)
    }
}