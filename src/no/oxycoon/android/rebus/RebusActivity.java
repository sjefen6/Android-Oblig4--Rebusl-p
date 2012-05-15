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
	}

	/**
	 * Starts RebusListViewer and waits for a returned value.
	 */
	public void startRace(int i) {
		if (i == SELECT_AVAILABLE_RACES) {
			startActivityForResult(new Intent(RebusActivity.this,
					RebusListViewer.class), SELECT_AVAILABLE_RACES);
		} // End if SELECT_AVAILABLE_RACES
		else if (i == SELECT_FINISHED_RACES) {
			startActivityForResult(new Intent(RebusActivity.this,
					RebusListViewer.class), SELECT_FINISHED_RACES);
		} // End if SELECT_FINISHED_RACES
	} // End startRace()

	/**
	 * Starts RebusMap
	 */
	public void startMapView() {
		startActivity(new Intent(RebusActivity.this, RebusMap.class));
	} // End startMapView()

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

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
					// TODO: Start up RebusMap with an intent to draw circles on
					// post locations.
					// TODO: Test method

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

	private class ButtonHandler implements View.OnClickListener {
		// TODO: Fix these cases. Will need more buttons and cases later.
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case R.id.main_button_startmap: {
				startMapView();
				break;
			} // End case R.id.main_button_startmap
			case R.id.main_button_startrace: {
				startRace(SELECT_AVAILABLE_RACES);
				break;
			} // End case R.id.main_button_startrace
			case R.id.main_button_settings: {
				break;
			} // End case R.id.main_button_settings
			case R.id.main_button_cancel: {
				break;
			} // End case R.id.main_button_cancel
			case R.id.main_button_finishedrace: {
				Intent intent = new Intent(RebusActivity.this, RebusMap.class);
				
				intent.putExtra("longitude", new double[] { 2.2, 3.3, 2.3 });
				intent.putExtra("latitude", new double[] { 2.2, 3.3, 2.3 });

				startActivity(intent);
			} // End case R.id.main_button_finishedrace
			} // End switch
		} // End onClick()
	} // End class ButtonHandler

	// TODO: Check and possibly return position, not sure on what yet.
	// See:
	// http://www.firstdroid.com/2010/04/29/android-development-using-gps-to-get-current-location-2/
	private class MyLocationListener implements LocationListener {
		public void onLocationChanged(Location location) {

		}

		public void onStatusChanged(String s, int i, Bundle b) {
		}

		public void onProviderDisabled(String s) {
		}

		public void onProviderEnabled(String s) {
		}
	} // End class MyLocationListener

}
