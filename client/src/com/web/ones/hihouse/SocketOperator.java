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
	
	private class SendRequestTask extends AsyncTask<Void, Void, String> {
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
        protected String doInBackground(Void... args) {
             
            // params comes from the execute() call: params[0] is the url.
            try {
                //return downloadUrl(params[0]);
            	return downloadUrl(method, url, params);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            //Toast.makeText(context, result, Toast.LENGTH_LONG).show();
        	Intent hiHouseMessage = new Intent(HiHouseTask.NEW_RESPONSE);
        	hiHouseMessage.putExtra("type", type);
            hiHouseMessage.putExtra("data", result);
            context.sendBroadcast(hiHouseMessage);
       }
    }
	
	private String downloadUrl(boolean method, String serverUrl, String params) throws IOException {
		URL url;
		String result = new String();
		
		if(method){ //GET
			serverUrl += "?" + params;
		}
		
		try{
			url = new URL(serverUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setReadTimeout(10000 /* milliseconds */);
			connection.setConnectTimeout(15000 /* milliseconds */);
			
			if(!method){ //POST - Send params
				connection.setDoOutput(true);
				if(!"".equals(params)){
					PrintWriter out = new PrintWriter(connection.getOutputStream());
					out.print(params);
					out.close();
				}
			}
			
			int i = connection.getResponseCode();
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
		
		return result;
	}
}
