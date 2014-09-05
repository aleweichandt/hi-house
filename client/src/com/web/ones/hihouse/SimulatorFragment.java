package com.web.ones.hihouse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class SimulatorFragment extends Fragment implements OnItemSelectedListener {
	
	private View mRootView;
	private LinearLayout simu_containter;
	private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;

	public SimulatorFragment() {
        // Empty constructor required for fragment subclasses
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_simulator, container, false);
		
		simu_containter = (LinearLayout) mRootView.findViewById(R.id.simulator_container);
		
		setProfileSpinner();		

        return mRootView;
    }

	private void setProfileSpinner() {
        
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        
        // Adding headers data
        listDataHeader.add("Exterior");
        listDataHeader.add("Cocina");
        listDataHeader.add("Living");
        listDataHeader.add("Alarmas");
        listDataHeader.add("Habitacion Chicos");
        
        // Adding child data
        List<String> exterior = new ArrayList<String>();
        exterior.add("Luz Puerta Principal");
        exterior.add("Porton Garage");
        exterior.add("Puerta Principal");
        exterior.add("Luz Jardin Trasero");
        
        List<String> cocina = new ArrayList<String>();
        cocina.add("Luz Cocina");
        cocina.add("Persiana Cocina");
        
        List<String> living = new ArrayList<String>();
        living.add("Luz Principal Living");
        living.add("Luz Fondo Living");
        living.add("Climatizador Living");
        
        List<String> alarmas = new ArrayList<String>();
        alarmas.add("Alarma Central");
        
        List<String> chicos = new ArrayList<String>();
        chicos.add("Luz Central");
        chicos.add("Velador Izquierda");
        chicos.add("Velador Derecha");
        chicos.add("Climatizador Chicos");
        
        // Header, Child data
        listDataChild.put(listDataHeader.get(0), exterior);
        listDataChild.put(listDataHeader.get(1), cocina);
        listDataChild.put(listDataHeader.get(2), living);
        listDataChild.put(listDataHeader.get(3), alarmas);
        listDataChild.put(listDataHeader.get(4), chicos);
        
		Spinner spinner = (Spinner) mRootView.findViewById(R.id.profile_spinner);
		
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<String> adapter = new ArrayAdapter<String> (this.getActivity(), android.R.layout.simple_spinner_item, listDataHeader);
		
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(this);
		
	}
	
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
		simu_containter.removeAllViews();
		List<String> list = listDataChild.get(parent.getItemAtPosition(pos));
		for(int i = 0; i<list.size(); i++){
			CheckBox check = new CheckBox(this.getActivity());
			check.setText(list.get(i));
			check.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			simu_containter.addView(check);
		}
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

}
