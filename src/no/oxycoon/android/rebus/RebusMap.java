package no.oxycoon.android.rebus;

import java.util.ArrayList;
import java.util.List;

import com.google.android.maps.*;
import com.google.android.maps.MapView.LayoutParams;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.content.*;
import android.graphics.drawable.Drawable;
import android.location.*;

public class RebusMap extends MapActivity{
	private MapView mapView;
    private LocationManager locationManager;
    private Location location;
    private MapController controller;
	private GeoPoint point;
	private MyLocationListener mll;
	
	/**
	 * Variables for drawing post positions.
	 */
	private ArrayList<GeoPoint> thePostPointList;
	private ArrayList<OverlayItem> theOverlayItemList;
	private List<Overlay> mapOverlays;
    
	private String providerName;
	
	private Double lng = 17.427678 * 1E6;
	private Double lat = 68.439267 * 1E6;
	
	private boolean activeRebus;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rebusmap);
		
		mll = new MyLocationListener();
		
		initializeMap();
		initializeLocation();
		
		activeRebus = true;
			
		Bundle extras = getIntent().getExtras();
		if (extras != null){
			if(extras.getDoubleArray("latitude").length > 0){
				activeRebus = false;
			    drawLocations(extras.getDoubleArray("latitude"), extras.getDoubleArray("longitude"));
			}//End if extras.getDoubleArray("latitude").length > 0
		}//End if extras != null
	}
	
	/**
	 * Private methods
	 **/
	
	/**
	 * Initializes the mapView
	 **/
	private void initializeMap(){
		mapView = (MapView)findViewById(R.id.mapview_view);
        LinearLayout zoomLayout = (LinearLayout)findViewById(R.id.mapview_layout_zoom);  

        zoomLayout.addView(mapView.getZoomControls(), 
            new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, 
                LayoutParams.WRAP_CONTENT)); 
        mapView.displayZoomControls(true);
        mapView.setSatellite(false);		
	}
	
	/**
	 * @author Daniel G. Razafimandimby
	 */
	private void initializeLocation(){
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		providerName = LocationManager.GPS_PROVIDER;		
		locationManager.requestLocationUpdates(providerName, 0, 0, mll);
		location = locationManager.getLastKnownLocation(providerName);
		// -------------------------------------

		// Gets user's last known location and center map on this location.
		controller = mapView.getController();
		if (location != null) { //tries to get current location
			Double tempLat = location.getLatitude();
			Double tempLng = location.getLongitude();

			point = new GeoPoint(tempLat.intValue(), tempLng.intValue());
		} //End if location != null
		else { //else sets default location in Narvik, Norway.
			point = new GeoPoint(lat.intValue(), lng.intValue());
		} //End else
		controller.setCenter(point);
		controller.setZoom(5);
	}
	
	/**
	 * @param latArray
	 * @param lngArray
	 * 
	 * Draws pins on the locations of the inputted locations.
	 * 
	 * @author Daniel G. Razafimandimby
	 */
	private void drawLocations(double[] latArray, double[] lngArray){
		if (thePostPointList != null){
			Drawable srcdrawable = this.getResources().getDrawable(R.drawable.pin_red);
		    CustomItemizedOverlay srcitemizedOverlay = new CustomItemizedOverlay(srcdrawable);
		    mapOverlays = mapView.getOverlays();
		    thePostPointList = new ArrayList();
		    theOverlayItemList = new ArrayList();
		    
		    for (int i = 0; i < latArray.length; i++){
		    	thePostPointList.add(new GeoPoint((int)(latArray[i] * 1E6),(int)(lngArray[i] * 1E6)));
		    }
		    

		    for (GeoPoint p: thePostPointList){
		    	theOverlayItemList.add(new OverlayItem(p, "Post from track: [PLACEHOLDER]", "Coordinates: (" + (p.getLatitudeE6()/1E6) + ", " + (p.getLongitudeE6()/1E6) + ")." ));
		    }
		    
		    for (OverlayItem o: theOverlayItemList){
		    	srcitemizedOverlay.addOverlay(o);
		    }
		    mapOverlays.add(srcitemizedOverlay);
		    
		    thePostPointList.clear();		// Clears arrays to save memory
		    theOverlayItemList.clear();		// Clears arrays to save memory
		}//End if thePostPointList != null
	}//End drawLocations()
	
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * @author Daniel
	 **/
	private class MyLocationListener implements LocationListener {
		public void onLocationChanged(Location loca) {
			if(activeRebus){
				Double tempLat = loca.getLatitude() * 1E6;
				Double tempLng = loca.getLongitude() * 1E6;

				//TODO: Need to decide on what this method is supposed to do.
			
				point = new GeoPoint(tempLat.intValue(), tempLng.intValue());
				controller.setCenter(point);
			}
		}

		public void onStatusChanged(String s, int i, Bundle b) {
		}

		public void onProviderDisabled(String s) {
		}

		public void onProviderEnabled(String s) {
		}
	}
}
