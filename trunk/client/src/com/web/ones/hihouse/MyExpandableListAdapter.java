package com.web.ones.hihouse;

import java.util.HashMap;
import java.util.List;
 
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
 
public class MyExpandableListAdapter extends BaseExpandableListAdapter {
 
    private Context _context;
    //private List<String> _listDataHeader; // header titles
    private List<Profile> _listDataHeader;
    // child data in format of header title, child title
    private HashMap<String, List<String>> _listDataChild;
 
    /*public MyExpandableListAdapter(Context context, List<String> listDataHeader,
            HashMap<String, List<String>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }*/
    public MyExpandableListAdapter(Context context, List<Profile> listDataHeader) {
        this._context = context;
        this._listDataHeader = listDataHeader;
    }
 
    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition)).get(childPosititon);
    }
 
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }
 
    @Override
    public View getChildView(int groupPosition, final int childPosition,
            boolean isLastChild, View convertView, ViewGroup parent) {
 
    	//final String childText = (String) getChild(groupPosition, childPosition);
    	final Profile prof = _listDataHeader.get(groupPosition);
        final Device device = prof.getDevices().get(childPosition);
 
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.device_item, null);
        }
 
        LinearLayout deviceLine = (LinearLayout) convertView.findViewById(R.id.device_line);
        deviceLine.setBackgroundColor(Color.parseColor(device.getEstado()?"#5500ff00":"#44ff0000"));
        TextView txtDeviceId = (TextView) convertView.findViewById(R.id.device_id);
        txtDeviceId.setText(device.getId());
        TextView txtDeviceName = (TextView) convertView.findViewById(R.id.device_name);
        txtDeviceName.setText(device.getName());
        //txtDeviceName.setText(childText);
        return convertView;
    }
 
    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataHeader.get(groupPosition).getDevices().size();
    }
 
    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }
 
    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }
 
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }
 
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        Profile profile = (Profile) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.profiles_group, null);
        }
 
        TextView lblListHeader = (TextView) convertView.findViewById(R.id.profile_name);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(profile.getName());
 
        return convertView;
    }
 
    @Override
    public boolean hasStableIds() {
        return false;
    }
 
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}