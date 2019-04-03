package apps.testosterol.birthdayreminder.Util;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

import java.util.Calendar;
import java.util.Objects;

import apps.testosterol.birthdayreminder.BroadcastReceiver.AlarmWakeUpBroadcastReceiver;
import apps.testosterol.birthdayreminder.BroadcastReceiver.RemoveReminderReceiver;
import apps.testosterol.birthdayreminder.BroadcastReceiver.RescheduleReminderForAnotherYearReceiver;
import apps.testosterol.birthdayreminder.Database.ReminderDatabase;
import apps.testosterol.birthdayreminder.R;

public class Util {

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static void dontCoverTopOfTheScreenWithApp(View view, Activity activtyContext){
        int flags = Objects.requireNonNull(view.getSystemUiVisibility());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }
        view.setSystemUiVisibility(flags);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Objects.requireNonNull(activtyContext).getWindow().setStatusBarColor(Color.WHITE);
        }

    }

    public static String getURLForResource (int resourceId) {
        return Uri.parse("android.resource://"+ Objects.requireNonNull(R.class.getPackage()).getName()+"/" +resourceId).toString();
    }

    private static Bitmap getCircleBitmap(Context context, String path) {

        Bitmap bitmap;
        if(path.contains("android.resource")){
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.birtdhaycake);
        }else{
            bitmap = BitmapFactory.decodeFile(path);
        }

        Bitmap output;
        Rect srcRect, dstRect;
        float r;
        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();

        if (width > height){
            output = Bitmap.createBitmap(height, height, Bitmap.Config.ARGB_8888);
            int left = (width - height) / 2;
            int right = left + height;
            srcRect = new Rect(left, 0, right, height);
            dstRect = new Rect(0, 0, height, height);
            r = height / 2;
        }else{
            output = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
            int top = (height - width)/2;
            int bottom = top + width;
            srcRect = new Rect(0, top, width, bottom);
            dstRect = new Rect(0, 0, width, width);
            r = width / 2;
        }

        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(r, r, r, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, srcRect, dstRect, paint);

        bitmap.recycle();

        return output;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void createNotificationChannel(Context context, String channel_id) {
        CharSequence name = context.getString(R.string.channel_name);
        String description = context.getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(channel_id, name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }
    }


    public static String getPathToImageFromUri(Uri uri, Context context) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = Objects.requireNonNull(context).getContentResolver().query(uri, projection, null,null,null);
        int column_index;
        if (cursor != null) {
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        return "";
    }

    private static PendingIntent getPendingIntentForRemoveNotification(Context context, long reminderId){
        Intent removeNotificationIntent = new Intent(context, RemoveReminderReceiver.class);
        removeNotificationIntent.putExtra("reminder_id", reminderId);
        return PendingIntent.getBroadcast(context, (int) reminderId, removeNotificationIntent, PendingIntent.FLAG_ONE_SHOT);
    }

    private static PendingIntent getPendingIntentForRescheduleNotification(Context context, long reminderId){
        Intent rescheduleIntent = new Intent(context, RescheduleReminderForAnotherYearReceiver.class);

        long newDateInMillis = ReminderDatabase.getInstance(context).daoAccess().getNotificationDate(reminderId);

        Calendar newDate = Calendar.getInstance();
        newDate.setTimeInMillis(newDateInMillis);

        rescheduleIntent.putExtra("reminder_id", reminderId);
        rescheduleIntent.putExtra("new_reminder_date", newDate);

        return PendingIntent.getBroadcast(context, (int) reminderId, rescheduleIntent, PendingIntent.FLAG_ONE_SHOT);
    }

    public static void rescheduleAlarmAfterBoot(Context context, long reminderId, long newDateInMillis){
        Calendar c = Calendar.getInstance(); // todo: remove after testing
        c.setTimeInMillis(newDateInMillis);

        Intent intent = new Intent(context, AlarmWakeUpBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) reminderId, intent, PendingIntent.FLAG_ONE_SHOT);

        AlarmManager aManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (aManager != null) {
            aManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() + 50000, pendingIntent ); // todo: get rid of additional time
        }
    }

    public static void rescheduleAlarmForAnotherYear(Context context, Calendar calendarFromIntent, long reminderIdFromIntent){

        // add to calendar one year
        calendarFromIntent.add(Calendar.YEAR, 1);

        // updated the database with new date
        ReminderDatabase.getInstance(context).daoAccess().updateReminderNotificationDate(reminderIdFromIntent,
                calendarFromIntent.getTimeInMillis());


        Calendar c = Calendar.getInstance();

        // set the alarm
        AlarmManager aManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (aManager != null) {
            aManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() + 10000, getPendingIntentForRescheduleAlarmForAnotherYear(context, (int) reminderIdFromIntent));

            dismissSpecificNotification(context, reminderIdFromIntent);
        }
    }

    private static PendingIntent getPendingIntentForRescheduleAlarmForAnotherYear(Context context, long reminderIdFromIntent){
        Intent rescheduleAlarmIntent = new Intent(context, AlarmWakeUpBroadcastReceiver.class);
        rescheduleAlarmIntent.putExtra("reminder_id", reminderIdFromIntent);
        return PendingIntent.getBroadcast(context, (int) reminderIdFromIntent, rescheduleAlarmIntent, 0);
    }

    public static void buildNotification(Context context, String path, String name, long reminderUniqueId){

        final String CHANNEL_ID = "notifications";

        long[] pattern = {500,500};
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Notification builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_cake) // app icon
                .setShowWhen(true) // show time when the notification was received
                .setColor(ContextCompat.getColor(context, R.color.bg_row_background)) // todo: investigate how to change multiple parts of the image to a different color
                .setLights(Color.BLUE, 500, 500) // todo: investigate what is this for
                .setVibrate(pattern) // vibration pattern - POSSIBLY CHANGE ?
                .setSound(alarmSound) // + sound of notification
                .setLargeIcon(getCircleBitmap(context, path))
                .setContentTitle(name + " has a birthday!")
                .addAction(R.drawable.ic_reschedule_for_another_year, "Reschedule for another year", getPendingIntentForRescheduleNotification(context, reminderUniqueId))
                .addAction(R.drawable.ic_remove_notification, "Remove", getPendingIntentForRemoveNotification(context, reminderUniqueId))
                .setAutoCancel(true) // will remove the notification when tapped on
                .setDeleteIntent(getPendingIntentForRemoveNotification(context, reminderUniqueId))
                .setContentText("Don't forget to buy a gift")
                .build();

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (mNotificationManager != null) {
            mNotificationManager.notify((int) reminderUniqueId, builder);
        }
    }

    public static void dismissSpecificNotification(Context context, long reminderIdFromIntent){
        // dismiss notification
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (mNotificationManager != null) {
            mNotificationManager.cancel((int) reminderIdFromIntent);
        }
    }

    public static void rescheduleAlarmToUsersSpecificDate(Context context, Calendar calendar, long uniqueReminderId){

        // set the alarm
        AlarmManager aManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (aManager != null) {

            aManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), getPendingIntentForRescheduleAlarmForAnotherYear(context, (int) uniqueReminderId));

        }
    }

}
