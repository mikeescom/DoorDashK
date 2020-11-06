package com.mikeescom.doordashk.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.mikeescom.doordashk.R
import com.mikeescom.doordashk.data.models.Restaurant
import com.mikeescom.doordashk.viewmodel.MainViewModel

class ListFragment : Fragment() {
    private var viewModel: MainViewModel? = null
    private var adapter: ListAdapter? = null
    private var recyclerView: RecyclerView? = null
    private var progressBar: ProgressBar? = null

    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation = locationResult.lastLocation
            viewModel!!.getRestaurantsResponseLiveData(mLastLocation.latitude, mLastLocation.longitude)!!
                .observe(viewLifecycleOwner, { response -> response?.let { updateUI(it) } })
        }
    }

    companion object {
        private val TAG = ListFragment::class.java.simpleName
        private const val PERMISSIONS_REQUEST_LOCATION = 99
        private const val INTERVAL = 1000 * 60.toLong()
        private const val FASTEST_INTERVAL = 1000 * 60.toLong()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        getLastLocation()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        adapter = ListAdapter(requireContext())
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        viewModel!!.init()
        initViews(view)

        return view
    }

    override fun onResume() {
        super.onResume()
        if (checkPermissions()) {
            getLastLocation()
        }
    }

    private fun initViews(view: View) {
        progressBar = view.findViewById(R.id.progressBar)
        recyclerView = view.findViewById(R.id.recycler_view)
    }

    private fun updateUI(restaurants: Array<Restaurant>) {
        if (progressBar != null) {
            progressBar!!.visibility = View.GONE
        }
        adapter!!.setResults(restaurants)
        recyclerView!!.addItemDecoration(
            DividerItemDecoration(
                recyclerView!!.context, DividerItemDecoration.VERTICAL
            )
        )
        recyclerView!!.layoutManager = LinearLayoutManager(context)
        adapter!!.setOnClickItemListener(object : ListAdapter.OnClickItemListener {
            override fun onClickItem(id: Long) {
                val bundle = Bundle()
                bundle.putLong("ID", id)
                NavHostFragment.findNavController(parentFragment!!)
                    .navigate(R.id.action_listFragment_to_detailFragment, bundle)
            }
        })
        recyclerView!!.adapter = adapter
    }


    // Location methods
    @SuppressLint("MissingPermission")
    fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient!!.lastLocation
                    .addOnCompleteListener { task: Task<Location?> ->
                        val location = task.result
                        if (location == null) {
                            requestNewLocationData()
                        } else {
                            viewModel!!.getRestaurantsResponseLiveData(location.latitude, location.longitude)!!
                                .observe(viewLifecycleOwner, { response -> response?.let { updateUI(it) } })
                        }
                    }
            } else {
                Toast.makeText(context, "Please turn on your location...", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        mLocationRequest.interval = INTERVAL
        mLocationRequest.fastestInterval = FASTEST_INTERVAL
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        mFusedLocationClient?.requestLocationUpdates(
            mLocationRequest,
            mLocationCallback,
            Looper.myLooper()
        )
    }

    private fun checkPermissions(): Boolean {
        return (ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSIONS_REQUEST_LOCATION
        )
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            }
        }
    }
}
