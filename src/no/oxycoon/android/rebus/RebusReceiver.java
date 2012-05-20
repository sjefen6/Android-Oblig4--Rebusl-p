package no.oxycoon.android.rebus;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class RebusReceiver extends BroadcastReceiver {

	private static final int NOTIFICATION_ID = 1000;

	@Override
	public void onReceive(Context context, Intent arg1) {
		// TODO: Fix method to receive the intents and start the rebus if alarm goes off
		//		or request next post if currentpost reached.
		Log.v("receiver", "Start of onReceive");

		Log.v("receiver", "Outside of if");

			if (arg1.getIntExtra("action", 0) == RebusActivity.ALARM_REQUEST_CODE) {
				Log.v("receiver", "Inside action = alarmrequestcode");
				NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

				PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, null, 0);
				Notification notification = createNotification();

				notification.setLatestEventInfo(context, "Proximity Alert!","You have found the post!", pendingIntent);

				notificationManager.notify(NOTIFICATION_ID, notification);
			} else if (arg1.getIntExtra("action", 0) == RebusActivity.PROXY_REQUEST_CODE) {

			}
		
        Log.v("receiver", "end of onReceive");
	}

	private Notification createNotification() {
		Notification notification = new Notification();

		notification.when = System.currentTimeMillis();

		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.flags |= Notification.FLAG_SHOW_LIGHTS;

		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notification.defaults |= Notification.DEFAULT_LIGHTS;

		notification.ledARGB = Color.WHITE;
		notification.ledOnMS = 1500;
		notification.ledOffMS = 1500;

		return notification;
	}

}
