package org.hisp.dhis.android.dataentry.commons;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import org.hisp.dhis.android.dataentry.R;

import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static android.content.ContentValues.TAG;

//TODO: refactor to use RxJava !!!
public class SyncService extends Service {
    private final int NOTIFICATION_ID = R.string.sync_notification_id;
    private NotificationManager notificationManager;
    private AtomicBoolean start = new AtomicBoolean(false);

    public enum SyncServiceState {SYNCING, ERROR, FATAL, SYNCED;}

    private SyncServiceState state = SyncServiceState.SYNCING;

    //TODO: find out where in onCreate or onStartCommand should the RxJava start.
    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {

/*        Log.d(TAG, "onStartCommand() called with: intent = [" + intent + "], flags = [" + flags + "], startId = [" +
                startId + "]");*/

        if (!start.get()) {
            start.set(true);
            showSyncNotification();
            Executors.newSingleThreadExecutor().submit(new Runnable() {
                @Override
                public void run() {
                    /*for (int i = 0; i < 10; i++) {
                        try {
                            sleep(1200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Log.d("ServiceThread", "something " + i);
                    }*/
                    switch (state) {
                        case SYNCING: { //interrupted.
                            notificationManager.cancel(NOTIFICATION_ID);
                            break;
                        }
                        case ERROR: {
                            showSyncErrorNotification();
                            break;
                        }
                        case FATAL: {
                            showFatalSyncError();
                            break;
                        }
                        case SYNCED: {
                            showSyncCompleteNotification();
                            break;
                        }
                    }
                }
            });
        } else {
            Log.d(TAG, "onStartCommand: Rejected !");
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void showSyncNotification() {
        String title = getString(R.string.syncing_title);
        String text = getString(R.string.syncing_text);

        Notification notification = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.ic_sync_black_24dp)
                .setContentTitle(title)
                .setContentText(text)
                .setProgress(0, 0, true)
                .setOngoing(true)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(
                NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private void showSyncCompleteNotification() {
        String title = getString(R.string.sync_complete_title);
        String text = getString(R.string.sync_complete_text);

        Notification notification = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.ic_sync_black_24dp)
                .setContentTitle(title)
                .setContentText(text)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(
                NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private void showSyncErrorNotification() {
        String title = getString(R.string.sync_error_title);
        String text = getString(R.string.sync_error_text);

        Notification notification = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.ic_sync_problem_black_24dp)
                .setContentTitle(title)
                .setContentText(text)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(
                NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private void showFatalSyncError() {
        String title = getString(R.string.fatal_sync_error_title);
        String text = getString(R.string.fatal_sync_error_text);

        Notification notification = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.ic_error_black_24dp)
                .setContentTitle(title)
                .setContentText(text)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(
                NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }
}