package com.web.ones.hihouse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import com.web.ones.hihouse.HiHouseService.HiHouseTask;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.Toast;

public class SocketOperator {
	private Context context;
	
	public SocketOperator(Context con) {
		context = con;
	}


	public boolean checkNetworkConn() {
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
	    if (networkInfo != null && networkInfo.isConnected()) return true;
	    else return false;
	}
	
	/** 
	 * @param method Metodo HTTP: true=GET - false=POST
	 * @param url Server URL
	 * @param params Parametros para el request
	 */
	public void sendRequest(int type, boolean method, String url, String params){
		if (checkNetworkConn()) {
	    	new SendRequestTask(type, method, url, params).execute();
	    } else {
	        // display error
	    	Toast.makeText(context, "Error: No Network Connection", Toast.LENGTH_SHORT).show();
	    }
	}
	
	private class SendRequestTask extends AsyncTask<Void, Void, Intent> {
		boolean method;
		String url, params;
		int type;
		
		public SendRequestTask(int type, boolean method, String url, String params){
			this.type = type;
			this.method = method;
			this.url = url;
			this.params = params;
		}
		
        @Override
        protected Intent doInBackground(Void... args) {
            try {
            	return downloadUrl(method, url, params);
            } catch (IOException e) {
            	Intent hiHouseMessage = new Intent(HiHouseTask.NEW_RESPONSE);
            	hiHouseMessage.putExtra("type", Request.ERROR);
            	//hiHouseMessage.putExtra("data","Unable to retrieve web page. URL may be invalid.");
            	return hiHouseMessage;
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(Intent hiHouseMessage) {
        	if(hiHouseMessage.getIntExtra("type", -1)==-1){
        		hiHouseMessage.putExtra("type", type);
        	}
        	LocalBroadcastManager.getInstance(context).sendBroadcast(hiHouseMessage);
       }
    }
	
	private Intent downloadUrl(boolean method, String serverUrl, String params) throws IOException {
		URL url;
		String result = new String();
		int responseCode = 0;
		Intent hiHouseMessage = new Intent(HiHouseTask.NEW_RESPONSE);
		
		if(method && !params.equals("")){ //GET
			serverUrl += "?" + params;
		}
		
		try{
			url = new URL(serverUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setReadTimeout(5000 /* milliseconds */);
			connection.setConnectTimeout(5000 /* milliseconds */);
			
			if(!method){ //POST - Send params
				connection.setDoOutput(true);
				if(!params.equals("")){
					PrintWriter out = new PrintWriter(connection.getOutputStream());
					out.print(params);
					out.close();
				}
			}

			try{
				responseCode = connection.getResponseCode();
			}
			catch(IOException e){
				responseCode = connection.getResponseCode();
				hiHouseMessage.putExtra("responseCode", responseCode);
				return hiHouseMessage;
			}
			if(responseCode!=200){
				hiHouseMessage.putExtra("responseCode", responseCode);
				return hiHouseMessage;
			}
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			
			while ((inputLine = in.readLine()) != null) {
				result = result.concat(inputLine);	
			}
			in.close();
			connection.disconnect();
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		if (result.length() == 0) {
			result = null;
		}
		
		hiHouseMessage.putExtra("data", result);
		hiHouseMessage.putExtra("responseCode", responseCode);
		return hiHouseMessage;
	}
}
