package apps.testosterol.birthdayreminder;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BroadcastManager extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Intent it =  new Intent(context, MainActivity.class);
            createNotification(context, it, "new mensage", "body!", "this is a mensage");
        }catch (Exception e){
            Log.i("date","error == "+e.getMessage());
        }
    }


    public void createNotification(Context context, Intent intent, CharSequence ticker, CharSequence title, CharSequence descricao){
        //create notification
        Log.d("TESTNOTIFICATION", "Birthday edit click");
        Intent notificationIntent = new Intent();
        PendingIntent contentIntent = PendingIntent.getActivity(context,0, notificationIntent, 0);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            String id = "my_channel_id";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder builder;
            if(notificationManager != null){
                NotificationChannel mChannel = notificationManager.getNotificationChannel(id);
                if(mChannel != null){
                    mChannel = new NotificationChannel(id, "notification", importance);
                    mChannel.enableVibration(true);
                    mChannel.setVibrationPattern(new long[]{0,100,0});
                    notificationManager.createNotificationChannel(mChannel);
                }
            }
            builder = new NotificationCompat.Builder(context, id);
            builder.setSmallIcon(R.drawable.button_shape_round_corner); // change
            builder.setContentTitle("random title");
            builder.setContentText("random content text");
            builder.setContentIntent(contentIntent)
                    .setAutoCancel(false)
                    .setChannelId(id);

            android.app.Notification notificationChnl = builder.build();
            if(notificationManager != null){
                notificationManager.notify(0, notificationChnl);
            }
        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                android.app.Notification.Builder builder = new android.app.Notification.Builder(context);
                builder.setSmallIcon(R.drawable.button_shape_round_corner); // change
                builder.setContentTitle("random title");
                builder.setContentText("random content text");
                builder.setContentIntent(contentIntent);

                android.app.Notification notification = builder.build();
                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                manager.notify(0, notification);
            }
        }}
    }