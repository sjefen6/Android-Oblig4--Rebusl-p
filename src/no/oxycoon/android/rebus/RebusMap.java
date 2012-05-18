package no.oxycoon.android.rebus;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.android.maps.*;
import com.google.android.maps.MapView.LayoutParams;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.app.ProgressDialog;
import android.content.*;
import android.graphics.drawable.Drawable;
import android.location.*;

public class RebusMap extends MapActivity {
	private MapView mapView;
	private LocationManager locationManager;
	private Location location;
	private MapController controller;
	private GeoPoint point;
	private MyLocationListener mll;
	private CustomItemizedOverlay postOverlay, usersOverlay, userOverlay;
	private Drawable userDraws;

	/**
	 * Variables for drawing post positions.
	 */
	private List<Overlay> mapOverlays;

	private String providerName;

	// Default location in Narvik
	private Double lng = 17.427678 * 1E6;
	private Double lat = 68.439267 * 1E6;

	private boolean activeRebus;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rebusmap);

		mll = new MyLocationListener();
		userDraws = this.getResources().getDrawable(R.drawable.pin_red); //TODO: get another icon for users.
		userOverlay = new CustomItemizedOverlay(userDraws);

		initializeMap();
		initializeLocation();

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			activeRebus = extras.getBoolean("active");
			
			if (extras.getDoubleArray("latitude").length > 0) {
				drawLocations(extras.getDoubleArray("latitude"),
						extras.getDoubleArray("longitude"));
			}// End if extras.getDoubleArray("latitude").length > 0
		}// End if extras != null

	}// End onCreate()

	/**
	 * Private methods
	 **/

	/**
	 * Initializes the mapView
	 **/
	private void initializeMap() {
		mapView = (MapView) findViewById(R.id.mapview_view);
		LinearLayout zoomLayout = (LinearLayout) findViewById(R.id.mapview_layout_zoom);

		zoomLayout.addView(mapView.getZoomControls(),
				new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT));
		mapView.displayZoomControls(true);
		mapView.setSatellite(false);
		
		mapOverlays = mapView.getOverlays();
	}// End initializeMap

	/**
	 * @author Daniel G. Razafimandimby
	 */
	private void initializeLocation() {
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		providerName = LocationManager.GPS_PROVIDER;
		locationManager.requestLocationUpdates(providerName, 0, 0, mll);
		location = locationManager.getLastKnownLocation(providerName);
		// -------------------------------------

		// Gets user's last known location and center map on this location.
		controller = mapView.getController();
		if (location != null) { // tries to get current location
			Double tempLat = location.getLatitude();
			Double tempLng = location.getLongitude();

			point = new GeoPoint(tempLat.intValue(), tempLng.intValue());
		} // End if location != null
		else { // else sets default location in Narvik, Norway.
			point = new GeoPoint(lat.intValue(), lng.intValue());
		} // End else
		controller.setCenter(point);
		controller.setZoom(5);
	}// End initializeLocation()

	/**
	 * @param latArray
	 * @param lngArray
	 * 
	 *            Draws pins on the locations of the inputted locations.
	 * 
	 * @author Daniel G. Razafimandimby
	 */
	private void drawLocations(double[] latArray, double[] lngArray) {
		if(latArray.length != lngArray.length){
			Toast.makeText(this, "An error has occured: different amount of longitudes and latitudes.", 5);
			return;
		} else if(activeRebus){
			Toast.makeText(this, "A race is currently active. Will not draw posts.", 5);
			return;
		}
		Drawable postDraws = this.getResources().getDrawable(R.drawable.pin_red);
		postOverlay = new CustomItemizedOverlay(postDraws);

		for (int i = 0; i < latArray.length - 1; i++) {
			postOverlay.addOverlay(new OverlayItem(new GeoPoint(
					(int) (latArray[i] * 1E6), (int) (lngArray[i] * 1E6)),
					"Post from track: [PLACEHOLDER]", "Coordinates: ("
							+ latArray[i] + ", " + lngArray[i] + ")."));
		}// End for

		mapOverlays.add(postOverlay);
	}// End drawLocations()

	// TODO: Test method
	private void drawParticipants(UserData[] users) {
		if(activeRebus){
			usersOverlay = new CustomItemizedOverlay(userDraws);

			for(int i = 0; i < users.length-1; i++){
				usersOverlay.addOverlay(new OverlayItem(new GeoPoint((int)(users[i].Latitude()*1E6),(int)(users[i].Longitude()*1E6)), "", ""));
			}// End for
			
			mapOverlays.add(usersOverlay);

		}// End activeRebus
	}// End drawPraticipants

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	/**
	 * @author Daniel
	 **/
	//TODO: Test this
	private class MyLocationListener implements LocationListener {
		public void onLocationChanged(Location loca) {
			if (activeRebus) {
				Double tempLat = loca.getLatitude() * 1E6;
				Double tempLng = loca.getLongitude() * 1E6;

				point = new GeoPoint(tempLat.intValue(), tempLng.intValue());
				userOverlay.clear();
				userOverlay.addOverlay(new OverlayItem(point, "You", ""));

				mapView.invalidate();
				
				controller.setCenter(point);
			}// End if activeRebus
		}// End onLocationChanged()

		public void onStatusChanged(String s, int i, Bundle b) {
		}

		public void onProviderDisabled(String s) {
		}

		public void onProviderEnabled(String s) {
		}
	}// End class MyLocationListener

	//TODO: fix callback to get user positions
	/**
	 * @author Daniel
	 * 
	 * Original source from NSR:
	 * https://github.com/narvik-studentradio/Android-Player/blob/master/src/com/nsr/podcast/Podcasts.java
	 */
	private class PositionTask extends AsyncTask<String, String, ArrayList<UserData>> {
		private ProgressDialog pd;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// resources.getString(R.string.podcasts_downloading), true, false);
		}

		@Override
		protected void onProgressUpdate(String... values) {
			pd.setMessage(values[0]);
		}

		@Override
		protected ArrayList<UserData> doInBackground(String... address) {
			try {
				URL url = new URL(address[0]);
				HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();

				if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

					InputStream in = httpConnection.getInputStream();
					Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
					Element docElement = doc.getDocumentElement();

					// publishProgress(resources.getString(R.string.podcasts_parsing));

					NodeList outlines = docElement.getElementsByTagName("item");

					ArrayList<UserData> result = new ArrayList<UserData>();

					for (int i = 0; i < outlines.getLength(); i++) {
						Element ele = (Element) outlines.item(i);

						String eleTitle = ele.getElementsByTagName("title").item(0).getFirstChild().getNodeValue();
						Element enclosure = (Element) ele.getElementsByTagName("enclosure").item(0);
						if (enclosure == null)
							continue;
						String eleUrl = enclosure.getAttribute("url");
						if (eleUrl.equals(""))
							continue;
						String eleDate = ele.getElementsByTagName("pubDate").item(0).getFirstChild().getNodeValue();

						// PodcastData thisCast = new PodcastData();

						// thisCast.description = eleTitle;
						// thisCast.date = eleDate;
						// thisCast.url = eleUrl;

						// result.add(thisCast);
					}
					return result;
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(ArrayList<UserData> result) {
			// data = result;
			// initList();
			pd.dismiss();
		}
	}
}
