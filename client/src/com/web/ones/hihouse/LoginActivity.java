package com.web.ones.hihouse;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import com.web.ones.hihouse.HiHouseService.HiHouseTask;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import android.os.Build;

public class LoginActivity extends Activity {
	public final static String EXTRA_USER = "hihouse.extra.user";
	public final static String EXTRA_PASS = "hihouse.extra.pass";
	public final static String EXTRA_SAVE = "hihouse.extra.save";
	public final static String EXTRA_TOKEN = "hihouse.extra.token";

	private CheckBox recordar;
	private EditText user, pass;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		recordar = (CheckBox) findViewById(R.id.checkbox_remember);
		user = (EditText) findViewById(R.id.user);
		pass = (EditText) findViewById(R.id.pass);
		Button login_btn = (Button) findViewById(R.id.login);
		
		//TODO Autocompleto user y pass. Luego borrar
		user.setText("admin");
		pass.setText("1234");
		
		login_btn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				//TODO Validar datos y loguear usuario.
				String params = "id="+user.getText()+"&pwd="+pass.getText();
				SocketOperator so = new SocketOperator(v.getContext());
				so.sendRequest(Request.LOGIN_USER, true, Command.serverURL+"users/login", params);
			}
		});
		
		// Message handling
        final IntentFilter myFilter = new IntentFilter(HiHouseTask.NEW_RESPONSE);
        registerReceiver(mReceiver, myFilter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
        	if(intent.getIntExtra("type",0)==Request.LOGIN_USER){
        		int responseCode = intent.getIntExtra("responseCode",0);
        		if(responseCode==200){
        			String in = intent.getCharSequenceExtra("data").toString();
        			JSONObject reader;
        			
        			try{
        				reader = new JSONObject(in);
        				String token = reader.getString("token");
        				//String admin = reader.getString("admin");
        				
        				// Creamos el intento para la HiHouse activity y mandamos los parametros
            	    	Intent activityIntent = new Intent(context, HiHouse.class);
            	    	activityIntent.putExtra(EXTRA_USER, user.getText());
            	    	activityIntent.putExtra(EXTRA_PASS, pass.getText());
            	    	activityIntent.putExtra(EXTRA_SAVE, recordar.isChecked());
            	    	activityIntent.putExtra(EXTRA_TOKEN, token);
            	    	startActivity(activityIntent);
            	    	finish();
        			}
        			catch(JSONException e){
        				Toast.makeText(context, "Error de Login", Toast.LENGTH_LONG).show();
        			}
        		}
        		else{
        			Toast.makeText(context, "Error de Login", Toast.LENGTH_LONG).show();
        		}
        	}
        }
	};
}
