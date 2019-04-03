package apps.testosterol.birthdayreminder.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Objects;

import apps.testosterol.birthdayreminder.Database.ReminderDatabase;
import apps.testosterol.birthdayreminder.Util.Util;

public class AlarmWakeUpBroadcastReceiver extends BroadcastReceiver{



    @Override
    public void onReceive(Context context, Intent intent) {

        long reminderId = (long) Objects.requireNonNull(intent.getExtras()).get("reminder_id");

        if(reminderId != -1) {
            String path = ReminderDatabase.getInstance(context).daoAccess().getReminderImageFromDatabase(reminderId);
            String name = ReminderDatabase.getInstance(context).daoAccess().getReminderNameFromDatabase(reminderId);

            Util.buildNotification(context, path, name, reminderId);
        }
    }




}
