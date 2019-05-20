package apps.testosterol.birthdayreminder.Reminder;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/*
 * Denis created this class on the 02/04/2019
 */

public class ReminderIdNotificationDateTuple {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    public long _reminderId;

    @ColumnInfo(name = "notification_date")
    public long notificationDateInMillis;

     public Long getNotificationDateInMillis(){
         return notificationDateInMillis;
     }

     public Long getReminderId(){
         return _reminderId;
     }

}
