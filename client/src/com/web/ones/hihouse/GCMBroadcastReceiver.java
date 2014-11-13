package com.web.ones.hihouse;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.widget.Toast;

public class GCMBroadcastReceiver extends WakefulBroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if(HiHouse.s_activity != null) {
			String title = "";
			String content = "";
			Bundle extras = intent.getExtras();
			if (!extras.isEmpty()){
				title = intent.getExtras().getString("title","");
     	   		try {
					content = URLDecoder.decode(intent.getExtras().getString("content",""),"UTF-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Toast.makeText(context, title +" - "+content, Toast.LENGTH_SHORT).show();
		} else{
	        ComponentName comp = new ComponentName(context.getPackageName(),
	                GCMIntentService.class.getName());
	        startWakefulService(context, (intent.setComponent(comp)));
	        setResultCode(Activity.RESULT_OK);
		}
	}
}
