package com.web.ones.hihouse;

import java.util.List;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment
 * must implement the {@link PerfilInfoFragment.OnFragmentInteractionListener}
 * interface to handle interaction events. Use the
 * {@link PerfilInfoFragment#newInstance} factory method to create an instance
 * of this fragment.
 * 
 */
public class PerfilInfoFragment extends Fragment {
	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";


	private String mName;
	private List<CharSequence> mSelectedProfiles;
	private View mMainView;
	private OnFragmentInteractionListener mListener;
	private Button btn_select, btn_edit, btn_delete, btn_confirm, btn_cancel;
	
	// TODO: Rename and change types of parameters
	private String mParam1;
	private String mParam2;

	// TODO: Rename and change types and number of parameters
	public static PerfilInfoFragment newInstance(String param1, String param2) {
		PerfilInfoFragment fragment = new PerfilInfoFragment();

		Bundle args = new Bundle();
		args.putString(ARG_PARAM1, param1);
		args.putString(ARG_PARAM2, param2);
		fragment.setArguments(args);
		return fragment;
	}
	
	public static PerfilInfoFragment newInstance(String name) {
		PerfilInfoFragment fragment = new PerfilInfoFragment();
		Bundle args = new Bundle();
		args.putString(ARG_PARAM1, name);
		fragment.setArguments(args);
		return fragment;
	}

	public PerfilInfoFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mParam1 = getArguments().getString(ARG_PARAM1);
			mParam2 = getArguments().getString(ARG_PARAM2);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		mMainView = inflater.inflate(R.layout.fragment_perfil_info, container, false);
		
		btn_select = (Button) mMainView.findViewById(R.id.userinfo_profiles);
		btn_edit = (Button) mMainView.findViewById(R.id.userinfo_edit);
		btn_delete = (Button) mMainView.findViewById(R.id.userinfo_delete);
		btn_confirm = (Button) mMainView.findViewById(R.id.userinfo_confirm);
		btn_cancel = (Button) mMainView.findViewById(R.id.userinfo_cancel);
		setButtonsOnClickListeners();
		
		loadPerfilInfo();
		setEditMode(false);
		return mMainView;
	}

	private void setButtonsOnClickListeners() {
		btn_select.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				//TODO Comportamiento del boton
			}
		});
		
		btn_edit.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				//TODO Comportamiento del boton
			}
		});
		
		btn_delete.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				//TODO Comportamiento del boton
			}
		});
		
		btn_confirm.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				//TODO Comportamiento del boton
			}
		});
		
		btn_cancel.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				//TODO Comportamiento del boton
			}
		});
	}

	// TODO: Rename method, update argument and hook method into UI event
	public void onButtonPressed(Uri uri) {
		if (mListener != null) {
			mListener.onFragmentInteraction(uri);
		}
	}

	/*
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnFragmentInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}*/
	
	private void loadPerfilInfo() {
		((EditText)mMainView.findViewById(R.id.userinfo_name)).setText(mName);
		/*((EditText)mMainView.findViewById(R.id.userinfo_mail)).setText(mName + "@aol.com");
		((EditText)mMainView.findViewById(R.id.userinfo_pwd)).setText("1234");
		((EditText)mMainView.findViewById(R.id.userinfo_pwd_confirm)).setText("1234");*/
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
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated to
	 * the activity and potentially other fragments contained in that activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnFragmentInteractionListener {
		// TODO: Update argument type and name
		public void onFragmentInteraction(Uri uri);
	}

}
