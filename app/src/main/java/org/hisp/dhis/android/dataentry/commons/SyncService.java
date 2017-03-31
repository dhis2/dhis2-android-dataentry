package org.hisp.dhis.android.dataentry.commons;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.dataentry.DhisApp;
import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.server.ServerComponent;

import javax.inject.Inject;

import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;


//TODO: refactor to use RxJava !!!
public class SyncService extends Service {
    private final static String TAG = SyncService.class.getSimpleName();
    private final static int IMPORTER_ERROR = 409;
    private final static int NOTIFICATION_ID = R.string.sync_notification_id;

//    @Inject
    public D2 d2;

    private NotificationManager notificationManager;
    private Observable<Response> metadataObservable;
    Subscriber<Response> subscriber;


    //TODO: find out where in onCreate or onStartCommand should the RxJava start.
    @Override
    public void onCreate() {
        super.onCreate();

        d2 = ((DhisApp) getApplicationContext()).serverComponent().sdk(); //I should probably not be doing this...

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        metadataObservable = Observable.create(new Observable.OnSubscribe<Response>() {
            @Override
            public void call(Subscriber<? super Response> subscriber) {
                try {
                    Log.d(TAG, "call() called with: subscriber = [" + subscriber + "]");
                    Response result = d2.syncMetaData().call();
                    //TODO: check if result.isSuccessfull() then call d2.dataSync()... and return either only at
                    // the end (success) or upon error (failure)
                    subscriber.onNext(result);
//                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                    e.printStackTrace();
                }
            }
        });

        subscriber = new Subscriber<Response>() {
            @Override
            public void onCompleted() {
                //unsubscribe();
            }

            @Override
            public void onError(Throwable e) {
                //??? Exception was thrown by the Call. What does that entail ?
                showSyncErrorNotification();
                unsubscribe();
            }

            @Override
            public void onNext(Response response) {
                int code = response.code();
                if (response.isSuccessful()) {
                    showSyncCompleteNotification();
                } else if (code == IMPORTER_ERROR) {
                    showFatalSyncError();
                } else if (!response.isSuccessful()) {
                    showSyncErrorNotification();
                } else {
                    notificationManager.cancel(NOTIFICATION_ID);
                }
            }
        };
        subscriber.unsubscribe();
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {

        //TODO: rewrite this! it is wrong!
        if (subscriber.isUnsubscribed()) {
            Log.d(TAG, "onStartCommand: Syncing !");
            showSyncNotification();
            metadataObservable.subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.immediate())
                    .subscribe(subscriber);
        } else {
            //noop...
            Log.d(TAG, "onStartCommand: Rejected !");
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //// All the notification helper methods :

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