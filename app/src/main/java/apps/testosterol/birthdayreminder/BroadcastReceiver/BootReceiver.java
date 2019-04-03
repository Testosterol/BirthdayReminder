package apps.testosterol.birthdayreminder.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;

import apps.testosterol.birthdayreminder.Database.ReminderDatabase;
import apps.testosterol.birthdayreminder.Reminder.ReminderIdNotificationDateTuple;
import apps.testosterol.birthdayreminder.Util.Util;

public class BootReceiver  extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null) {
            if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

                // Set alarms again

                Log.d("testotherbroadcasts", "Boot receiver completed");

                List<ReminderIdNotificationDateTuple> reminderIdNotificationDateTuplesList =
                        ReminderDatabase.getInstance(context).daoAccess().getAllNotificationDatesAndIdsIntoTuple();

                for(int i =0; i<reminderIdNotificationDateTuplesList.size(); i++){
                    // if the date of notification passed until device was off
                    if(reminderIdNotificationDateTuplesList.get(i).getNotificationDateInMillis() > System.currentTimeMillis()){
                        // reschedule alarm
                        Util.rescheduleAlarmAfterBoot(context, reminderIdNotificationDateTuplesList.get(i).getReminderId(),
                                reminderIdNotificationDateTuplesList.get(i).getNotificationDateInMillis());
                    }else{
                        // remove notifications that passed when  the device was off
                        ReminderDatabase.getInstance(context).daoAccess().removeSpecificReminder(reminderIdNotificationDateTuplesList.get(i).getReminderId());
                    }
                }
            }
        }
    }


}