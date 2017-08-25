package scrapingsoft.myapplication

import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle

/**
 * Created by Zephyr on 8/19/2017.
 */

class LocationTracer : LocationListener {
    private var locationManager: LocationManager? = null

    fun getLastKnownLocation(context: Context): Location? {
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var location: Location = locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        if (location == null) {

            locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    0, 0f, this)
            location = locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            return location

        } else {
            return location

        }
    }

    override fun onLocationChanged(location: Location) {

    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {

    }

    override fun onProviderEnabled(provider: String) {

    }

    override fun onProviderDisabled(provider: String) {

    }
}
