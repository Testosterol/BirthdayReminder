package apps.testosterol.birthdayreminder.Reminder;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;

@Entity(tableName = "reminder")
public class Reminder implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private  Integer _reminderId;

    @ColumnInfo(name = "notification_date")
    private String notificationDate;

    @ColumnInfo(name = "reminder_name")
    private String reminderName;

    @ColumnInfo(name = "reminder_birthday_date")
    private String reminderBirthdayDate;

    @ColumnInfo(name = "reminder_image")
    private String reminderImage;

    public Reminder(){}

    Reminder(String reminderImage, String notificationDate, String remindantName, String reminderBirthdayDate){
        this.reminderImage = reminderImage;
        this.notificationDate = notificationDate;
        this.reminderBirthdayDate = reminderBirthdayDate;
        this.reminderName = remindantName;
    }

    public void set_reminderId(@NonNull Integer _reminderId) {
        this._reminderId = _reminderId;
    }

    public void setReminderName(String reminderName) {
        this.reminderName = reminderName;
    }

    public void setReminderBirthdayDate(String reminderBirthdayDate) {
        this.reminderBirthdayDate = reminderBirthdayDate;
    }

    public void setReminderImage(String reminderImage) {
        this.reminderImage = reminderImage;
    }

    public void setNotificationDate(String reminderBirthdayDate) {
        this.notificationDate = reminderBirthdayDate;
    }


    @NonNull
    public Integer get_reminderId() {
        return _reminderId;
    }

    public String getNotificationDate(){
        return notificationDate;
    }

    public String getReminderName() {
        return reminderName;
    }

    public String getReminderBirthdayDate() {
        return reminderBirthdayDate;
    }

    public String getReminderImage() {
        return reminderImage;
    }

}