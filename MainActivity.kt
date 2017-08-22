package scrapingsoft.myapplication


import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.osmdroid.bonuspack.kml.KmlDocument
import org.osmdroid.bonuspack.routing.MapQuestRoadManager
import org.osmdroid.bonuspack.routing.Road
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBoxE6
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.FolderOverlay
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
            location = GeoPoint(0.0,0.0)



        mapController.setCenter(location)
        val startMarker = Marker(map)
        startMarker.position = location
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        map.overlays.add(startMarker)

        map.invalidate();
        startMarker.setIcon(getResources().getDrawable(R.drawable.marker_car_on))
        startMarker.setTitle("Zaheer");

        map.setTileSource(TileSourceFactory.MAPNIK)



        SomeTask(this,location).execute();

        map.invalidate();



    }




    inner class SomeTask(val cont: Context,val geoPoint:GeoPoint) : AsyncTask<Void, Void, String>() {

        override fun doInBackground(vararg params: Void?): String? {



            val endPoint = GeoPoint(33.729388, 73.093146)
            val startPoint = GeoPoint(33.69657, 72.97735)

            routFromStartToEndPoin(geoPoint,endPoint);
           // kmlLoad()
            return null;
        }

        override fun onPreExecute() {
            super.onPreExecute()

        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

        }

        public fun routFromStartToEndPoin(startPoint: GeoPoint, endPoint: GeoPoint) {

           // val roadManager = OSRMRoadManager(cont)

            val roadManager = MapQuestRoadManager("T8T3EM9KDQzzwTe61zjPuuOEwSS1QkV6")
            roadManager.addRequestOption("routeType=bicycle")
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
                nodeMarker.setSnippet(node.mInstructions);
                nodeMarker.setSubDescription(Road.getLengthDurationText(cont, node.mLength, node.mDuration));
                val icon = resources.getDrawable(R.drawable.marker_friend_on)
                nodeMarker.setImage(icon)
            }


        }
        public fun kmlLoad(){
            val kmlDocument = KmlDocument()
            kmlDocument.parseKMLUrl("http://mapsengine.google.com/map/kml?forcekml=1&mid=z6IJfj90QEd4.kUUY9FoHFRdE")
            val kmlOverlay = kmlDocument.mKmlRoot.buildOverlay(map, null, null, kmlDocument) as FolderOverlay
            map.getOverlays().add(kmlOverlay);
            val  bb: BoundingBoxE6 = kmlDocument.mKmlRoot.getBoundingBox() as BoundingBoxE6
            map.zoomToBoundingBox(bb);

        }
    }
}
