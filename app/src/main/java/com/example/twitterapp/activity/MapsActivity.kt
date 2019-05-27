package com.example.twitterapp.activity

import android.Manifest
import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.widget.ContentLoadingProgressBar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.twitterapp.R
import com.example.twitterapp.Utils
import com.example.twitterapp.model.TweetModel
import com.example.twitterapp.viewmodel.TweetMapViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener,
    GoogleMap.InfoWindowAdapter {

    companion object {

        var TAG: String? = MapsActivity::class.java.canonicalName

        const val REQUEST_PERMISSIONS_REQUEST_CODE = 1
    }

    private lateinit var mMap: GoogleMap

    private lateinit var tweetMapViewModel: TweetMapViewModel

    private val mapMarkers: HashMap<Marker, TweetModel> = HashMap()

    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    private var mLastLocation: Location? = null

    private var loadingProgress: ContentLoadingProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        loadingProgress = findViewById(R.id.loadingProgress)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        tweetMapViewModel = ViewModelProviders.of(this).get(TweetMapViewModel::class.java)

        setUpToolbar()
    }

    private fun setUpToolbar() {
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayShowTitleEnabled(false)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = resources.getString(R.string.section_a)
        }
    }

    public override fun onStart() {
        super.onStart()
        if (!checkPermissions()) {
            requestPermissions()
        } else {
            getLastLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        mFusedLocationClient.lastLocation
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful && task.result != null) {
                    mLastLocation = task.result
                    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                    val mapFragment = supportFragmentManager
                        .findFragmentById(R.id.map) as SupportMapFragment
                    mapFragment.getMapAsync(this)
                } else {
                    Log.w(TAG, "getLastLocation:exception", task.exception)
                    showMessage(getString(R.string.no_location_detected))
                }
            }
    }

    private fun showMessage(text: String) {
        val container = findViewById<View>(android.R.id.content)
        if (container != null) {
            Toast.makeText(this@MapsActivity, text, Toast.LENGTH_LONG).show()
        }
    }

    private fun showSnackbar(mainTextStringId: Int) {
        val snackBar =
            Snackbar.make(findViewById(android.R.id.content), getString(mainTextStringId), Snackbar.LENGTH_LONG)
                .setAction(R.string.settings) {
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:$packageName")
                    )
                    intent.addCategory(Intent.CATEGORY_DEFAULT)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
        snackBar.show()
    }

    private fun checkPermissions(): Boolean {
        val permissionState = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

    private fun startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(
            this@MapsActivity,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
            REQUEST_PERMISSIONS_REQUEST_CODE
        )
    }

    private fun requestPermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.")

            showSnackbar(R.string.permission_rationale)

        } else {
            Log.i(TAG, "Requesting permission")
            startLocationPermissionRequest()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        Log.i(TAG, "onRequestPermissionResult")
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            when {
                grantResults.isEmpty() -> // If user interaction was interrupted, the permission request is cancelled and you
                    // receive empty arrays.
                    Log.i(TAG, "User interaction was cancelled.")
                grantResults[0] == PackageManager.PERMISSION_GRANTED -> // Permission granted.
                    getLastLocation()
                else -> // Permission denied.
                    showSnackbar(R.string.permission_denied_explanation)
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setOnInfoWindowClickListener(this@MapsActivity)
        mMap.setInfoWindowAdapter(this@MapsActivity)

        val currentLatLng:LatLng = if(mLastLocation!=null){
            LatLng(mLastLocation!!.latitude, mLastLocation!!.longitude)
        }else{
            LatLng(43.76503, -79.37996)
        }

        val cameraUpdate: CameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLatLng, 12.0f)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng))
        mMap.animateCamera(cameraUpdate)

        tweetMapViewModel.loadRecentTweetData(currentLatLng)

        tweetMapViewModel.allRecentTweets.observe(this, Observer {

            runOnUiThread {
                loadingProgress?.show()
                if (it != null) {

                    Log.i("count", mapMarkers.size.toString())

                    if (mapMarkers.size >= 100) {
                        val markers = mapMarkers.keys
                        var i = 10
                        markers.forEach { marker ->
                            if (i > 0) {
                                marker.remove()
                                i--
                            }
                        }
                    }

                    for (i in 0 until it.length()) {
                        val tweetObject = Gson().fromJson(it.getJSONObject(i).toString(), TweetModel::class.java)
                        val tweet = tweetObject.text

                        if (tweetObject.geo != null && mapMarkers.size < 100) {
                            val tweetCoordinates =
                                LatLng(
                                    tweetObject.geo.coordinates?.get(0) as Double,
                                    tweetObject.geo.coordinates[1] as Double
                                )
                            val marker = mMap.addMarker(
                                MarkerOptions().position(tweetCoordinates).title(tweet).icon(
                                    BitmapDescriptorFactory.fromResource(R.drawable.twitter_icon)
                                )
                            )
                            mapMarkers[marker] = tweetObject
                        }
                    }
                }
                loadingProgress?.hide()
            }
        })
    }

    override fun getInfoContents(marker: Marker?): View? {
        return null
    }

    override fun getInfoWindow(marker: Marker?): View {

        val tweetObject = mapMarkers[marker]

        val nullParent: ViewGroup? = null
        val view = layoutInflater.inflate(R.layout.marker_info_window, nullParent)

        val tvTweetText = view.findViewById<TextView>(R.id.tv_tweet_text)
        tvTweetText.text = tweetObject?.text

        return view
    }

    override fun onInfoWindowClick(marker: Marker?) {

        val tweetObject = mapMarkers[marker]

        val intent = Intent(this, TweetDetailActivity::class.java)
        intent.putExtra(Utils.TWEET, tweetObject)
        startActivity(intent)
    }
}
