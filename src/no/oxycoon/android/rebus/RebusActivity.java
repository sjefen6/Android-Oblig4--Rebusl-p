package no.oxycoon.android.rebus;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import android.content.*;
import android.widget.*;
import android.view.*;

public class RebusActivity extends Activity {
	private Button startMapViewButton, startRaceButton, settingsButton, cancelButton;

	private MyLocationListener mll;
	private LocationManager locationManager;

	public static final int REBUSLISTVIEWER = 2;	// responsecode for RebusListViewer

	/**
	 * Values for proximity alert
	 * */
	public static long minimum_distance_for_update = 5; // meters
	public static long minimum_time_for_update = 5000; // milliseconds

	private static final String POINT_LATITUDE_KEY = "POINT_LATITUDE_KEY";
	private static final String POINT_LONGITUDE_KEY = "POINT_LONGITUDE_KEY";

	private static final long RADIUS_FOR_POINT = 1000; // meters
	private static final long PROXY_ALERT_EXPIRATION = -1;

	private static final String PROX_ALERT_INTENT = "no.oxycoon.android.rebus.ProximityAlert";
	/**
	 * End
	 * */

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		startMapViewButton = (Button) findViewById(R.id.main_button_startmap);
		startRaceButton = (Button) findViewById(R.id.main_button_startrace);
		settingsButton = (Button)findViewById(R.id.main_button_settings);
		cancelButton = (Button)findViewById(R.id.main_button_cancel);
		
		settingsButton.setOnClickListener(new ButtonHandler());
		startMapViewButton.setOnClickListener(new ButtonHandler());
		startRaceButton.setOnClickListener(new ButtonHandler());
		cancelButton.setOnClickListener(new ButtonHandler());
	}
	
	/**
	 * Starts RebusListViewer and waits for a returned value.
	 */
	public void startRace() {
		Toast.makeText(RebusActivity.this, "testing startRace()", 10).show();
		startActivityForResult(new Intent(RebusActivity.this,
				RebusListViewer.class), REBUSLISTVIEWER);
	}

	/**
	 * Starts RebusMap
	 */
	public void startMapView() {
		startActivity(new Intent(RebusActivity.this, RebusMap.class));
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
			case (REBUSLISTVIEWER): {
				if (resultCode == Activity.RESULT_OK) {
					if (data != null) {
						//TODO: Start a timer notification for time until race starts.
						
						
						//TODO: Start a Proximity Alert with given data.
					}
				}
			}
		}
	}

	private class ButtonHandler implements View.OnClickListener {
		//TODO: Fix these cases. Will need more buttons and cases later.
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case R.id.main_button_startmap:
				startMapView();
				break;
			case R.id.main_button_startrace:
				startRace();
				break;
			case R.id.main_button_settings:
				break;
			case R.id.main_button_cancel:
				break;
			}
		}
	}

	
	//TODO: Check and possibly return position, not sure on what yet.
	//See: http://www.firstdroid.com/2010/04/29/android-development-using-gps-to-get-current-location-2/
	private class MyLocationListener implements LocationListener {
		public void onLocationChanged(Location location) {

		}

		public void onStatusChanged(String s, int i, Bundle b) {
		}

		public void onProviderDisabled(String s) {
		}

		public void onProviderEnabled(String s) {
		}
	}

}
