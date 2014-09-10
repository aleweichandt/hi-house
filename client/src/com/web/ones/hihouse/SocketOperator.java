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
	
	public void sendHttpRequest(){
		
		String stringUrl = "http://192.168.1.110/AppServer/";
		if (checkNetworkConn()) {
	    	new DownloadWebpageTask().execute(stringUrl);
	    } else {
	        // display error
	    	Toast.makeText(context, "Error: No Network Connection", Toast.LENGTH_SHORT).show();
	    }
	}
	
	private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
             
            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            //Toast.makeText(context, result, Toast.LENGTH_LONG).show();
        	Intent hiHouseMessage = new Intent(HiHouseTask.NEW_RESPONSE);
            hiHouseMessage.putExtra("data", result);
            context.sendBroadcast(hiHouseMessage);
       }
    }
	
	private String downloadUrl(String myurl) throws IOException {
	    InputStream is = null;
	    // Only display the first 500 characters of the retrieved
	    // web page content.
	    int len = 500;
	        
	    try {
	        URL url = new URL(myurl);
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setReadTimeout(10000 /* milliseconds */);
	        conn.setConnectTimeout(15000 /* milliseconds */);
	        conn.setRequestMethod("POST");
	        conn.setDoInput(true);
	    	conn.setDoOutput(true);
	        // Starts the query
	        
	        // Enviar parametros
	    	String params = "" + URLEncoder.encode("username","UTF-8")+"="+ URLEncoder.encode("Charly","UTF-8");
	    	//String params = "?username=Charly";
	    	String result = new String();
	    	PrintWriter out = new PrintWriter(conn.getOutputStream());
	    	out.print(params);
	    	out.close();
	    	/*BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	    	String inputLine;
	    	while ((inputLine = in.readLine()) != null) {
	    	result = result.concat(inputLine);	
	    	}
	    	in.close();	
	    	return result;*/
	        // Fin Eviar parametrso
	    	
	        //conn.connect();
	        //int response = conn.getResponseCode();
	        //Log.d(DEBUG_TAG, "The response is: " + response);
	        is = conn.getInputStream();

	        // Convert the InputStream into a string
	        String contentAsString = readIt(is, len);
	        return contentAsString;
	        
	    // Makes sure that the InputStream is closed after the app is
	    // finished using it.
	    } finally {
	        if (is != null) {
	            is.close();
	        } 
	    }
	}
	
	// Reads an InputStream and converts it to a String.
	public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
	    Reader reader = null;
	    reader = new InputStreamReader(stream, "UTF-8");        
	    char[] buffer = new char[len];
	    reader.read(buffer);
	    return new String(buffer);
	}
}
