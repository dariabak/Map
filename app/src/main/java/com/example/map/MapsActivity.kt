package com.example.map

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback
import androidx.core.content.ContextCompat
import com.example.map.PermissionUtils.PermissionDeniedDialog.Companion.newInstance
import com.example.map.PermissionUtils.isPermissionGranted
import com.example.map.PermissionUtils.requestPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


/**
 * This demo shows how GMS Location can be used to check for changes to the users location.  The
 * "My Location" button uses GMS Location to set the blue dot representing the users location.
 * Permission for [Manifest.permission.ACCESS_FINE_LOCATION] is requested at run
 * time. If the permission has not been granted, the Activity is finished with an error message.
 */

private const val TAG = "MainActivity"
private lateinit var fusedLocationClient: FusedLocationProviderClient
private lateinit var  searchText: EditText

class MapsActivity : AppCompatActivity(), OnMyLocationButtonClickListener,
    OnMyLocationClickListener, OnMapReadyCallback, OnRequestPermissionsResultCallback {
    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * [.onRequestPermissionsResult].
     */
    private var permissionDenied = false
    private lateinit var map: GoogleMap
    private val objectCon = APIConnection()
    private var enteredPlace = ""
    private val placesList = mutableListOf<Place>()
    var mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        searchText = findViewById(R.id.editText)

        val currentFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container)


        searchText.setOnClickListener {
                if (currentFragment == null) {
                    val fragment = PlacesListFragment.newInstance(placesList)
                    supportFragmentManager
                        .beginTransaction()
                        .add(R.id.fragment_container, fragment)
                        .commit()
                }
        }
    }


    override fun onMapReady(googleMap: GoogleMap?) {
        map = googleMap ?: return
        googleMap.setOnMyLocationButtonClickListener(this)
        googleMap.setOnMyLocationClickListener(this)
        enableMyLocation()

        val locationButton =
            (mapFragment!!.view!!.findViewById<View>("1".toInt())
                .parent as View).findViewById<View>("2".toInt())

        // and next place it, for exemple, on bottom right (as Google Maps app)

        // and next place it, for exemple, on bottom right (as Google Maps app)
        val rlp =
            locationButton.layoutParams as RelativeLayout.LayoutParams
        // position on right bottom
        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
        rlp.setMargins(0, 0, 30, 30)


        objectCon.onPlacesDownloadedListener = { places ->
            for(i in 0 until places.size) {
                val point =  LatLng(places[i].lat, places[i].lon)
                val markerOptions = MarkerOptions()
                markerOptions.position(point)
                markerOptions.title(places[i].name)
                googleMap.addMarker(markerOptions);
                val place = places[i]
                placesList.add(place)
            }
        }


    }



    /**
     * Enables the My Location layer if the fine location permission has been granted
     */
    private fun enableMyLocation() {
        if (!::map.isInitialized) return
        // [START maps_check_location_permission]
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            map.isMyLocationEnabled = true
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {

                        objectCon.getData(this, location.latitude, location.longitude)
                        Log.d(TAG, location.latitude.toString())
                        // get latitude , longitude and other info from this

                    }
                    map.setOnMapLongClickListener {
                        location.latitude = it.latitude
                        location.longitude = it.longitude
                        objectCon.getData(this, location.latitude, location.longitude)
                    }
                }
        } else {
            // Permission to access the location is missing. Show rationale and request permission
            requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                Manifest.permission.ACCESS_FINE_LOCATION, true
            )
        }
        // [END maps_check_location_permission]
    }

    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show()
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false
    }

    override fun onMyLocationClick(location: Location) {
        Toast.makeText(this, "Current location:\n$location", Toast.LENGTH_LONG).show()

    }

    // [START maps_check_location_permission_result]
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return
        }
        if (isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation()
        } else {
            // Permission was denied. Display an error message
            // [START_EXCLUDE]
            // Display the missing permission error dialog when the fragments resume.
            permissionDenied = true
            // [END_EXCLUDE]
        }
    }

    // [END maps_check_location_permission_result]
    override fun onResumeFragments() {
        super.onResumeFragments()
        if (permissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError()
            permissionDenied = false
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private fun showMissingPermissionError() {
        newInstance(true).show(supportFragmentManager, "dialog")
    }

    companion object {
        /**
         * Request code for location permission request.
         *
         * @see .onRequestPermissionsResult
         */
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}