package no.oxycoon.android.rebus;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class RebusListViewer extends ListActivity {
	// private String trackList;
	private URL theUrl;
	private boolean activeRace;
	
	private ServerContactTask serverContact = null;

	private ArrayList<Track> theTrackList;
	private ArrayList<String> theNameList;
	
	private ArrayAdapter<String> adapter;
	
	//popup variables
	private TextView popup_name, popup_posts, popup_time;
	private Button popup_cancel, popup_confirm;
	private PopupWindow pw;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		theTrackList = new ArrayList<Track>();
		theNameList = new ArrayList<String>();
		
		Bundle extras = getIntent().getExtras();
		Object retained = getLastNonConfigurationInstance();
		
//		if (android.os.Build.VERSION.SDK_INT > 9) {
//			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
//					.permitAll().build();
//			StrictMode.setThreadPolicy(policy);
//		}//End if

		String theStringUrl = "http://rdb.goldclone.no:80/api.php?target=tracks";
		//theUrl = new URL("http://213.166.188.87:80/api.php?target=tracks");
		//theUrl= new URL("http://www.google.com");
		if(extras != null){
			Log.v("Start", "RebusListViewer: onCreate - extras not null");
			activeRace = extras.getBoolean("newRace");
			if(retained == null){
				Log.v("Start", "RebusListViewer: onCreate - retained is null");
				serverContact = new ServerContactTask();
				Log.v("Start", "RebusListViewer: onCreate - serverContact before execute");
				serverContact.execute(new String[]{theStringUrl});
				Log.v("Start", "RebusListViewer: onCreate - serverContact.executed");
			}//end if retained==null
			else{
				Log.v("Start", "RebusListViewer: onCreate - retained not null");
				theTrackList =(ArrayList<Track>)retained;
			}//end else
		}//end if extras != null		
		else{
			Toast.makeText(this, "There was an error: Unable to get extras.", 5);
		}//end else

		
//		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, theNameList);
//		setListAdapter(adapter);
	}//End onCreate()

	private class ServerContactTask extends
			AsyncTask<String, String, ArrayList<Track>> {
		private ProgressDialog pd;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = ProgressDialog.show(RebusListViewer.this, "Please wait.", "Downloading data.", true, false);
		}//End onPreExecute()

		@Override
		protected void onProgressUpdate(String... values) {
			pd.setMessage(values[0]);
		}//End onProgressUpdate()
		
		protected void onPostExecute(ArrayList<Track> result){
			pd.dismiss();
			adapter = new ArrayAdapter<String>(RebusListViewer.this, android.R.layout.simple_list_item_1, theNameList);
			setListAdapter(adapter);
		}

		/**
		 * doInBackground()
		 * 
		 * Gets generated xml-file from server. File contains information about
		 * available routes.
		 **/
		@Override
		protected ArrayList<Track> doInBackground(String... params) {
			Log.v("http", "RebusListViewer: doInBackground");
			try {
				HttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setSoTimeout(httpParams, 30000);
				
				HttpClient theClient = new DefaultHttpClient(httpParams);
				
				HttpGet method = new HttpGet(new URI(params[0]));
				
				HttpResponse response = theClient.execute(method);
				Log.v("http", "HttpResponse executed, response: ");

					String tempName="", tempWinner="", tempCreator="";
					long tempStart, tempStop;
					int tempId;
					
					InputStream in = response.getEntity().getContent();
					Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);

					NodeList nodeLst = doc.getElementsByTagName("track");

					// XML-parse loop
					for (int i = 0; i < nodeLst.getLength(); i++) {
						Node fstNode = nodeLst.item(i);
						Log.v("http", "RebusListViewer inside parse loop: " + i);

						// TODO: Test parse
						if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
							Log.v("http", "RebusListViewer inside parse loop's if");
							
							Element fstElement = (Element) fstNode;
							// ------Gets the track name from the xml-------
							Log.v("parse", "Pre-Name");
							NodeList trackNameList = fstElement.getElementsByTagName("name");
							Element trackNameElement = (Element) trackNameList.item(0);
							NodeList textTNList = trackNameElement.getChildNodes();

							tempName = textTNList.item(0).getNodeValue();
							Log.v("parse", "Post-Name");
							// -----------Gets track creator from xml----------
							Log.v("parse", "Pre-creator");
							NodeList creatorNameList = fstElement.getElementsByTagName("creator");
							Element creatorNameElement = (Element) creatorNameList.item(0);
							NodeList textCNList = creatorNameElement.getChildNodes();
							
							tempCreator = textCNList.item(0).getNodeValue();
							Log.v("parse", "Post-creator");
							// ----------Gets track winner from xml-----------
							if(!activeRace){
								Log.v("parse", "Pre-winner");
								NodeList winnerNameList = fstElement.getElementsByTagName("winner");
								Element winnerNameElement = (Element) winnerNameList.item(0);
								NodeList textWNList = winnerNameElement.getChildNodes();
							
								tempWinner = textWNList.item(0).getNodeValue();
								Log.v("parse", "Post-winner");
							}
							// ----------Gets track start time from xml-----------
							Log.v("parse", "Pre-start");
							NodeList startTimeList = fstElement.getElementsByTagName("start_ts");
							Element startTimeElement = (Element) startTimeList.item(0);
							NodeList longStartList = startTimeElement.getChildNodes();
							
							tempStart = Long.parseLong(longStartList.item(0).getNodeValue());
							Log.v("parse", "Post-start");
							// ----------Gets track stop time from xml-----------
							Log.v("parse", "Pre-stop");
							NodeList stopTimeList = fstElement.getElementsByTagName("start_ts");
							Element stopTimeElement = (Element) stopTimeList.item(0);
							NodeList longStopList = stopTimeElement.getChildNodes();
							
							tempStop = Long.parseLong(longStopList.item(0).getNodeValue());
							Log.v("parse", "Post-stop");
							// ----------Gets track id from xml--------------
							Log.v("parse", "Pre-id");
							NodeList idList = fstElement.getElementsByTagName("id");
							Element idElement = (Element) idList.item(0);
							NodeList intIdList = idElement.getChildNodes();
							
							tempId = Integer.parseInt(intIdList.item(0).getNodeValue());
							Log.v("parse", "Post-id");
							// ------------------------------------------------------
							
							//Checks if the user is looking for a race to sign up for or a finished race.
							if(activeRace && tempStart > (System.currentTimeMillis() / 1000L)){
								theTrackList.add(new Track(tempId, tempName, tempCreator, tempStart, tempStop));
								theNameList.add(tempName);
							}//end if activeRace && tempStart > (System.currentTimeMillis() / 1000L
							else if(!activeRace && tempStop < (System.currentTimeMillis() / 1000L)){
								theTrackList.add(new Track(tempId, tempName, tempCreator, tempWinner, tempStart, tempStop));
								theNameList.add(tempName);
							}//end else if !activeRace && tempStop < (System.currentTimeMillis() / 1000L
							
						}//End if
					}//End for i < nodeLst.getLength()
			}//End try 
			catch (MalformedURLException e) {
				Toast.makeText(RebusListViewer.this,
						"URL error: " + e.getMessage(), 10).show();
			}//End catch
			catch (IOException e) {
				Toast.makeText(RebusListViewer.this,
						"IO error: " + e.getMessage(), 10).show();
			}//End catch
			catch (Exception e) {
				Toast.makeText(RebusListViewer.this,
						"Error: " + e.getMessage() + "\n", 10).show();
			}//End catch
			
			return theTrackList;
		}//end doInBackground()
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
	}//End onListItemClick()
}
