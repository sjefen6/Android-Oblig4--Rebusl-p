package no.oxycoon.android.rebus;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import android.content.*;
import android.widget.*;
import android.util.Log;
import android.view.*;

public class RebusActivity extends Activity {
	private Button startMapViewButton, startRaceButton, settingsButton,
			cancelButton, finishedRacebutton;

	private MyLocationListener mll;
	private LocationManager locationManager;

	private boolean activeRebus;

	public static final int SELECT_AVAILABLE_RACES = 1; // responsecode for
														// RebusListViewer
	public static final int SELECT_FINISHED_RACES = 2; // responsecode for
														// RebusListViewer

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
		settingsButton = (Button) findViewById(R.id.main_button_settings);
		cancelButton = (Button) findViewById(R.id.main_button_cancel);
		finishedRacebutton = (Button)findViewById(R.id.main_button_finishedrace);
		
		settingsButton.setOnClickListener(new ButtonHandler());
		startMapViewButton.setOnClickListener(new ButtonHandler());
		startRaceButton.setOnClickListener(new ButtonHandler());
		cancelButton.setOnClickListener(new ButtonHandler());
		finishedRacebutton.setOnClickListener(new ButtonHandler());
		
		activeRebus = false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.v("Start", "RebusActivity: ActivityResult: " + requestCode);
		switch (requestCode) {
		case SELECT_AVAILABLE_RACES: {
			if (resultCode == Activity.RESULT_OK) {
				if (data != null) {
					// TODO: Start a timer notification for time until race
					// starts.
					activeRebus = true;

					// TODO: Start a Proximity Alert with given data.
				} // End if data
			} // End if resultCode
			break;
		} // End case SELECT_AVAILABLE_RACES
		case SELECT_FINISHED_RACES: {
			if (resultCode == Activity.RESULT_OK) {
				if (data != null) {
					Intent intent = new Intent(this, RebusMap.class);

					intent.putExtra("longitude", data.getDoubleArrayExtra("longitude"));
					intent.putExtra("latitude", data.getDoubleArrayExtra("latitude"));

					startActivity(intent);
				} // End if data
			} // End if resultCode
			break;
		} // End case SELECT_FINISHED_RACES
		} // End switch
	} // End onActivityResult()

	/**
	 * @author Daniel
	 *
	 */
	private class ButtonHandler implements View.OnClickListener {
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case R.id.main_button_startmap: {
				Intent intent = new Intent(RebusActivity.this, RebusMap.class);
				intent.putExtra("active", activeRebus);
				startActivity(intent);
				break;
			} // End case R.id.main_button_startmap
			case R.id.main_button_startrace: {
				Log.v("Start", "RebusActivity");
				Intent intent = new Intent(RebusActivity.this, RebusListViewer.class);
				intent.putExtra("newRace", true);
				Log.v("Start", "RebusActivity: Before startActivity");
				//TODO: Make the RebusListViewer look for the intent boolean to decide which xml to read from.
				startActivityForResult(intent, SELECT_AVAILABLE_RACES);
				Log.v("Start", "RebusActivity: After startActivity");
				break;
			} // End case R.id.main_button_startrace
			case R.id.main_button_settings: {
				break;
			} // End case R.id.main_button_settings
			case R.id.main_button_cancel: {
				break;
			} // End case R.id.main_button_cancel
			case R.id.main_button_finishedrace: {
				/**Real method to use.*/
				Intent intent = new Intent(RebusActivity.this, RebusListViewer.class);
				intent.putExtra("newRace", false);
				//TODO: Make the RebusListViewer look for the intent boolean to decide which xml to read from.
				startActivityForResult(intent, SELECT_FINISHED_RACES);
				
				/**Test values for mapView draw*/
//				Intent intent = new Intent(RebusActivity.this, RebusMap.class);
//				
//				intent.putExtra("longitude", new double[] { 2.2, 3.3, 2.3 });
//				intent.putExtra("latitude", new double[] { 2.2, 3.3, 2.3 });
//				intent.putExtra("active", false);
//
//				startActivity(intent);
				break;
			} // End case R.id.main_button_finishedrace
			} // End switch
		} // End onClick()
	} // End class ButtonHandler

	// TODO: Check and return user's location to server.
	/**
	* http://www.firstdroid.com/2010/04/29/android-development-using-gps-to-get-current-location-2/
	*/
	private class MyLocationListener implements LocationListener {
		public void onLocationChanged(Location location) {
			double tempLat = location.getLatitude();
			double tempLng = location.getLongitude();
			
			
		}

		public void onStatusChanged(String s, int i, Bundle b) {
		}

		public void onProviderDisabled(String s) {
		}

		public void onProviderEnabled(String s) {
		}
	} // End class MyLocationListener

}
