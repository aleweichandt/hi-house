package com.web.ones.hihouse;

import org.json.JSONException;
import org.json.JSONObject;

import com.web.ones.hihouse.HiHouseService.HiHouseTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {
	public final static String EXTRA_USER = "hihouse.extra.user";
	public final static String EXTRA_PASS = "hihouse.extra.pass";
	public final static String EXTRA_SAVE = "hihouse.extra.save";
	public final static String EXTRA_TOKEN = "hihouse.extra.token";
	public final static String EXTRA_ADMIN = "hihouse.extra.admin";

	private CheckBox recordar;
	private EditText user, pass;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		//Font
		TextView txt = (TextView) findViewById(R.id.login);
		Typeface font = Typeface.createFromAsset(getAssets(), "YAHOO.TTF");
		txt.setTypeface(font);
		
		recordar = (CheckBox) findViewById(R.id.checkbox_remember);
		user = (EditText) findViewById(R.id.user);
		pass = (EditText) findViewById(R.id.pass);
		Button login_btn = (Button) findViewById(R.id.login);
		
		//TODO Autocompleto user y pass. Luego borrar
		user.setText("administrador");
		pass.setText("1234");
		
		login_btn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				//TODO Validar datos y loguear usuario.
				String params = "name="+user.getText()+"&pwd="+pass.getText();
				SocketOperator so = new SocketOperator(v.getContext());
				so.sendRequest(Request.LOGIN_USER, true, Command.serverURL+"users/login", params);
			}
		});
		
	}
	
	@Override
	protected void onResume(){
		// Register broadcast receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter(HiHouseTask.NEW_RESPONSE));
		super.onResume();
	}
	
	@Override
	protected void onPause(){
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
		super.onPause();
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
		if (id == R.id.action_setup_ip) {
			showIPSetupDialog();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
        	switch(intent.getIntExtra("type",-1)){
        	case Request.LOGIN_USER:
        		int rc = intent.getIntExtra("responseCode",0);
        		if(rc==200){
        			String in = intent.getCharSequenceExtra("data").toString();
        			JSONObject reader;
        			
        			try{
        				reader = new JSONObject(in);
        				String token = reader.getString("token");
        				boolean admin = reader.getBoolean("admin");
        				
        				// Creamos el intento para la HiHouse activity y mandamos los parametros
            	    	Intent activityIntent = new Intent(context, HiHouse.class);
            	    	activityIntent.putExtra(EXTRA_USER, user.getText().toString());
            	    	activityIntent.putExtra(EXTRA_PASS, pass.getText().toString());
            	    	activityIntent.putExtra(EXTRA_SAVE, recordar.isChecked());
            	    	activityIntent.putExtra(EXTRA_TOKEN, token);
            	    	activityIntent.putExtra(EXTRA_ADMIN, admin);
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
        		break;
        	case Request.ERROR:
        		Toast.makeText(context, "Error de Login", Toast.LENGTH_LONG).show();
        		break;
        	}
        }
	};
	
	private void showIPSetupDialog()
	{
	    final AlertDialog.Builder Main_Dialog = new AlertDialog.Builder(this);
	    final EditText input = new EditText(this);
	    input.setPadding(10, 10, 10, 10);
	    input.setText("http://192.168.1.102:8080");

	    Main_Dialog.setView(input);
	    Main_Dialog.setTitle(getResources().getString(R.string.action_setup_ip));
	    Main_Dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener()
	    {
	        public void onClick(DialogInterface dialog, int whichButton)
	        {
	        	Command.setCommandServerBase(input.getText().toString());
	        }
	    });
	    Main_Dialog.show();
	}
}
