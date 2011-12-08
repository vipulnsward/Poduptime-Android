package org.sward.poduptime;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class Poduptime_AndroidActivity extends Activity {
	private static final int REQUEST_CODE = 101;
	public static final String SETTINGS_FILENAME="settings";
    public static String pod="https://diasp";
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        SharedPreferences preferences = getSharedPreferences(SETTINGS_FILENAME, MODE_PRIVATE);
	    String selPod=preferences.getString("currentpod", "nothing");
	    if(selPod.equals("nothing")==false){
	        	//Fill with Selected Info
	        	try{
	        	String all=preferences.getString("all", "{}");
	        	Log.i("ALL",all);
	        	Log.i("SEL",selPod);
	        	JSONObject j=new JSONObject(all);			
				JSONArray jr=j.getJSONArray("pods");
				Log.i("Poduptime","Endtries " + jr.length());
				for (int i = 0; i < jr.length(); i++) {
					JSONObject jo = jr.getJSONObject(i);
					Log.i("DOM", jo.getString("domain"));
					if(jo.getString("domain").equals(selPod))
					{
						//This ones the selected Pod, display
						/*
						 * To Display -> Last Check =dateupdated 
						 * Status = status, Last Git Pull= hgitdate, Uptime= uptimelast7,
						 *  Months Monitored= monthsmonitored, Response Time=responsetimelast7
						 * 
						 * */
						String podInfo="Pod: <a href=https://"+selPod+">"+selPod+"</a>";
						podInfo+="<br>Last Check: "+jo.getString("dateupdated");
						podInfo+="<br/>Status: "+jo.getString("status");
						podInfo+="<br/>Last Git Pull: "+jo.getString("hgitdate");
						podInfo+="<br/>Uptime this month: "+jo.getString("uptimelast7");
						podInfo+="<br/>Months Monitored: "+jo.getString("monthsmonitored");
						podInfo+="<br/>Response Time: "+jo.getString("responsetimelast7");
						TextView podStat = (TextView) findViewById(R.id.textView3);
						podStat.setMovementMethod(LinkMovementMethod.getInstance());
						podStat.setText(Html.fromHtml(podInfo));
					}
					
						
					}
	        	}catch(Exception e){
	        		//TODO Handle Buggy Json
	        		Log.d("ERR", "Here"+e);
	        		e.printStackTrace();
	        	}
				
	        }
			
		
        //Set the Links to Diaspora and Flattr here
        TextView diasporaLinks = (TextView) findViewById(R.id.textView1);
        diasporaLinks.setMovementMethod(LinkMovementMethod.getInstance());
        String diaspora = "<a href=\"http://github.com/diaspora/diaspora\" >More about Diaspora*</a>";
        String flattr= "<br><a href=\"https://flattr.com/thing/170048/Diaspora-Live-Uptime-watch\"><font color=\"DAA520\" face=\"arial\" size=\"14\">Flattr</font></a>";
        diaspora+=flattr;
        diasporaLinks.setText(Html.fromHtml(diaspora));	
        
        
    }
	
	@Override
	public void onPause(){
		super.onPause();
	     SharedPreferences preferences = getSharedPreferences(SETTINGS_FILENAME, MODE_PRIVATE);
	     SharedPreferences.Editor edit=preferences.edit();
	     //Clear pod value
	     edit.putString("currentpod", "nothing");
	     edit.commit();
		
	}
	
	
	public void podpick(View v){
		Intent i=new Intent(this,PodPicker.class);
		finish();
		startActivity(i);
	}
    
	
    
}