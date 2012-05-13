package no.oxycoon.android.rebus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class RebusListViewer extends ListActivity {
	//private String trackList;

	private ArrayList<String> trackNames, trackCreator, trackWinner;
	private ArrayList<Integer> trackStart, trackStop;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		trackNames = new ArrayList<String>();
		trackCreator = new ArrayList<String>();
		trackWinner = new ArrayList<String>();
		trackStart = new ArrayList<Integer>();
		trackStop = new ArrayList<Integer>();

		// TODO: get information from server
		readServerForTracks();
		
		//TODO: Fix up ArrayAdapters for view.
	}

	/**
	 * readServerForTracks()
	 * 
	 * Gets generated xml-file from server. File contains information about
	 * available routes.
	 **/
	private void readServerForTracks() {
		//TODO: Fix NullPointException that occurs in this block.
		//TODO: Fix the parsing to give user the correct listview
		try {
			//TODO: See: https://github.com/narvik-studentradio/Android-Player/blob/master/src/com/nsr/podcast/Podcasts.java
			//				From line 148.|
			URL url = new URL("http://rdb.goldclone.no/beta.xml");
			HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection(); //NullPointer occurs here

			// Needed?
			// BufferedReader in = new BufferedReader(new
			// InputStreamReader(url.openStream()));
			
			// Checks if connection is ok.
			if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				InputStream in = httpConnection.getInputStream();
				Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
				
				//This needed?
				//doc.getDocumentElement().normalize();

				NodeList nodeLst = doc.getElementsByTagName("track");

				// XML-parse loop
				for (int i = 0; i < nodeLst.getLength(); i++) {
					Node fstNode = nodeLst.item(i);

					//TODO: Finish all the parsing. Currently only making a toast for debugging.
					if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
						Element fstElement = (Element) fstNode;
						// ------Gets the track name from the xml-------
						NodeList trackNameList = fstElement.getElementsByTagName("name");
						Element trackNameElement = (Element) trackNameList.item(0);
						NodeList textTNList = trackNameElement.getChildNodes();

						trackNames.add(textTNList.item(0).getNodeValue());

						Toast.makeText(this, textTNList.item(0).getNodeValue(),	5).show();
						// ---------------------
					}
				}	
			}
		} catch (MalformedURLException e) {
			Toast.makeText(this, "URL error: " + e.getMessage(), 10).show();
			return;
		} catch (IOException e) {
			Toast.makeText(this, "IO error: " + e.getMessage(), 10).show();
			return;
		} catch (Exception e) {
			Toast.makeText(this, "Error: " + e.getMessage() + "\n", 10).show();
			return;
		}
	}

	/**
	 * onSelect "listener". Used to return track selection
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		String s = (String) this.getListAdapter().getItem(position);
		Intent result = new Intent();

		//TODO: give detailed information about selected race
		
		//TODO: return race to follow
		result.putExtra("returnResult", s);
		setResult(RESULT_OK, result);
		finish();
	}
}
