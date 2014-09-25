package com.web.ones.hihouse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

public class LoginFragment extends Fragment {
	
	public LoginFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);   
        final CheckBox recordar = (CheckBox) rootView.findViewById(R.id.checkbox_remember);
       
        Button login_btn = (Button) rootView.findViewById(R.id.login);
        login_btn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				//TODO Validar datos y loguear usuario.
					
				Toast.makeText(getActivity(), "Login", Toast.LENGTH_SHORT).show();
				if(recordar.isChecked()){
					recordar.setText("Prueba");
				}
			}
		});
        
        return rootView;
    }
    
}
