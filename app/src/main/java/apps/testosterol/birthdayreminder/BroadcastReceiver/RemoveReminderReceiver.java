package apps.testosterol.birthdayreminder.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Objects;

import apps.testosterol.birthdayreminder.Database.ReminderDatabase;
import apps.testosterol.birthdayreminder.Util.Util;

/*
 * Denis created this class on the 2019-03-26
 */

public class RemoveReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        long reminderIdFromIntent = (long) Objects.requireNonNull(intent.getExtras()).get("reminder_id");

        if(reminderIdFromIntent != -1) {

            ReminderDatabase.getInstance(context).daoAccess().removeSpecificReminder(reminderIdFromIntent);

            Util.dismissSpecificNotification(context, reminderIdFromIntent);
        }
    }
}
