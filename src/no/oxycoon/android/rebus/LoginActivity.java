package no.oxycoon.android.rebus;

import java.io.InputStream;
import java.net.URI;

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

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
	private Button buttonSubmit;
	private EditText editUname, editPwd;
	private ServerContactTask serverContact;
	
	private String uName, pwd, theUrl;
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		buttonSubmit = (Button)findViewById(R.id.login_submit);
		editUname = (EditText)findViewById(R.id.login_uname);
		editPwd = (EditText)findViewById(R.id.login_pwd);
		
		buttonSubmit.setOnClickListener(new View.OnClickListener(){
			
			public void onClick(View v) {
				uName = editUname.getText().toString();
				pwd = editPwd.getText().toString();
				
				theUrl = "http://rdb.goldclone.no/?format=xml&username=" + uName + "&password=" + pwd;
				
				serverContact = new ServerContactTask();
				serverContact.execute(new String[]{theUrl});
			}
		});
	}
	
	private class ServerContactTask extends	AsyncTask<String, String, Boolean> {
		private ProgressDialog pd;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = ProgressDialog.show(LoginActivity.this, "Please wait.", "Signing in.", true, false);
		}//End onPreExecute()

		@Override
		protected void onProgressUpdate(String... values) {
			pd.setMessage(values[0]);
		}//End onProgressUpdate()
		
		protected void onPostExecute(Boolean result){
			pd.dismiss();
			if(result){
				Intent intent = new Intent(LoginActivity.this, RebusActivity.class);
				intent.putExtra("username", uName);
				intent.putExtra("password", pwd);
				startActivity(intent);
			}else{
				Toast.makeText(LoginActivity.this, "Login failed", 5);
			}
		}

		/**
		 * doInBackground()
		 * 
		 **/
		@Override
		protected Boolean doInBackground(String... params) {
			boolean logged = false;
			try{
				HttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setSoTimeout(httpParams, 30000);
				HttpClient theClient = new DefaultHttpClient(httpParams);
				HttpGet method = new HttpGet(new URI(params[0]));
				
				HttpResponse response = theClient.execute(method);
				InputStream in = response.getEntity().getContent();
				Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);

				NodeList nodeLst = doc.getElementsByTagName("user");

				// XML-parse loop
				for (int i = 0; i < nodeLst.getLength(); i++) {
					Node fstNode = nodeLst.item(i);

					if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
						Element fstElement = (Element) fstNode;
						// ------Gets the signedin from the xml-------
						NodeList logList = fstElement.getElementsByTagName("signedin");
						Element logElement = (Element) logList.item(0);
						NodeList textlogList = logElement.getChildNodes();
						
						if(textlogList.item(0).getNodeValue().toString().equals("true")){
							logged = true;
							return logged;
						}
					}
				}
			}catch(Exception e){
				Log.v("login", e.getMessage());
			}
			return logged;
			
		}//end doInBackground()
	}//end class ServerContactTask
}
