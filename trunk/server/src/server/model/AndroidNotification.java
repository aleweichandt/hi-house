package server.model;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.json.Json;
import javax.json.JsonObject;

public class AndroidNotification {
	private static String ANDROID_GCM_URL = "https://android.googleapis.com/gcm/send";
	private static String ANDROID_GCM_KEY = "AIzaSyB6h0hXJ-GBKDvW_n9mg2Qfy6APUu8SPRo";
	private static String ADNROID_GCM_PACKAGE_NAME = "com.web.ones.hihouse";
	
	private HttpURLConnection mConnection;
	public AndroidNotification() {
		URL url;
		try {
			url = new URL(ANDROID_GCM_URL);
			mConnection = (HttpURLConnection) url.openConnection();
			mConnection.setRequestMethod("POST");
			mConnection.setRequestProperty("Content-Type", "application/json");
			mConnection.setRequestProperty("Accept", "application/json");
			mConnection.setRequestProperty("Authorization", "key=" + ANDROID_GCM_KEY);
			mConnection.setDoInput(true);
			mConnection.setDoOutput(true);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(mConnection != null) {
				mConnection.disconnect();
			}
		}
	}
	
	public boolean send(String destid, JsonObject data) {
		JsonObject msg = Json.createObjectBuilder()
							 .add("registration_ids", Json.createArrayBuilder().add(destid).build())
							 //enable if using notification_keys instead of registration_ids (disable it also)
							 //.add("notification_key", destid)
							 .add("data", data)
							 .add("restricted_package_name", ADNROID_GCM_PACKAGE_NAME)
							 //enable for testing
							 //.add("dry_run", true)
							 .build();
		try {
			OutputStreamWriter wr= new OutputStreamWriter(mConnection.getOutputStream());
			wr.write(msg.toString());
			wr.flush();
			
			int HttpResult =mConnection.getResponseCode(); 
			if(HttpResult != HttpURLConnection.HTTP_OK){
				BufferedReader br = new BufferedReader(new InputStreamReader(mConnection.getInputStream(),"utf-8"));  
				String line;
				StringBuffer response = new StringBuffer(); 
				while((line = br.readLine()) != null) {
					response.append(line);
					response.append('\r');
				}
				br.close();
			}
			return (HttpResult == HttpURLConnection.HTTP_OK);
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if(mConnection != null) {
				mConnection.disconnect();
			}
		}
	}
}
