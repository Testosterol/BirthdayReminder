package apps.testosterol.birthdayreminder.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.Objects;

import apps.testosterol.birthdayreminder.Database.ReminderDatabase;

public class AlarmWakeUpBroadcastReceiver extends BroadcastReceiver{

    private final static String TAG = AlarmWakeUpBroadcastReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "alarm as been received for : " +intent.getStringExtra("reminder_name")
                + " reminder id: " + intent.getStringExtra("reminder_id")  , Toast.LENGTH_SHORT).show();

        long default_intent = (long) Objects.requireNonNull(intent.getExtras()).get("reminder_id");
        Log.d("TESTINGINTENTALARM", "the id of reminder : " + Objects.requireNonNull(intent.getExtras()).get("reminder_id"));

        if(default_intent != -1) {

            Log.d("TESTINGINTENTALARM", "removed remidner from database: " + default_intent);

            ReminderDatabase.getInstance(context).daoAccess().removeSpecificReminder(default_intent);


            ReminderDatabase.getInstance(context).daoAccess().fetchNotificationsById(default_intent);

//            Log.d("TESTINGINTENTALARM", "removed remidner from database: " + ReminderDatabase.getInstance(context).daoAccess().fetchNotificationsById(default_intent).get_reminderId());
            // create notification ?

        }else{
            Log.d("TESTINGINTENTALARM", "failed to remove from database because of default intent number");
        }
    }
}
