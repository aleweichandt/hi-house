package com.web.ones.hihouse;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileInfoFragment extends Fragment implements OnClickListener, OnItemClickListener{
	final static String ARG_PROFILE_NAME = "name";
	final static String ARG_PROFILE_ID = "id";
	final static String ARG_PROFILE_DESC = "desc";
	final static String ARG_IS_ADD = "isAddOperation";
	private boolean mIsAddOperation = false;
	private boolean mState = false;
	private String mName, mDesc;
	private int id;
	private View mMainView;
	private HiHouse hiHouseAct;
	ListView lv;
	private ArrayList<Device> devices;
	private EditText prof_name, prof_desc;

	public ProfileInfoFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		hiHouseAct = (HiHouse)getActivity();
		
		Bundle args = getArguments();
		if (args != null){
			mName = args.getString(ARG_PROFILE_NAME, "Nuevo");
			id = args.getInt(ARG_PROFILE_ID, -1);
			mDesc = args.getString(ARG_PROFILE_DESC, "");
			mIsAddOperation = args.getBoolean(ARG_IS_ADD);
			mState = mIsAddOperation;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mMainView = inflater.inflate(R.layout.fragment_profile_info, container, false);
		
		lv = (ListView) mMainView.findViewById(R.id.profileinfo_devices);
		prof_name = (EditText)mMainView.findViewById(R.id.profileinfo_name);
		prof_name.setText(mName);
		prof_desc = (EditText)mMainView.findViewById(R.id.profileinfo_desc);
		prof_desc.setText(mDesc);
		
		setEditMode(mIsAddOperation || mState);
		
		hiHouseAct.mHiHouseService.sendCommand(new Command(Request.GET_ALL_DEVICES, true, "devices/all", "token="+hiHouseAct.getUser().getToken()));
		hiHouseAct.setLoadingBarVisibility(View.VISIBLE);
		
		return mMainView;
	}
	
	public void loadDevices(String str) {
		devices = new ArrayList<Device>();
		JSONArray devArray;
		JSONObject deviceInfo;
    	try{
    		devArray = new JSONArray(str);
    		for(int i=0; i<devArray.length(); i++){
    			deviceInfo = devArray.getJSONObject(i);
    			Device d = new Device(deviceInfo.getInt("id"), deviceInfo.getString("name"));
    			d.setState(false); //estado del checkbox
    			devices.add(d);
    		}
    	}
    	catch(JSONException e){
    		e.printStackTrace();
    	}
    	
    	if(!mIsAddOperation){
    		hiHouseAct.mHiHouseService.sendCommand(new Command(Request.GET_PROFILE_DEVICES, true, "profiles/"+id+"/devices", "token="+hiHouseAct.getUser().getToken()));
    	}
    	else{
    		ProfileInfoAdapter adapter = new ProfileInfoAdapter(getActivity());
    		lv.setAdapter(adapter);
    		hiHouseAct.setLoadingBarVisibility(View.GONE);
    	}
	}
	
	public void checkDevices(String str){
		JSONArray devArray;
		JSONObject deviceInfo;
		try{
    		devArray = new JSONArray(str);
    		for(int i=0; i<devArray.length(); i++){
    			deviceInfo = devArray.getJSONObject(i);
    			for(Device d : devices){
    				if(d.getId()==deviceInfo.getInt("id")) d.setState(true);
    			}
    		}
    	}
    	catch(JSONException e){
    		e.printStackTrace();
    	}
		ProfileInfoAdapter adapter = new ProfileInfoAdapter(getActivity());
		lv.setAdapter(adapter);
	}

	@Override
	public void onPause() {
		super.onPause();
		setListeners(null);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		setListeners(this);
	}
	
	private void setListeners(OnClickListener listener) {
		mMainView.findViewById(R.id.profileinfo_confirm).setOnClickListener(listener);
		mMainView.findViewById(R.id.profileinfo_cancel).setOnClickListener(listener);
		mMainView.findViewById(R.id.profileinfo_edit).setOnClickListener(listener);
		mMainView.findViewById(R.id.profileinfo_delete).setOnClickListener(listener);
		lv.setOnItemClickListener((OnItemClickListener) listener);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case (R.id.profileinfo_confirm):
			onConfirmPressed();
			break;
		case (R.id.profileinfo_cancel):
			onCancelPressed();
			break;
		case (R.id.profileinfo_edit):
			onEditPressed();
			break;
		case (R.id.profileinfo_delete):
			onDeletePressed();
			break;
		default:
			break;
		}
		
	}

	private void onEditPressed() {
		setEditMode(true);
	}
	
	private void onConfirmPressed() {
		String profName = prof_name.getText().toString();
		String profDesc = prof_desc.getText().toString();
		JSONObject builder = new JSONObject();
		JSONArray devArray = new JSONArray();
		
		Boolean validate = validateData(profName, profDesc);
		if(validate)
		{
			return;
		}
		try{
			builder.put("name", profName);
			builder.put("description", profDesc);
			for(Device d : devices){
				if(d.getState())
					devArray.put(d.getId());
			}
			builder.put("devices", devArray);
		}
		catch (JSONException e){}
		
		if(mIsAddOperation)
			hiHouseAct.mHiHouseService.sendCommand(new Command(Request.ADD_PROFILE, false, "profiles/add?token="+hiHouseAct.getUser().getToken(), builder.toString()));
		else
			hiHouseAct.mHiHouseService.sendCommand(new Command(Request.UPDATE_PROFILE, false, "profiles/"+id+"/update?token="+hiHouseAct.getUser().getToken(), builder.toString()));
		hiHouseAct.setLoadingBarVisibility(View.VISIBLE);
	}
	
	private Boolean validateData(String profName, String profDesc) {
		ArrayList<String> errors = new ArrayList<String>();
		Boolean state = false;
		
		if(profName.isEmpty()){
			errors.add("Ingrese un nombre");
			state = true;
		}
		if(profName.length()>20){
			errors.add("El nombre supera los 20 caracteres");
			state = true;
		}
		
		if(profDesc.isEmpty()){
			errors.add("Ingrese una descripción de voz");
			state = true;
		}
		if(profDesc.length()>50){
			errors.add("La descripción supera los 50 caracteres");
			state = true;
		}
		
		String cadena = "";
		if(state)
		{
			for (String a : errors)
			{
				cadena += a + '\n';
			}
			
			Toast.makeText(getActivity(), cadena.substring(0, cadena.length() - 1), Toast.LENGTH_SHORT).show();
		}
		return state;
	}
	
	private void onCancelPressed() {
		if(mIsAddOperation) {
			getActivity().getFragmentManager().popBackStack();
			return;
		}
		setEditMode(false);
	}
	
	private void onDeletePressed() {
		confirmDelete();
	}
	
	private void setEditMode(boolean on) {
		mMainView.findViewById(R.id.profileinfo_name).setEnabled(on);
		mMainView.findViewById(R.id.profileinfo_desc).setEnabled(on);
		mMainView.findViewById(R.id.profileinfo_edit).setVisibility(on?View.GONE:View.VISIBLE);
		mMainView.findViewById(R.id.profileinfo_delete).setVisibility(on?View.GONE:View.VISIBLE);
		mMainView.findViewById(R.id.profileinfo_confirm).setVisibility(on?View.VISIBLE:View.GONE);
		mMainView.findViewById(R.id.profileinfo_cancel).setVisibility(on?View.VISIBLE:View.GONE);
		mState = on;
	}

	@Override
	public void onItemClick(AdapterView<?> ad, View v, int pos, long id) {
		if(mState){
			CheckBox r = (CheckBox)v.findViewById(R.id.dev_row_check);
			boolean b = !r.isChecked();
			r.setChecked(b);
			devices.get(pos).setState(b);
		}
	}
	
	//adapter for list
	private class ProfileInfoAdapter extends ArrayAdapter<Device> {
		private Context mContext;
		
		public ProfileInfoAdapter(Context context) {
			super(context, R.layout.profileinfo_device_row, devices);
			mContext = context;
		}
			
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.profileinfo_device_row, parent, false);
			TextView tv = (TextView)rowView.findViewById(R.id.dev_row_name);
			tv.setText(devices.get(position).getName());
			CheckBox cb = (CheckBox)rowView.findViewById(R.id.dev_row_check);
			cb.setChecked(devices.get(position).getState());
			return rowView;
		}
	}

	public void addProfileResult(boolean added) {
		if(!added) {
			Toast.makeText(getActivity(), "Un perfil con ese nombre ya existe.", Toast.LENGTH_LONG).show();
			return;
		}
		if(mIsAddOperation) {
			Toast.makeText(getActivity(), "Perfil agregado exitosamente.", Toast.LENGTH_LONG).show();
			getActivity().getFragmentManager().popBackStack();
			return;
		}
		setEditMode(false);
	}
	
	public void updateProfileResult(boolean updated) {
		if(!updated) {
			Toast.makeText(getActivity(), "Un perfil con ese nombre ya existe.", Toast.LENGTH_LONG).show();
			return;
		}
		Toast.makeText(getActivity(), "Perfil actualizado exitosamente.", Toast.LENGTH_LONG).show();
		getActivity().getFragmentManager().popBackStack();
		return;
	}
	
	public void confirmDelete() {
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				 switch (which) {
				 case DialogInterface.BUTTON_POSITIVE:
					 	hiHouseAct.mHiHouseService.sendCommand(new Command(Request.DELETE_PROFILE, false, "profiles/"+id+"/delete?token="+hiHouseAct.getUser().getToken(), ""));
					 	hiHouseAct.setLoadingBarVisibility(View.VISIBLE);
				        break;
				
				 case DialogInterface.BUTTON_NEGATIVE:
				        // No button clicked do nothing
					 	//Toast.makeText(getActivity(), "No Clicked",Toast.LENGTH_LONG).show();
				        break;
				 }
			}
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("¿Está seguro que desea eliminar el perfil \""+prof_name.getText().toString()+"\"?")
                     .setPositiveButton("Si", dialogClickListener)
                     .setNegativeButton("No", dialogClickListener).show();
	}
	
	public void deleteProfileResult(boolean deleted) {
		if(deleted){
			Toast.makeText(getActivity(), "Perfil eliminado exitosamente.", Toast.LENGTH_LONG).show();
			getActivity().getFragmentManager().popBackStack();
		}
		else {
			Toast.makeText(getActivity(), "El Perfil no pudo ser eliminado.", Toast.LENGTH_LONG).show();
			hiHouseAct.setLoadingBarVisibility(View.GONE);
		}
		
	}

}
