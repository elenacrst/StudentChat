package com.example.elena.studentchat;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static android.support.v4.app.NotificationCompat.DEFAULT_VIBRATE;

/**
 * Created by Elena on 10/24/2017.
 */

public class FirebaseNotificationService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        String notificationTitle = null, notificationBody = null;

        if (remoteMessage.getNotification() != null) {
            notificationTitle = remoteMessage.getNotification().getTitle();
            notificationBody = remoteMessage.getNotification().getBody();
        }

           sendNotification(notificationTitle, notificationBody);
    }


    private void sendNotification(String notificationTitle, String notificationBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder =  new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setContentTitle(notificationTitle)
                .setContentText(notificationBody)
                .setDefaults(DEFAULT_VIBRATE);


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (!isAppInForeground(this, getPackageName()))
             notificationManager.notify(123, notificationBuilder.build());
    }

    private boolean isAppInForeground(Context context, String packageName){
        String[] activePackages;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        activePackages = getActivePackages(am);

        return activePackages[0].equals(packageName);
    }



    private String[] getActivePackages(ActivityManager mActivityManager) {
        Set<String> activePackages = new HashSet<>();
        List<ActivityManager.RunningAppProcessInfo> processInfos = mActivityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo processInfo : processInfos) {
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                activePackages.addAll(Arrays.asList(processInfo.pkgList));
            }
        }
        return activePackages.toArray(new String[activePackages.size()]);
    }
}

