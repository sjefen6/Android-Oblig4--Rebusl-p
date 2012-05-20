package no.oxycoon.android.rebus;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;

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
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
	private ServerSendTask serverSend = null;

	private ArrayList<Track> theTrackList;
	private ArrayList<String> theNameList;
	
	private ArrayAdapter<String> adapter;
	
	//popup variables
	private TextView popup_name, popup_posts, popup_time;
	private Button popup_cancel, popup_confirm;
	private PopupWindow pw;
	private int selectedItem;
	
	private String uname, upwd;

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

		String theStringUrl = "http://rdb.goldclone.no/?format=xml&target=tracks";
		//theUrl = new URL("http://213.166.188.87:80/api.php?target=tracks");
		//theUrl= new URL("http://www.google.com");
		if(extras != null){
			activeRace = extras.getBoolean("newRace");
			
			if(activeRace){
				uname = extras.getString("username");
				upwd = extras.getString("password");
			}
			
			if(retained == null){
				serverContact = new ServerContactTask();
				serverContact.execute(new String[]{theStringUrl});
			}//end if retained==null
			else{
				theTrackList =(ArrayList<Track>)retained;
			}//end else
		}//end if extras != null		
		else{
			Toast.makeText(this, "There was an error: Unable to get extras.", 5);
		}//end else

		
//		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, theNameList);
//		setListAdapter(adapter);
	}//End onCreate()

	private class ServerContactTask extends	AsyncTask<String, String, ArrayList<Track>> {
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
			try {
				HttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setSoTimeout(httpParams, 30000);
				
				HttpClient theClient = new DefaultHttpClient(httpParams);
				
				HttpGet method = new HttpGet(new URI(params[0]));
				
				HttpResponse response = theClient.execute(method);

					String tempName="", tempWinner="", tempCreator="";
					long tempStart, tempStop;
					int tempId;
					
					InputStream in = response.getEntity().getContent();
					Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);

					NodeList nodeLst = doc.getElementsByTagName("track");

					// XML-parse loop
					for (int i = 0; i < nodeLst.getLength(); i++) {
						Node fstNode = nodeLst.item(i);

						if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
							Element fstElement = (Element) fstNode;
							// ------Gets the track name from the xml-------
							NodeList trackNameList = fstElement.getElementsByTagName("name");
							Element trackNameElement = (Element) trackNameList.item(0);
							NodeList textTNList = trackNameElement.getChildNodes();

							tempName = textTNList.item(0).getNodeValue();
							// -----------Gets track creator from xml----------
							NodeList creatorNameList = fstElement.getElementsByTagName("creator");
							Element creatorNameElement = (Element) creatorNameList.item(0);
							NodeList textCNList = creatorNameElement.getChildNodes();
							
							tempCreator = textCNList.item(0).getNodeValue();
							// ----------Gets track winner from xml-----------
							try{
								NodeList winnerNameList = fstElement.getElementsByTagName("winner");
								Element winnerNameElement = (Element) winnerNameList.item(0);
								NodeList textWNList = winnerNameElement.getChildNodes();			
								
								tempWinner = textWNList.item(0).getNodeValue();
							}
							catch(NullPointerException e){
								tempWinner = "No winner registered";
							}
							// ----------Gets track start time from xml-----------
							NodeList startTimeList = fstElement.getElementsByTagName("start_ts");
							Element startTimeElement = (Element) startTimeList.item(0);
							NodeList longStartList = startTimeElement.getChildNodes();
							
							tempStart = Long.parseLong(longStartList.item(0).getNodeValue());
							// ----------Gets track stop time from xml-----------
							NodeList stopTimeList = fstElement.getElementsByTagName("stop_ts");
							Element stopTimeElement = (Element) stopTimeList.item(0);
							NodeList longStopList = stopTimeElement.getChildNodes();
							
							tempStop = Long.parseLong(longStopList.item(0).getNodeValue());
							// ----------Gets track id from xml--------------
							NodeList idList = fstElement.getElementsByTagName("id");
							Element idElement = (Element) idList.item(0);
							NodeList intIdList = idElement.getChildNodes();
							
							tempId = Integer.parseInt(intIdList.item(0).getNodeValue());
							// ------------------------------------------------------
							
							//Checks if the user is looking for a race to sign up for or a finished race.
							if(activeRace && tempStop > (System.currentTimeMillis() / 1000L)){
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
		selectedItem = position;
		
		try{
            LayoutInflater inflater = (LayoutInflater) RebusListViewer.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.viewerpopup1, (ViewGroup) findViewById(R.id.popup1_element));
            pw = new PopupWindow(layout, 300, 470, true);
            pw.showAtLocation(layout, Gravity.CENTER, 0, 0);

            popup_name = (TextView) layout.findViewById(R.id.popup1_title);
            popup_time = (TextView) layout.findViewById(R.id.popup1_time);
            
            popup_name.setText(theTrackList.get(position).Name() + " by " +theTrackList.get(position).Creator());

            if(activeRace){
            	Date start = new Date((long) theTrackList.get(position).Start_ts()*1000);
            	Date stop = new Date((long) theTrackList.get(position).Stop_ts()*1000);
            	popup_time.setText("Race starts at: " + start + " and ends at: " + stop);
            }
            else if(!activeRace){
            	popup_time.setText("This race's winner was: " + theTrackList.get(position).Winner());
            }
            
            popup_confirm = (Button) layout.findViewById(R.id.popup1_button_signup);
            popup_cancel = (Button) layout.findViewById(R.id.popup1_button_cancel);

            popup_confirm.setOnClickListener(new MyButtonHandler());
            popup_cancel.setOnClickListener(new MyButtonHandler());
		}//end try
		catch(Exception e){
			;
		}//end catch
	}//End onListItemClick()
	
	private class ServerSendTask extends AsyncTask<String, String, Boolean> {
		private ProgressDialog pd;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = ProgressDialog.show(RebusListViewer.this, "Please wait.", "Registering signup.", true, false);
		}//End onPreExecute()

		@Override
		protected void onProgressUpdate(String... values) {
			pd.setMessage(values[0]);
		}//End onProgressUpdate()
		
		protected void onPostExecute(boolean result){
			pd.dismiss();
		}

		/**
		 * doInBackground()
		 * 
		 * Gets generated xml-file from server. File contains information about
		 * available routes.
		 **/
		@Override
		protected Boolean doInBackground(String... params) {
			try{
				HttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setSoTimeout(httpParams, 30000);
			
				HttpClient theClient = new DefaultHttpClient(httpParams);
			
				HttpGet method = new HttpGet(new URI(params[0]));
				
				theClient.execute(method);
			}
			catch(Exception e){
			}
			return true;
		}//end doInBackground()
	}//end class ServerContactTask
	
	private class MyButtonHandler implements View.OnClickListener{
		public void onClick(View arg0) {
			switch(arg0.getId()){
			case R.id.popup1_button_cancel:{
				pw.dismiss();
				break;
			}//end case R.id.popup1_button_cancel
			case R.id.popup1_button_signup:{
				Intent result = new Intent();
				result.putExtra("returnResult", theTrackList.get(selectedItem).getStringArray());
				setResult(RESULT_OK, result);
				
				String tempUrl = "http://rdb.goldclone.no/?format=xml&" +
						"action=join&track="+theTrackList.get(selectedItem).Id()+"&username="+uname+"&password="+upwd;
				
				serverSend = new ServerSendTask();
				serverSend.execute(tempUrl);
				
				finish();				
				break;
			}//end case R.id.popup1_button_signup
			}//end switch
		}//end onClick
	}//end class myButtonHandler
}
