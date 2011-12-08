package org.sward.poduptime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class PodPicker extends Activity  {
	
	public static final String SETTINGS_FILENAME="settings";
	public String lvPods_arr[] ={};
	public ListView lvPods;
	JSONArray jsonArray;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.podpick);
        lvPods_arr=getPods();
        fillListview();
	}
	public void fillListview()
	{
        lvPods = (ListView) findViewById(R.id.lvPods);
        lvPods.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,lvPods_arr));
        lvPods.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> a, View v, int position, long id) {
				//onclick select pod
				SharedPreferences preferences = getSharedPreferences(SETTINGS_FILENAME, MODE_PRIVATE);		
				// Save the new currentpod
		        SharedPreferences.Editor editor = preferences.edit();
		        String pod=lvPods.getItemAtPosition(position).toString();
		        pod=pod.substring(8);
		        editor.putString("currentpod",pod);
		        editor.commit();
		        Toast.makeText(v.getContext(), "Pod Selected: "+pod,Toast.LENGTH_SHORT).show();
		        startActivity(new Intent(v.getContext(), Poduptime_AndroidActivity.class));
		        finish();
			    
			}
        });
	}
	
	
	public String [] getPods() {
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		List<String> list = null;
		SharedPreferences preferences = getSharedPreferences(SETTINGS_FILENAME, MODE_PRIVATE);
	    String prev=preferences.getString("all", "nothing");
	    if(prev.equals("nothing"))
		{
	    	try {
		
			HttpGet httpGet = new HttpGet("http://podupti.me/api.php?key=4r45tg&format=json");
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
			} else {
				//TODO  Notify User about failure
				Log.e("Diaspora-WebClient", "Failed to download file");
			}
		} catch (ClientProtocolException e) {
			//TODO handle network unreachable exception here
			e.printStackTrace();
		} catch (IOException e) {
			//TODO handle json buggy feed
			e.printStackTrace();
		}
		
		//Parse the JSON Data
		try {
			// Save the fetched String
	        SharedPreferences.Editor editor = preferences.edit();
	        editor.putString("all", builder.toString());
	        editor.commit();
			JSONObject j=new JSONObject(builder.toString());			
			JSONArray jr=j.getJSONArray("pods");
			Log.i("PODU","Number of entries " + jr.length());
			list=new ArrayList<String>();
			for (int i = 0; i < jr.length(); i++) {
				JSONObject jo = jr.getJSONObject(i);
				Log.i("PODU", jo.getString("domain"));
				String secure=jo.getString("secure");
				if(secure.equals("true"))
				list.add("https://"+jo.getString("domain"));				
				}
			return list.toArray(new String[list.size()]);
		}catch (Exception e) {
			//TODO Handle Parsing errors here	
			e.printStackTrace();
		}
		}//Data not present
	    else{
	    	//Parse from Local data
	    	try {
				JSONObject j=new JSONObject(prev);			
				JSONArray jr=j.getJSONArray("pods");
				Log.i("PODU","Number of entries " + jr.length());
				list=new ArrayList<String>();
				for (int i = 0; i < jr.length(); i++) {
					JSONObject jo = jr.getJSONObject(i);
					Log.i("PODU", jo.getString("domain"));
					String secure=jo.getString("secure");
					if(secure.equals("true"))
					list.add("https://"+jo.getString("domain"));				
					}
				return list.toArray(new String[list.size()]);
			}catch (Exception e) {
				//TODO Handle Parsing errors here	
				e.printStackTrace();
			}
	    	
	    }
		return lvPods_arr;
	}//GetPods
	
	
	
	
}

