package scrapingsoft.myapplication


import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.cachemanager.CacheManager
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker



class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var ctx = applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        setContentView(R.layout.activity_main)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setBuiltInZoomControls(true)
        map.setMultiTouchControls(true)
        val mapController = map.controller
        mapController.setZoom(15)

        val currentLocation=LocationTracer()


        var location=GeoPoint(0.0,0.0)

        if(currentLocation.getLastKnownLocation(this)!=null)
            location=GeoPoint(currentLocation.getLastKnownLocation(this)!!.latitude,currentLocation.getLastKnownLocation(this)!!.longitude)
        else
            Toast.makeText(this, "Last Known Location Provide by Network is unknown!!!", Toast.LENGTH_SHORT).show()

        mapController.setCenter(location)
        val startMarker = Marker(map)
        startMarker.position = location
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        map.overlays.add(startMarker)


        startMarker.setIcon(getResources().getDrawable(R.drawable.person))
        startMarker.setTitle("My Location")

        var bb=BoundingBox(location.latitude,location.longitude,location.latitude,location.longitude)
        val cm= CacheManager(map)

        cm.downloadAreaAsync(this,bb,map.minZoomLevel,map.maxZoomLevel)
        map.invalidate()


    }
}
