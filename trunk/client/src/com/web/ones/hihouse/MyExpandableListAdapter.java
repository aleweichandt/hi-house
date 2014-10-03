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
    private HiHouse hiHouse;
    //private List<Profile> _listDataHeader;
    private HashMap<String, List<String>> _listDataChild;
    private String selectedItem = "";
    private boolean isDevice = false;
    
    public void setSelectedItem(String item, boolean isDevice){
    	this.selectedItem = item;
    	this.isDevice = isDevice;
    	this.notifyDataSetChanged();
    }
 
    /*public MyExpandableListAdapter(Context context, List<Profile> listDataHeader) {
        this._context = context;
        this._listDataHeader = listDataHeader;
    }*/
    public MyExpandableListAdapter(HiHouse activity) {
        this._context = (Context)activity;
        this.hiHouse = activity;
    }

	@Override
    public Object getChild(int groupPosition, int childPosititon) {
		return this._listDataChild.get(hiHouse.getUser().getProfiles().get(groupPosition)).get(childPosititon);
		//return this._listDataChild.get(this._listDataHeader.get(groupPosition)).get(childPosititon);
    }
 
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }
 
    @Override
    public View getChildView(int groupPosition, final int childPosition,
            boolean isLastChild, View convertView, ViewGroup parent) {
 
    	final Profile prof = hiHouse.getUser().getProfiles().get(groupPosition);
        final Device device = prof.getDevices().get(childPosition);
 
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.device_item, null);
        }
 
        LinearLayout deviceLine = (LinearLayout) convertView.findViewById(R.id.device_line);
        if(isDevice && device.getId().equals(selectedItem)){
        	deviceLine.setBackgroundResource(device.getState()?R.drawable.item_selected_border_enabled:R.drawable.item_selected_border_disabled);
        }
        else deviceLine.setBackgroundResource(device.getState()?R.color.enabled:R.color.disabled);
        TextView txtDeviceId = (TextView) convertView.findViewById(R.id.device_id);
        txtDeviceId.setText(device.getId());
        TextView txtDeviceName = (TextView) convertView.findViewById(R.id.device_name);
        txtDeviceName.setText(device.getName());

        return convertView;
    }
 
    @Override
    public int getChildrenCount(int groupPosition) {
        return this.hiHouse.getUser().getProfiles().get(groupPosition).getDevices().size();
    }
 
    @Override
    public Object getGroup(int groupPosition) {
        return this.hiHouse.getUser().getProfiles().get(groupPosition);
    }
 
    @Override
    public int getGroupCount() {
        return this.hiHouse.getUser().getProfiles().size();
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
        if(!isDevice && profile.getName().equals(selectedItem)){
        	lblListHeader.setBackgroundResource(R.drawable.item_selected_border_profile);
        }
        else lblListHeader.setBackgroundResource(R.color.profile_item_bkgr);
 
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