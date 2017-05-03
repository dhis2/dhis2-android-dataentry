package org.hisp.dhis.android.dataentry.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import org.hisp.dhis.android.dataentry.DhisApp;
import org.hisp.dhis.android.dataentry.R;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;

public class SyncService extends Service implements SyncView {
    private final static int NOTIFICATION_ID = 0xdeadbeef;

    @Inject
    SyncPresenter syncPresenter;

    @Inject
    NotificationManagerCompat notificationManager;

    // @NonNull
    SyncResult syncResult;

    @Override
    public void onCreate() {
        super.onCreate();

        // inject dependencies
        ((DhisApp) getApplicationContext()).userComponent()
                .plus(new ServiceModule()).inject(this);
        syncPresenter.onAttach(this);
        syncResult = SyncResult.idle();
    }

    @Override
    public void onDestroy() {
        syncPresenter.onDetach();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        if (!syncResult.inProgress()) {
            syncPresenter.sync();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new IllegalStateException("This service is not intended for binding.");
    }

    @NonNull
    @Override
    public Consumer<SyncResult> render() {
        return result -> {
            syncResult = result;
            if (result.inProgress()) {
                renderInProgress();
            } else if (result.isSuccess()) {
                renderSuccess();
            } else if (!result.isSuccess()) { // NOPMD
                renderFailure();
            } else {
                throw new IllegalStateException();
            }
        };
    }

    @NonNull
    private void renderInProgress() {
        String title = getString(R.string.sync_title);
        String text = getString(R.string.sync_text);

        Notification notification = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.ic_sync_black)
                .setContentTitle(title)
                .setContentText(text)
                .setProgress(0, 0, true)
                .setOngoing(true)
                .build();

        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    @NonNull
    private void renderSuccess() {
        String title = getString(R.string.sync_complete_title);
        String text = getString(R.string.sync_complete_text);

        Notification notification = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.ic_sync_black)
                .setContentTitle(title)
                .setContentText(text)
                .build();

        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    @NonNull
    private void renderFailure() {
        String title = getString(R.string.sync_error_title);
        String text = getString(R.string.sync_error_text);

        Notification notification = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.ic_sync_error_black)
                .setContentTitle(title)
                .setContentText(text)
                .build();

        notificationManager.notify(NOTIFICATION_ID, notification);
    }
}