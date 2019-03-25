package apps.testosterol.birthdayreminder.BroadcastReceiver;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import apps.testosterol.birthdayreminder.Main.MainActivity;
import apps.testosterol.birthdayreminder.R;

class NotificationIntentService extends IntentService {

    private static final int NOTIFICATION_ID = 1;
    private static final String ACTION_START = "ACTION_START";
    private static final String ACTION_DELETE = "ACTION_DELETE";
    private String eventNum = "";
    long[] pattern = {500,500};
    Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    public NotificationIntentService() {
        super(NotificationIntentService.class.getSimpleName());
    }

    public static Intent createIntentStartNotificationService(Context context) {
        Intent intent = new Intent(context, NotificationIntentService.class);
        intent.setAction(ACTION_START);
        return intent;
    }

    public static Intent createIntentDeleteNotification(Context context) {
        Intent intent = new Intent(context, NotificationIntentService.class);
        intent.setAction(ACTION_DELETE);
        return intent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(getClass().getSimpleName(), "onHandleIntent, started handling a notification event");
        try {
            String action = intent.getAction();
            if (ACTION_START.equals(action)) {
                processStartNotification(intent);
            }
        } finally {
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }
    }
    private void processStartNotification(Intent intent) {
        //eventNum = LoginActivity.getEventsNum();
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle("Scheduled Notification")
                .setAutoCancel(true)
                .setLights(Color.BLUE, 500, 500)
                .setVibrate(pattern)
                .setSound(alarmSound)
                .setColor(getResources().getColor(R.color.colorAccent))
                .setContentText("You have " + " upcoming events")
                .setSmallIcon(R.drawable.birtdhaycake)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.birtdhaycake));

        Intent mainIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, NOTIFICATION_ID, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        builder.setDeleteIntent(NotificationEventReceiver.getDeleteIntent(this));

        final NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(NOTIFICATION_ID, builder.build());
        }
    }
}