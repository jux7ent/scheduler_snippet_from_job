package com.dorfer.scheduler_pomodoro.Recievers;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.dorfer.scheduler_pomodoro.Activity.PomodoroActivity;
import com.dorfer.scheduler_pomodoro.R;

import static com.dorfer.scheduler_pomodoro.Activity.PomodoroActivity.NOTIFICATION_CHANNEL;
import static com.dorfer.scheduler_pomodoro.Activity.PomodoroActivity.NOTIFICATION_CHANNEL_ID;

public class AlarmReceiverPomodoro extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("myTag", "broadcast package");

        SharedPreferences sharedPref =
                context.getSharedPreferences(context.getString(R.string.preference_file_key),
                Context.MODE_PRIVATE);

        if (sharedPref.getBoolean(PomodoroActivity.APP_PREFERENCE_VIBRATION,
                PomodoroActivity.DEFAULT_VIBRATION)) {
            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            long[] pattern = {1000, 1000};
            v.vibrate(pattern, -1);
        }

        if (sharedPref.getBoolean(PomodoroActivity.APP_PREFERENCE_SOUND,
                PomodoroActivity.DEFAULT_SOUND)) {
            MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.alarm);
            mediaPlayer.start();
        }

        Intent notificationIntent = new Intent(context, PomodoroActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(PomodoroActivity.class);
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,
                NOTIFICATION_CHANNEL);
        builder.setSmallIcon(R.drawable.ic_pomodoro)
                .setContentTitle(context.getString(R.string.pomodoro_notification_title))
                .setContentText(context.getString(R.string.pomodoro_notification_text))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        Notification notification = builder.build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFICATION_CHANNEL_ID, notification);
    }
}
