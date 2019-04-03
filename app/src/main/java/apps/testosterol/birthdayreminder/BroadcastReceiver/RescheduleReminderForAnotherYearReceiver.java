package apps.testosterol.birthdayreminder.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
import java.util.Objects;

import apps.testosterol.birthdayreminder.Util.Util;

/*
 * Denis created this class on the 2019-03-26
 */
public class RescheduleReminderForAnotherYearReceiver  extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        long reminderIdFromIntent = (long) Objects.requireNonNull(intent.getExtras()).get("reminder_id");
        Calendar calendarFromIntent = (Calendar) intent.getExtras().get("new_reminder_date");

        if(reminderIdFromIntent != -1 && calendarFromIntent != null) {
            Util.rescheduleAlarmForAnotherYear(context, calendarFromIntent, reminderIdFromIntent);
        }
    }


}
