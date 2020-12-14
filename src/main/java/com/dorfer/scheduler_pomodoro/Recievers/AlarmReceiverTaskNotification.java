package com.dorfer.scheduler_pomodoro.Recievers;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;

import com.dorfer.scheduler_pomodoro.Activity.AddTaskActivity;
import com.dorfer.scheduler_pomodoro.Activity.DayActivity;
import com.dorfer.scheduler_pomodoro.R;

public class AlarmReceiverTaskNotification extends BroadcastReceiver {
    private static final String NOTIFICATION_CHANNEL =
            AlarmReceiverTaskNotification.class.toString() + "Channel";
    private static final int NOTIFICATION_CHANNEL_ID = 101;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent notificationIntent = new Intent(context, DayActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(DayActivity.class);
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(1,
                PendingIntent.FLAG_UPDATE_CURRENT);

        long[] pattern = {1000, 1000};

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,
                NOTIFICATION_CHANNEL);
        builder.setSmallIcon(R.drawable.ic_pomodoro)
                .setContentTitle(context.getString(R.string.pomodoro_notification_title))
                .setContentText(intent.getExtras().getString(AddTaskActivity.KEY_FOR_TASK_CONTENT))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true);

        Notification notification = builder.build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFICATION_CHANNEL_ID, notification);
    }
}
