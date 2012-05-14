package no.oxycoon.android.rebus;

import com.google.android.maps.*;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.app.PendingIntent;
import android.content.*;
import android.location.*;

public class RebusMap extends MapActivity{
	private MapView mapView;
    private LocationManager locationManager;
    private Location location;
    private MapController controller;
	private GeoPoint point;
	private MyLocationListener mll;
    
	private String providerName;
	
	private Double lng = 17.427678 * 1E6;
	private Double lat = 68.439267 * 1E6;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rebusmap);
		
		mll = new MyLocationListener();
		
		initializeMap();
		initializeLocation();
		
		
		Bundle extras = getIntent().getExtras();
		if (extras != null){
			Toast.makeText(this, "This intent contains data", 5).show();
		}
	}
	
	
	/**
	 * Private methods
	 **/
	
	/**
	 * Initializes the mapView
	 **/
	private void initializeMap(){
		mapView = (MapView)findViewById(R.id.mapview_view);
		mapView.displayZoomControls(true);
		mapView.setSatellite(false);
	}
	
	/**
	 * 
	 */
	private void initializeLocation(){
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		providerName = LocationManager.GPS_PROVIDER;		
		locationManager.requestLocationUpdates(providerName, 0, 0, mll);
		location = locationManager.getLastKnownLocation(providerName);
		// -------------------------------------

		//TODO: Get user's current location and center map on user's location
		controller = mapView.getController();
		if (location != null) { //tries to get current location
			Double tempLat = location.getLatitude();
			Double tempLng = location.getLongitude();

			point = new GeoPoint(tempLat.intValue(), tempLng.intValue());
		} else { //else sets default location in Narvik, Norway.
			point = new GeoPoint(lat.intValue(), lng.intValue());
		}
		controller.setCenter(point);
		controller.setZoom(5);
	}
	
	//TODO: Draw icons on map where posts of selected track was.
	private void drawLocations(double[] latArray, double[] lngArray){
		
	}
	
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * @author Daniel
	 *
	 **/
	private class MyLocationListener implements LocationListener {
		public void onLocationChanged(Location loca) {
			Double tempLat = loca.getLatitude() * 1E6;
			Double tempLng = loca.getLongitude() * 1E6;

			//TODO: Need to decide on what this method is supposed to do.
			
			point = new GeoPoint(tempLat.intValue(), tempLng.intValue());
			controller.setCenter(point);
		}

		public void onStatusChanged(String s, int i, Bundle b) {
		}

		public void onProviderDisabled(String s) {
		}

		public void onProviderEnabled(String s) {
		}
	}
}
