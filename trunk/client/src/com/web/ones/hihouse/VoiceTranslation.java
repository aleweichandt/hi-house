package com.web.ones.hihouse;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * Para usar requiere un container en la actividad que se lo vaya a agregar, y
 * a su vez implementar la interfaz OnVoiceCommand
 *
 * Adjuntar usando attachToActivity
 */

public class VoiceTranslation extends Fragment implements OnClickListener, RecognitionListener {

	private ImageButton button;
	private OnVoiceCommand mListener;
	private SpeechRecognizer speech = null;
	private Intent recognizerIntent;
	private String LOG_TAG = "VoiceRecognitionActivity";
	private ProgressBar loadingBar;
	private TextView speak_box;

	public static VoiceTranslation newInstance() {
		VoiceTranslation fragment = new VoiceTranslation();
		//crear bundle y adjuntar con setArguments si hace falta
		return fragment;
	}
	
	public static void attachToActivity(Activity activity, int resId) {
		FragmentTransaction ft =activity.getFragmentManager().beginTransaction();
		ft.add(resId, VoiceTranslation.newInstance());
		ft.commit();
	}

	public VoiceTranslation() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//si hay argumentos, procesarlos con getArguments
		
		speech = SpeechRecognizer.createSpeechRecognizer(getActivity());
		speech.setRecognitionListener(this);
		recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		//recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en");
		recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getActivity().getApplication().getPackageName());
		recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
		//recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_voice_input_button, container, false);
		button = (ImageButton) v.findViewById(R.id.voice_button);
		loadingBar = (ProgressBar) v.findViewById(R.id.loading_bar);
		speak_box = (TextView) v.findViewById(R.id.speak_box);
		return v;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(button != null) {
			button.setOnClickListener(this);
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if(button != null) {
			button.setOnClickListener(null);
			//remover listener
		}
	}

	@Override
	public void onClick(View arg0) {
		speech.startListening(recognizerIntent);
		if (mListener != null) {
			//mListener.onVoiceInputInteraction();//llama al Callback de la Activity
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnVoiceCommand) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	public interface OnVoiceCommand {
		public void onVoiceInputInteraction();
	}
	
	@Override
    public void onBeginningOfSpeech() {
        Log.i(LOG_TAG, "onBeginningOfSpeech");
        //loadingBar.setIndeterminate(false);
        //loadingBar.setMax(10);
    }
 
    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.i(LOG_TAG, "onBufferReceived: " + buffer);
    }
 
    @Override
    public void onEndOfSpeech() {
        Log.i(LOG_TAG, "onEndOfSpeech");
        //loadingBar.setIndeterminate(true);
        speak_box.setVisibility(View.GONE);
		loadingBar.setVisibility(View.VISIBLE);
    }
 
    @Override
    public void onError(int errorCode) {
        String errorMessage = getErrorText(errorCode);
        //Log.d(LOG_TAG, "FAILED " + errorMessage);
		loadingBar.setVisibility(View.GONE);
		speak_box.setVisibility(View.GONE);
		button.setVisibility(View.VISIBLE);
        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
    }
 
    @Override
    public void onEvent(int arg0, Bundle arg1) {
        //Log.i(LOG_TAG, "onEvent");
    }
 
    @Override
    public void onPartialResults(Bundle arg0) {
        //Log.i(LOG_TAG, "onPartialResults");
    }
 
    @Override
    public void onReadyForSpeech(Bundle arg0) {
        //Log.i(LOG_TAG, "onReadyForSpeech");
    	button.setVisibility(View.INVISIBLE);
		speak_box.setVisibility(View.VISIBLE);
    }
 
    @Override
    public void onResults(Bundle results) {
        //Log.i(LOG_TAG, "onResults");
        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
 
        CommandBuilder c = new CommandBuilder((HiHouse)this.getActivity());
        Command command = c.generateCommand(matches);

        if (command!=null){
        	((HiHouse)getActivity()).mHiHouseService.sendCommand(command);
        	((HiHouse)getActivity()).setLoadingBarVisibility(View.VISIBLE);
        }
        else{
        	//Toast.makeText(getActivity(), "Comando no reconocido", Toast.LENGTH_LONG).show();
        }
        
        /*if (mListener != null) {
			mListener.onVoiceInputInteraction();//llama al Callback de la Activity
		}*/
        //Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
        //TextView t = (TextView) getActivity().findViewById(R.id.text);
        //t.setText(s);
        button.setVisibility(View.VISIBLE);
        loadingBar.setVisibility(View.GONE);
		speak_box.setVisibility(View.GONE);
    }
 
    @Override
    public void onRmsChanged(float rmsdB) {
        //Log.i(LOG_TAG, "onRmsChanged: " + rmsdB);
        //loadingBar.setProgress((int) rmsdB);
    }
 
    public static String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
        case SpeechRecognizer.ERROR_AUDIO:
            message = "Audio recording error";
            break;
        case SpeechRecognizer.ERROR_CLIENT:
            message = "Client side error";
            break;
        case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
            message = "Insufficient permissions";
            break;
        case SpeechRecognizer.ERROR_NETWORK:
            message = "Network error";
            break;
        case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
            message = "Network timeout";
            break;
        case SpeechRecognizer.ERROR_NO_MATCH:
            message = "No match";
            break;
        case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
            message = "RecognitionService busy";
            break;
        case SpeechRecognizer.ERROR_SERVER:
            message = "error from server";
            break;
        case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
            message = "No speech input";
            break;
        default:
            message = "Didn't understand, please try again.";
            break;
        }
        return message;
    }

}
