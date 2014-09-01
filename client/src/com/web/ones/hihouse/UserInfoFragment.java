package com.web.ones.hihouse;

import java.util.ArrayList;
import java.util.List;

import com.web.ones.hihouse.MultiChoiceDialog.OnMultiChoiceDialogListener;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class UserInfoFragment extends Fragment implements OnMultiChoiceDialogListener{
	private static final String ARG_NAME = "user.name";
	
	private static final String PROFILE_TAG = "userinfo_profiles";

	private String mName;
	private List<CharSequence> mSelectedProfiles;
	private View mMainView;
	private OnUserInfoListener mListener;
	
	String[] values = new String[] { "Cocina", "Living", "Baño",
	        						 "Habitacion 1", "Habitacion 2", "Garage" };

	public static UserInfoFragment newInstance(String name) {
		UserInfoFragment fragment = new UserInfoFragment();
		Bundle args = new Bundle();
		args.putString(ARG_NAME, name);
		fragment.setArguments(args);
		return fragment;
	}

	public UserInfoFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mName = getArguments().getString(ARG_NAME);
		}
		mSelectedProfiles = new ArrayList<CharSequence>();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		mMainView = inflater.inflate(R.layout.fragment_user_info, container, false);
		loadUserInfo();
		setEditMode(false);
		return mMainView;
	}
	
	private void loadUserInfo() {
		((EditText)mMainView.findViewById(R.id.userinfo_name)).setText(mName);
		((EditText)mMainView.findViewById(R.id.userinfo_mail)).setText(mName + "@aol.com");
		((EditText)mMainView.findViewById(R.id.userinfo_pwd)).setText("1234");
		((EditText)mMainView.findViewById(R.id.userinfo_pwd_confirm)).setText("1234");
	}
	
	public void onProfilesPressed(View v) {
		Bundle b = new Bundle();
		b.putCharSequenceArray(MultiChoiceDialog.MULTICHOICHE_ALL, values);
		b.putCharSequenceArrayList(MultiChoiceDialog.MULTICHOICHE_SELECTED, 
								   (ArrayList<CharSequence>) mSelectedProfiles);
		MultiChoiceDialog md = new MultiChoiceDialog(this, b);
		md.show(getActivity().getFragmentManager(), PROFILE_TAG);
		
	}

	public void onEditPressed(View v) {
		setEditMode(true);
	}
	
	public void onConfirmEdition(View v) {
		//TODO save changes
		setEditMode(false);
	}
	
	public void onCancelEdition(View v) {
		//TODO rollback changes
		setEditMode(false);
	}
	
	public void onDeletePressed(View v) {
		//TODO ask before
		//TODO remove user
		if(mListener != null) {
			mListener.onEndEdition();
		}
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
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnUserInfoListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnUserInfoListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	@Override
	public void OnMultiChoiceConfirm(List<CharSequence> selected) {
		mSelectedProfiles.clear();
		mSelectedProfiles.addAll(selected);
	}

	@Override
	public void OnMultiChoiceCancel(List<CharSequence> selected) {
		
	}
	
	public interface OnUserInfoListener {
		void onEndEdition();
	}

}
