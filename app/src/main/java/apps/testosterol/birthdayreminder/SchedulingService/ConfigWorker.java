package apps.testosterol.birthdayreminder.SchedulingService;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.util.Random;

import androidx.work.Worker;
import androidx.work.WorkerParameters;
import apps.testosterol.birthdayreminder.MainActivity;
import apps.testosterol.birthdayreminder.R;

public class ConfigWorker extends Worker {

    private static final String TAG = ConfigWorker.class.getSimpleName();

    public ConfigWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    /**
     * Method to execute the work for dispatching config / initializing config.
     *
     * @return Result of execution.
     */
    @NonNull
    @Override
    public Result doWork () {
        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "1")
                .setSmallIcon(R.drawable.ic_close_black_24dp)
                .setContentTitle("Notifikacia")
                .setContentText("Kontent bracho")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("THIS IS A TEXT THAT CONFIRMS THAT THE NOTIFICATION WORKED"))
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

        // notificationId is a unique int for each notification that you must define
        int notificationId = new Random().nextInt(7);
        notificationManager.notify(notificationId, mBuilder.build());

        return Result.success();
    }
}