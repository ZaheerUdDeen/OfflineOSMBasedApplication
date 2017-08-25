package scrapingsoft.myapplication


import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import org.osmdroid.bonuspack.kml.KmlDocument
import org.osmdroid.bonuspack.location.OverpassAPIProvider
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.Road
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.MapTile
import org.osmdroid.tileprovider.MapTileCache
import org.osmdroid.tileprovider.cachemanager.CacheManager
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.FolderOverlay
import org.osmdroid.views.overlay.Marker



class MainActivity : AppCompatActivity() {
    var kmlDocument = KmlDocument();
    val location= GeoPoint(0.0,0.0);
    var bb=BoundingBox(0.0,0.0,0.0,0.0)
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

       val mtc= MapTileCache();


       val location:GeoPoint

        if(currentLocation!=null)
            location=GeoPoint(currentLocation.getLastKnownLocation(this)!!.latitude,currentLocation.getLastKnownLocation(this)!!.longitude)
        else
            location = GeoPoint(0.0,0.0)

         bb=BoundingBox(location.latitude,location.longitude,location.latitude,location.longitude)
        //mapController.setCenter(map.boundingBox.center)
        mapController.setCenter(location)
        val startMarker = Marker(map)
        startMarker.position = location
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        map.overlays.add(startMarker)




        startMarker.setIcon(getResources().getDrawable(R.drawable.person))
        startMarker.setTitle("My Location");

        val cm= CacheManager(map)
        val  mt=MapTile(map.getZoomLevel(),map.scrollX,map.scrollY);
        //(cm.loadTile(TileSourceFactory.MAPNIK,mt))
         cm.downloadAreaAsync(this,bb,map.minZoomLevel,map.maxZoomLevel);


        //SomeTask(this,location).execute();
        //kmlLoad()

        map.invalidate();



    }
    fun getcitySearch(){
        val overpassProvider = OverpassAPIProvider()
       // val bb=BoundingBox(33.72234080364465,73.10714721679688,33.66392505434982,72.87042617797852)
        Log.d("bounding box1:",""+map.boundingBox.concat(map.boundingBox))
       // val url = overpassProvider.urlForTagSearchKml("amnesti=hospital", bb, 20, 30)
        val url = overpassProvider.urlForPOISearch("area", bb, 20, 30)

        Log.d("bounding box:",""+map.boundingBox)
        val ok = overpassProvider.addInKmlFolder(kmlDocument.mKmlRoot, url)

        val kmlOverlay = kmlDocument.mKmlRoot.buildOverlay(map, null, null, kmlDocument) as FolderOverlay
        map.overlays.add(kmlOverlay)
        map.getOverlays().add(kmlOverlay)
        downloadKMLFile(url)
    }
    public fun kmlLoad(){

        val localOffliceFile = kmlDocument.getDefaultPathForAndroid("my_city.geojson")
        if(localOffliceFile.exists()) {
            kmlDocument.parseGeoJSON(localOffliceFile)
            loadofflineTiles();
            Log.d("offline:","yesss")
        }
        else {
            Log.d("online:","yesss")
            SomeTask(this, location).execute();
        }
    }
    public fun loadofflineTiles(){
        val kmlOverlay = kmlDocument.mKmlRoot.buildOverlay(map, null, null, kmlDocument) as FolderOverlay
        map.getOverlays().add(kmlOverlay);
        val  bb: BoundingBox = kmlDocument.mKmlRoot.getBoundingBox()
        map.getController().setCenter(bb.getCenter());
    }
    public fun downloadKMLFile(url:String ){
        //kmlDocument.parseKMLUrl("http://mapsengine.google.com/map/kml?forcekml=1&mid=z6IJfj90QEd4.kUUY9FoHFRdE")
        kmlDocument.parseKMLUrl(url)

        val localFile = kmlDocument.getDefaultPathForAndroid("my_city.kml")
        kmlDocument.saveAsKML(localFile)
        //loadofflineTiles();
    }
    inner class SomeTask(val cont: Context,val geoPoint:GeoPoint) : AsyncTask<Void, Void, String>() {

        override fun doInBackground(vararg params: Void?): String? {



            val endPoint = GeoPoint(33.729388, 73.093146)
            val startPoint = GeoPoint(33.69657, 72.97735)

            //routFromStartToEndPoin(geoPoint,endPoint);
            //downloadKMLFile();
            getcitySearch()
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
                nodeMarker.setSnippet(node.mInstructions);
                nodeMarker.setSubDescription(Road.getLengthDurationText(cont, node.mLength, node.mDuration));
                val icon = resources.getDrawable(R.drawable.marker_friend_on)
                nodeMarker.setImage(icon)
            }


        }



    }
}
