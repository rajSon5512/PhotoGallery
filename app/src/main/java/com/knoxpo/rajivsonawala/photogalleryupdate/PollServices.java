package com.knoxpo.rajivsonawala.photogalleryupdate;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.bumptech.glide.load.engine.Resource;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PollServices extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */

    private static final String TAG = "PollServices";

    private static final long POLL_INTERVAL_MS = TimeUnit.MINUTES.toMillis(1);

    private static final String SETSERVICETAG = "SetService";


    public static Intent newIntent(Context context) {

        return new Intent(context, PollServices.class);

    }


    public PollServices() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if (!isNetwrokAvailableAndConnected())
            Log.d(TAG, "Receive Intent:" + intent);

        String query = QueryPreferences.getStoredQuery(this);
        String lastResultId = QueryPreferences.getLastResultId(this);
        List<GalleryItem> Items;

        if (query == null) {

            Items = new FlickrFetchr().fetchRecentPhotos();

        } else {

            Items = new FlickrFetchr().searchPhotos(query);

        }

        if (Items.size() == 0) {

            return;
        }

        String resultId = Items.get(0).getId();

        if (resultId.equals(lastResultId)) {

            Log.i(TAG, "got old Result" + resultId);
        } else {

            Log.i(TAG, "Got new result" + resultId);

            Resources resources = (Resources) getResources();
            Intent tempIntent = MainActivity.newIntent(this);
            PendingIntent pi = PendingIntent.getActivity(this, 0, tempIntent, 0);

            Notification notification = new Notification.Builder(this)
                    .setTicker(resources.getString(R.string.new_picture_in_title))
                    .setSmallIcon(android.R.drawable.ic_menu_report_image)
                    .setContentTitle(resources.getString(R.string.new_picture_in_title))
                    .setContentText(resources.getString(R.string.new_picture_next))
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .build();

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(0, notification);

        }

        QueryPreferences.setLastResultId(this, resultId);

    }

    private boolean isNetwrokAvailableAndConnected() {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        boolean isNetworkIsAvailable = cm.getActiveNetworkInfo() != null;

        boolean isNetworkConnected = isNetworkIsAvailable && cm.getActiveNetworkInfo().isConnected();

        return isNetworkConnected;

    }

    public static void setServiceAlarm(Context context, boolean isOn) {

        Log.d(SETSERVICETAG, "your Setservice Alaram Service call " + new Date());

        Intent i = PollServices.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);


        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (isOn) {

            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), POLL_INTERVAL_MS, pi);

        } else {

            alarmManager.cancel(pi);
            pi.cancel();

        }

    }

    public static boolean isServiceAlarmOn(Context context) {

        Intent i = PollServices.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_NO_CREATE);
        return pi != null;

    }
}
