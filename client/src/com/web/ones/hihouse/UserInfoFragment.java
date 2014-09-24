package com.web.ones.hihouse;

import java.util.ArrayList;
import java.util.List;

import com.web.ones.hihouse.MultiChoiceDialog.OnMultiChoiceDialogListener;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;

public class UserInfoFragment extends Fragment implements
OnClickListener,
OnMultiChoiceDialogListener{
	private static final String PROFILE_TAG = "userinfo_profiles";

	private boolean mIsAddOperation = false;
	private boolean mState = false;
	private String mName;
	private List<CharSequence> mSelectedProfiles;
	private View mMainView;
	
	String[] values = new String[] { "Cocina", "Living", "Ba�o",
	        						 "Habitacion 1", "Habitacion 2", "Garage" };

	public UserInfoFragment(String name, boolean isAddOperation) {
		mName = name;
		mIsAddOperation = isAddOperation;
		mState = mIsAddOperation;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSelectedProfiles = new ArrayList<CharSequence>();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		mMainView = inflater.inflate(R.layout.fragment_user_info, container, false);
		loadUserInfo();
		setEditMode(mIsAddOperation || mState);
		return mMainView;
	}
	
	private void loadUserInfo() {
		((EditText)mMainView.findViewById(R.id.userinfo_name)).setText(mName);
		((EditText)mMainView.findViewById(R.id.userinfo_mail)).setText(mName + "@aol.com");
		((EditText)mMainView.findViewById(R.id.userinfo_pwd)).setText("1234");
		((EditText)mMainView.findViewById(R.id.userinfo_pwd_confirm)).setText("1234");
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
		//TODO save changes
		if(mIsAddOperation) {
			getActivity().getFragmentManager().popBackStack();
			return;
		}
		setEditMode(false);
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
		//TODO ask before
		//TODO remove user
		getActivity().getFragmentManager().popBackStack();
	}
	
	private void onProfilesPressed() {
		Bundle b = new Bundle();
		b.putCharSequenceArray(MultiChoiceDialog.MULTICHOICHE_ALL, values);
		b.putCharSequenceArrayList(MultiChoiceDialog.MULTICHOICHE_SELECTED, 
								   (ArrayList<CharSequence>) mSelectedProfiles);
		MultiChoiceDialog md = new MultiChoiceDialog(this, b);
		md.show(getActivity().getFragmentManager(), PROFILE_TAG);
		
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
		mSelectedProfiles.clear();
		mSelectedProfiles.addAll(selected);
	}

	@Override
	public void OnMultiChoiceCancel(List<CharSequence> selected) {
		
	}

}