package com.web.ones.hihouse;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class LoginFragment extends Fragment {

	public static final String ARG_NUMBER = "arg_number";
	
	public LoginFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        int i = getArguments().getInt(ARG_NUMBER);
        String title = getResources().getStringArray(R.array.nav_drawer_items)[i];

        getActivity().setTitle(title);
        
        Button login_btn = (Button) rootView.findViewById(R.id.login);
        login_btn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				//TODO Validar datos y loguear usuario.
				Toast.makeText(getActivity(), "Login", Toast.LENGTH_SHORT).show();
			}
		});
        
        return rootView;
    }
    
}
