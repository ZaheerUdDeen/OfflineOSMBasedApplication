package scrapingsoft.myapplication


import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
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
        mapController.setZoom(12)

        val currentLocation=LocationTracer()



        val location: GeoPoint

        if(currentLocation!=null)
            location=GeoPoint(currentLocation.getLastKnownLocation(this)!!.latitude,currentLocation.getLastKnownLocation(this)!!.longitude)
        else
            location = GeoPoint(33.729388, 73.093146)



        mapController.setCenter(location)
        val startMarker = Marker(map)
        startMarker.position = location
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        map.overlays.add(startMarker)

        map.invalidate();
        startMarker.setIcon(getResources().getDrawable(R.drawable.marker_car_on))
        startMarker.setTitle("Zaheer");

        map.setTileSource(TileSourceFactory.MAPNIK)



        SomeTask(this).execute();
        map.invalidate();


    }




    inner class SomeTask(val cont: Context) : AsyncTask<Void, Void, String>() {
        override fun doInBackground(vararg params: Void?): String? {


            val endPoint = GeoPoint(33.729388, 73.093146)
            val startPoint = GeoPoint(33.69657, 72.97735)

             routFromStartToEndPoin(startPoint,endPoint);

            return null;
        }

        override fun onPreExecute() {
            super.onPreExecute()

        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

        }

        public fun routFromStartToEndPoin(startPoint: GeoPoint, endPoint: GeoPoint) {

            val roadManager = OSRMRoadManager(cont)
            val waypoints = ArrayList<GeoPoint>()
            waypoints.add(startPoint)
            waypoints.add(endPoint)

            val road = roadManager.getRoad(waypoints)

            val roadOverlay = RoadManager.buildRoadOverlay(road)

            map.getOverlays().add(roadOverlay);

            val nodeIcon = resources.getDrawable(R.drawable.marker_node)
            for (i in 0..road.mNodes.lastIndex - 1) {
                val node = road.mNodes[i]
                val nodeMarker = Marker(map)
                nodeMarker.position = node.mLocation
                nodeMarker.setIcon(nodeIcon)
                nodeMarker.title = "Step " + i
                map.overlays.add(nodeMarker)
            }
        }

    }
}
