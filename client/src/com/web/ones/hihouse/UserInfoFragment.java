package com.web.ones.hihouse;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class UserInfoFragment extends Fragment {
	private static final String ARG_NAME = "user.name";

	private String mName;

	private OnUserInfoListener mListener;

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
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_user_info, container, false);
	}

	public void onEditPressed(View v) {
		
	}
	
	public void onConfirmEdition(View v) {
		
	}
	
	public void onCancelEdition(View v) {
		
	}
	
	public void onDeletePressed(View v) {
		
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

	public interface OnUserInfoListener {
		void onEndEdition();
	}

}
