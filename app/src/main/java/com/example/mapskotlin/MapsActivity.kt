package com.example.mapskotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_maps.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import java.util.*
import android.R.attr.data
import android.app.Activity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.example.mapskotlin.model.Maps
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import org.jetbrains.anko.alert
import kotlin.collections.ArrayList


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var database :FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        database = FirebaseDatabase.getInstance()

        btnAction.onClick {
            val myRef = database.getReference("Maps")
            val key = myRef.push().key
            val maps = Maps()
            maps.name = etNameLocation.text.toString()
            maps.owner = etOwner.text.toString()
            maps.latitude = tvLat.text.toString()
            maps.longitude = tvLong.text.toString()

            maps.key = key
            myRef.child(key ?:"").setValue(maps)

            alert {
                title = "Add maps to Firebase Successfully"
            }.show()

        }




        btnSelectLocation.onClick {
            Places.initialize(applicationContext,"AIzaSyCkdLjhSoU4OAZwZTp0O_5zxthIfr6FJRc")
            val fields = Arrays.asList(Place.Field.ID,Place.Field.NAME,Place.Field.LAT_LNG,Place.Field.ADDRESS)
            val intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY,fields)
                .build(applicationContext)
            startActivityForResult(intent,1)
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        /*val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))*/
        tvLong.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                val lat = tvLat.text.toString()
                val lon = tvLong.text.toString()
                val name = etNameLocation.text.toString()

                if (lat.isNotEmpty()||lon.isNotEmpty()){
                    val latLng = LatLng(lat.toDouble(),lon.toDouble())
                    mMap.clear()
                    mMap.addMarker(MarkerOptions().position(latLng).title("${Place.Field.NAME}"))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,16f))
                }
            }
        })


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                val place = data?.let { Autocomplete.getPlaceFromIntent(it) }
                val latlng = place?.latLng
                tvLat.text = latlng?.latitude.toString()
                tvLong.text = latlng?.longitude.toString()

            } else if (resultCode === AutocompleteActivity.RESULT_ERROR) {

                //val status = Autocomplete.getStatusFromIntent(data)
            }
        }

    }
}
