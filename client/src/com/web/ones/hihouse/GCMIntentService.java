package com.web.ones.hihouse;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

public class GCMIntentService extends IntentService {

	public static final int NOTIFICATION_ID = 1;
	private static final String GCM_DATA_MSG_TITLE = "title";
	private static final String GCM_DATA_MSG_CONTENT = "content";
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public GCMIntentService() {
        super("GCMIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);
        if (!extras.isEmpty()) {
           if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                //TODO get data inside message
        	   String title = intent.getExtras().getString(GCM_DATA_MSG_TITLE,"");
        	   String content = intent.getExtras().getString(GCM_DATA_MSG_CONTENT,"");
        	   sendNotification(title, content);
            }
        }
        GCMBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String title, String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, HiHouse.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.logo)
        .setContentTitle(title)
        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
        .setContentText(msg)
        .setAutoCancel(true);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

}
