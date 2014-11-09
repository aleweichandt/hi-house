package com.web.ones.hihouse;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class GCMBroadcastReceiver extends WakefulBroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if(HiHouse.s_activity != null) {
			//TODO do something
		} else{
	        ComponentName comp = new ComponentName(context.getPackageName(),
	                GCMIntentService.class.getName());
	        startWakefulService(context, (intent.setComponent(comp)));
	        setResultCode(Activity.RESULT_OK);
		}
	}
}
