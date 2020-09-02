package com.example.map

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.parcel.Parcelize

@Parcelize
class Place(val name: String, val lon: Double, val lat: Double): Parcelable