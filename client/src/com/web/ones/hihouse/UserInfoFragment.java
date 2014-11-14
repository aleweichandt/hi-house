package com.web.ones.hihouse;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.web.ones.hihouse.MultiChoiceDialog.OnMultiChoiceDialogListener;

import android.os.Bundle;
import android.os.Parcelable;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class UserInfoFragment extends Fragment implements
OnClickListener,
OnMultiChoiceDialogListener{
	private static final String PROFILE_TAG = "userinfo_profiles";
	final static String ARG_USER_NAME = "user";
	final static String ARG_USER_ID = "id";
	final static String ARG_IS_ADD = "isAddOperation";

	private boolean mIsAddOperation = false;
	private boolean mState = false;
	private HiHouse hiHouseAct;
	private int id;
	private String mName;
	private EditText user_name;
	private List<CharSequence> mSelectedProfiles;
	private View mMainView;
	private String[] profiles;
	private ArrayList<Profile> allProfileList;
	private ArrayList<Profile> selectedProfiles;
	private boolean mHasSubType;
    private final static Pattern EMAIL_ADDRESS_PATTERN = Pattern
            .compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@"
                    + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\."
                    + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+");
	
	String[] values = new String[] { "Cocina", "Living", "Baño",
	        						 "Habitacion 1", "Habitacion 2", "Garage" };

	public UserInfoFragment() {
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSelectedProfiles = new ArrayList<CharSequence>();
		hiHouseAct = (HiHouse)getActivity();
		
		Bundle args = getArguments();
		if (args != null){
			mName = args.getString(ARG_USER_NAME, "Nuevo");
			id = args.getInt(ARG_USER_ID, -1);
			mIsAddOperation = args.getBoolean(ARG_IS_ADD);
			mState = mIsAddOperation;
			mHasSubType = false;
		}
		
		selectedProfiles = new ArrayList<Profile>();
		profiles = new String[0];
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		mMainView = inflater.inflate(R.layout.fragment_user_info, container, false);
		
		user_name = (EditText)mMainView.findViewById(R.id.userinfo_name);
		user_name.setText(mName);
		
		loadUserInfo();
		setEditMode(mIsAddOperation || mState);
		return mMainView;
	}
	
	private void loadUserInfo() {
		((EditText)mMainView.findViewById(R.id.userinfo_name)).setText(mName);
		if(!mIsAddOperation){	
			hiHouseAct.mHiHouseService.sendCommand(new Command(Request.GET_USER, true, "users/"+id, "token="+hiHouseAct.getUser().getToken()));
			hiHouseAct.setLoadingBarVisibility(View.VISIBLE);
		}
		hiHouseAct.mHiHouseService.sendCommand(new Command(Request.GET_ALL_PROFILES, true, "profiles/all?token="+hiHouseAct.getUser().getToken(), ""));
	}
	
	public void updateUserInfo(String str){
		JSONObject userInfo;
		try{
			userInfo = new JSONObject(str);
			((EditText)mMainView.findViewById(R.id.userinfo_mail)).setText(userInfo.getString("email"));
			((CheckBox)mMainView.findViewById(R.id.userinfo_admin)).setChecked(userInfo.getBoolean("admin"));
		}
		catch(JSONException e){e.printStackTrace();}
		if(!mIsAddOperation)
		{
			hiHouseAct.mHiHouseService.sendCommand(new Command(Request.GET_USER_PROFILES, true, "users/"+id+"/profiles", "token="+hiHouseAct.getUser().getToken()));
		}	 	
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
		mMainView.findViewById(R.id.userinfo_confirm).setOnClickListener(listener);
		mMainView.findViewById(R.id.userinfo_cancel).setOnClickListener(listener);
		mMainView.findViewById(R.id.userinfo_edit).setOnClickListener(listener);
		mMainView.findViewById(R.id.userinfo_delete).setOnClickListener(listener);
		mMainView.findViewById(R.id.userinfo_profiles).setOnClickListener(listener);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case (R.id.userinfo_confirm):
			onConfirmPressed();
			break;
		case (R.id.userinfo_cancel):
			onCancelPressed();
			break;
		case (R.id.userinfo_edit):
			onEditPressed();
			break;
		case (R.id.userinfo_delete):
			onDeletePressed();
			break;
		case (R.id.userinfo_profiles):
			onProfilesPressed();
			break;
		default:
			break;
		}
	}

	private void onEditPressed() {
		setEditMode(true);
	}
	
	private void onConfirmPressed() {
		JSONObject builder = new JSONObject();
		JSONArray devArray = new JSONArray();
		
		String userName = ((EditText)mMainView.findViewById(R.id.userinfo_name)).getText().toString();
		String mail = ((EditText)mMainView.findViewById(R.id.userinfo_mail)).getText().toString();
		String pwd = ((EditText)mMainView.findViewById(R.id.userinfo_pwd)).getText().toString();
		String pwdConfirm = ((EditText)mMainView.findViewById(R.id.userinfo_pwd_confirm)).getText().toString();

		Boolean validate = validateData(userName, mail, pwd, pwdConfirm);
		if(validate)
		{
			return;
		}
				
		Boolean admin = ((CheckBox)mMainView.findViewById(R.id.userinfo_admin)).isChecked();
		try{
			for(Profile p : selectedProfiles){
				devArray.put(Integer.toString(p.getId()));
			}
			builder.put("profiles", devArray);
			builder.put("admin", admin);
			builder.put("email", mail);
			if(!pwd.isEmpty()) {
				builder.put("pwd", getMD5(pwd));
			}
			builder.put("name", userName);
		}
		catch (JSONException e){}
		
		if(mIsAddOperation)
			hiHouseAct.mHiHouseService.sendCommand(new Command(Request.ADD_USER, false, "users/add?token="+hiHouseAct.getUser().getToken(), builder.toString()));
		else
			hiHouseAct.mHiHouseService.sendCommand(new Command(Request.UPDATE_USER, false, "users/"+id+"/update?token="+hiHouseAct.getUser().getToken(), builder.toString()));
		hiHouseAct.setLoadingBarVisibility(View.VISIBLE);
	}

	private Boolean validateData(String userName, String mail, String pwd,
			String pwdConfirm) {
		ArrayList<String> errors = new ArrayList<String>();
		Boolean state = false;
		if(userName.isEmpty())
		{
			errors.add("Ingrese un usuario");
			state = true;
		}
		
		if(mail.isEmpty())
		{
			errors.add("Ingrese un mail");
			state = true;
		}
		else
		{
			if(!EMAIL_ADDRESS_PATTERN.matcher(mail).matches())
			{
				errors.add("Mail inválido");
				state = true;
			}
		}
		
		if(!mIsAddOperation){
			if(!pwd.isEmpty() || !pwdConfirm.isEmpty()){
				if(pwd.length() != 4){
					errors.add("La clave debe ser de 4 dígitos");
					state = true;
				}
				if (pwd.compareTo(pwdConfirm) != 0){
					errors.add("Las claves deben ser iguales");
					state = true;
				}
			}
		}
		else if(mIsAddOperation && (pwd.isEmpty() || pwdConfirm.isEmpty()))
		{
			errors.add("Ingrese una clave y la confimación");
			state = true;
		}
		else
		{
			if(pwd.length() != 4)
			{
				errors.add("La clave debe ser de 4 dígitos");
				state = true;
			}
			
			if (pwd.compareTo(pwdConfirm) != 0)
			{
				errors.add("Las claves deben ser iguales");
				state = true;
			}
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
	
	public void addNewUserResult(boolean added){
		if(!added) {
			Toast.makeText(getActivity(), "Un Usuario con ese nombre ya existe.", Toast.LENGTH_LONG).show();
			return;
		}
		if(mIsAddOperation) {
			Toast.makeText(getActivity(), "Usuario agregado exitosamente.", Toast.LENGTH_SHORT).show();
			getActivity().getFragmentManager().popBackStack();
			return;
		}
		setEditMode(false);
	}
	
	public void updateUserResult(boolean updated){
		if(!updated) {
			Toast.makeText(getActivity(), "Un Usuario con ese nombre ya existe.", Toast.LENGTH_LONG).show();
			return;
		}
		Toast.makeText(getActivity(), "Usuario actualizado exitosamente.", Toast.LENGTH_LONG).show();
		getActivity().getFragmentManager().popBackStack();
		return;
	}
	
	private void onCancelPressed() {
		//TODO rollback changes
		if(mIsAddOperation) {
			getActivity().getFragmentManager().popBackStack();
			return;
		}
		setEditMode(false);
	}
	
	private void onDeletePressed() {
		confirmDelete();
	}
	
	private void onProfilesPressed() {	
		callMultiChoiceElement();
	}
	
	public void loadProfilesList(String str)
	{
		JSONArray profArray;
		JSONObject profInfo;
		allProfileList = new ArrayList<Profile>();
		try{
			profArray = new JSONArray(str);
			profiles = new String[profArray.length()];
			for(int i=0; i<profArray.length(); i++){
    			profInfo = profArray.getJSONObject(i);
    			profiles[i] = profInfo.getString("name");
    			Profile p = new Profile(profInfo.getInt("id"), profInfo.getString("name"));
    			allProfileList.add(p);
    		}
		}
		catch(JSONException e){e.printStackTrace();}
	}
	
	public void loadUsersProfiles(String str)
	{
		JSONArray profArray;
		JSONObject profInfo;
		try{
			profArray = new JSONArray(str);
			selectedProfiles = new ArrayList<Profile>();
			mSelectedProfiles = new ArrayList<CharSequence>();
			for(int i=0; i<profArray.length(); i++){
    			profInfo = profArray.getJSONObject(i);
    			mSelectedProfiles.add(profInfo.getString("name"));
    			Profile p = new Profile(profInfo.getInt("id"), profInfo.getString("name"));
    			selectedProfiles.add(p);
    		}				
		}
		catch(JSONException e){e.printStackTrace();}
	}

	private void callMultiChoiceElement() {
		Bundle b = new Bundle();		
		b.putCharSequenceArray(MultiChoiceDialog.MULTICHOICHE_ALL, profiles);
		b.putCharSequenceArrayList(MultiChoiceDialog.MULTICHOICHE_SELECTED, (ArrayList<CharSequence>) mSelectedProfiles);
		MultiChoiceDialog md = new MultiChoiceDialog(this, b);
		md.setTargetFragment(UserInfoFragment.this, 0);
		md.show(getFragmentManager(), PROFILE_TAG);
	}
	
	private void setEditMode(boolean on) {
		mMainView.findViewById(R.id.userinfo_name).setEnabled(on);
		mMainView.findViewById(R.id.userinfo_mail).setEnabled(on);
		mMainView.findViewById(R.id.userinfo_admin).setEnabled(on);
		mMainView.findViewById(R.id.userinfo_pwd).setEnabled(on);
		mMainView.findViewById(R.id.userinfo_pwd_confirm).setEnabled(on);
		mMainView.findViewById(R.id.userinfo_profiles).setEnabled(on);
		mMainView.findViewById(R.id.userinfo_edit).setVisibility(on?View.GONE:View.VISIBLE);
		mMainView.findViewById(R.id.userinfo_delete).setVisibility(on?View.GONE:View.VISIBLE);
		mMainView.findViewById(R.id.userinfo_confirm).setVisibility(on?View.VISIBLE:View.GONE);
		mMainView.findViewById(R.id.userinfo_cancel).setVisibility(on?View.VISIBLE:View.GONE);
		mState = on;
	}

	@Override
	public void OnMultiChoiceConfirm(List<CharSequence> selected) {
		selectedProfiles = new ArrayList<Profile>();
		for (Profile p : allProfileList)
		{
			for(CharSequence ch: selected)
			{
				if(p.getName().compareTo(ch.toString()) == 0)
				{
					selectedProfiles.add(p);
				}
			}
		}
	}

	@Override
	public void OnMultiChoiceCancel(List<CharSequence> selected) {
		
	}

//password must be hashed before sent using MD5
	protected String getMD5(String text) {
		try {
			java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
			byte[] array = md.digest(text.getBytes());
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < array.length; ++i) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
			}
			return sb.toString();
		} catch (java.security.NoSuchAlgorithmException e) {
		}
		return null;
	}
	
	public void confirmDelete() {
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				 switch (which) {
				 case DialogInterface.BUTTON_POSITIVE:
					 	hiHouseAct.mHiHouseService.sendCommand(new Command(Request.DELETE_USER, false, "users/"+id+"/delete?token="+hiHouseAct.getUser().getToken(), ""));
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
        builder.setMessage("¿Está seguro que desea eliminar el usuario \""+user_name.getText().toString()+"\"?")
                     .setPositiveButton("Si", dialogClickListener)
                     .setNegativeButton("No", dialogClickListener).show();
	}
	
	public void deleteUserResult(boolean deleted) {
		if(deleted){
			Toast.makeText(getActivity(), "Usuario eliminado exitosamente.", Toast.LENGTH_LONG).show();
			getActivity().getFragmentManager().popBackStack();
		}
		else {
			Toast.makeText(getActivity(), "El Usuario no pudo ser eliminado.", Toast.LENGTH_LONG).show();
			hiHouseAct.setLoadingBarVisibility(View.GONE);
		}
		
	}
}
