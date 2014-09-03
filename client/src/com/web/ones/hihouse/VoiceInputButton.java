package com.web.ones.hihouse;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

/**
 * 
 * Para usar requiere un container en la actividad que se lo vaya a agregar, y
 * a su vez implementar la interfaz OnVoiceCommand
 *
 * Adjuntar usando attachToActivity
 */

public class VoiceInputButton extends Fragment implements OnClickListener{

	private OnVoiceCommand mListener;

	public static VoiceInputButton newInstance() {
		VoiceInputButton fragment = new VoiceInputButton();
		//crear bundle y adjuntar con setArguments si hace falta
		return fragment;
	}
	
	public static void attachToActivity(Activity activity, int resId) {
		FragmentTransaction ft =activity.getFragmentManager().beginTransaction();
		ft.add(resId, VoiceInputButton.newInstance());
		ft.commit();
	}

	public VoiceInputButton() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//si hay argumentos, procesarlos con getArguments
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_voice_input_button,
				container, false);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(mListener != null) {
			Activity act = (Activity)mListener;
			ImageButton button = (ImageButton) act.findViewById(R.id.voice_button);
			if(button != null) {
				button.setOnClickListener(this);
			}
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if(mListener != null) {
			Activity act = (Activity)mListener;
			ImageButton button = (ImageButton) act.findViewById(R.id.voice_button);
			if(button != null) {
				button.setOnClickListener(null);
				//remover listener
			}
		}
	}

	@Override
	public void onClick(View arg0) {
		if (mListener != null) {
			mListener.onVoiceInputInteraction();
		}	
	}

	/*@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnVoiceCommand) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}*/

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	public interface OnVoiceCommand {
		public void onVoiceInputInteraction();
	}

}
