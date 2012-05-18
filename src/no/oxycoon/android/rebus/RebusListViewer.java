package no.oxycoon.android.rebus;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class RebusListViewer extends ListActivity {
	// private String trackList;
	private URL theUrl;
	private boolean activeRace;
	
	private ServerContactTask serverContact = null;

	private ArrayList<Track> theTrackList;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		theTrackList = new ArrayList<Track>();
		
		Bundle extras = getIntent().getExtras();
		Object retained = getLastNonConfigurationInstance();
		
		if(extras != null){
			activeRace = extras.getBoolean("active");
			if(activeRace){
				try {
					theUrl = new URL("http://rdb.goldclone.no/beta.xml");//TODO: get appropriate url
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			}//end if activeRace
			else if(!activeRace){
				try {
					theUrl = new URL("http://rdb.goldclone.no/beta.xml");//TODO: get appropriate url
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			}//end if !activeRace
			if(retained == null){
				serverContact = new ServerContactTask();
				serverContact.execute(new String[]{theUrl.toString()}); //TODO: get propper parameter;
			}//end if retained==null
			else{
				theTrackList =(ArrayList<Track>)retained;
			}//end else
		}//end if extras != null
		
		
		else{
			Toast.makeText(this, "There was an error: Unable to get extras.", 5);
		}//end else

		// TODO: get information from server

		// TODO: Fix up ArrayAdapters for view.
	}

	private class ServerContactTask extends
			AsyncTask<String, String, ArrayList<Track>> {
		private ProgressDialog pd;

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected void onProgressUpdate(String... values) {

		}

		/**
		 * doInBackground()
		 * 
		 * Gets generated xml-file from server. File contains information about
		 * available routes.
		 **/
		@Override
		protected ArrayList<Track> doInBackground(String... params) {

			// TODO: Fix the parsing to give user the correct listview
			try {
				// TODO: See:
				// https://github.com/narvik-studentradio/Android-Player/blob/master/src/com/nsr/podcast/Podcasts.java
				// From line 148.|
				// URL url = new
				// URL("http://earthquake.usgs.gov/eqcenter/catalogs/1day-M2.5.xml");
				HttpURLConnection httpConnection = (HttpURLConnection) theUrl
						.openConnection(); // NullPointer occurs here

				// Needed?
				// BufferedReader in = new BufferedReader(new
				// InputStreamReader(url.openStream()));

				// Checks if connection is ok.
				if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
					InputStream in = httpConnection.getInputStream();
					Document doc = DocumentBuilderFactory.newInstance()
							.newDocumentBuilder().parse(in);

					// This needed?
					// doc.getDocumentElement().normalize();

					NodeList nodeLst = doc.getElementsByTagName("track");

					// XML-parse loop
					for (int i = 0; i < nodeLst.getLength(); i++) {
						Node fstNode = nodeLst.item(i);

						// TODO: Finish all the parsing. Currently only making a
						// toast for debugging.
						if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
							Element fstElement = (Element) fstNode;
							// ------Gets the track name from the xml-------
							NodeList trackNameList = fstElement
									.getElementsByTagName("name");
							Element trackNameElement = (Element) trackNameList
									.item(0);
							NodeList textTNList = trackNameElement
									.getChildNodes();

							Toast.makeText(RebusListViewer.this,
									textTNList.item(0).getNodeValue(), 5)
									.show();
							// ---------------------
						}//End if
					}//End for i < nodeLst.getLength()
				}//End if httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK
			} catch (MalformedURLException e) {
				Toast.makeText(RebusListViewer.this,
						"URL error: " + e.getMessage(), 10).show();
			} catch (IOException e) {
				Toast.makeText(RebusListViewer.this,
						"IO error: " + e.getMessage(), 10).show();
			} catch (Exception e) {
				Toast.makeText(RebusListViewer.this,
						"Error: " + e.getMessage() + "\n", 10).show();
			}
			return null;
		}//end doInBackground
	}//end class ServerContactTask

	/**
	 * onSelect "listener". Used to return track selection
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		String s = (String) this.getListAdapter().getItem(position);
		Intent result = new Intent();

		
		// TODO: give detailed information about selected race

		// TODO: return race to follow
		result.putExtra("returnResult", s);
		setResult(RESULT_OK, result);
		finish();
	}
}
