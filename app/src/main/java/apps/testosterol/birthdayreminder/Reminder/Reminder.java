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
    private long notificationDateInMillis;

    @ColumnInfo(name = "reminder_name")
    private String reminderName;

    @ColumnInfo(name = "reminder_birthday_date")
    private String reminderBirthdayDate;

    @ColumnInfo(name = "reminder_image")
    private String reminderImage;

    public Reminder(){}

    // Constructor
    public Reminder(String reminderImage, long notificationDate, String remindantName, String reminderBirthdayDate){
        this.reminderImage = reminderImage;
        this.notificationDateInMillis = notificationDate;
        this.reminderBirthdayDate = reminderBirthdayDate;
        this.reminderName = remindantName;
    }

    // Setters
    public void set_reminderId(@NonNull Integer _reminderId) {
        this._reminderId = _reminderId;
    }
    public void setReminderName(String reminderName) {
        this.reminderName = reminderName;
    }
    public void setReminderBirthdayDate(String reminderBirthdayDate) { this.reminderBirthdayDate = reminderBirthdayDate; }
    public void setReminderImage(String reminderImage) { this.reminderImage = reminderImage; }
    public void setNotificationDateInMillis(long reminderBirthdayDate) { this.notificationDateInMillis = reminderBirthdayDate; }

    // Getters
    @NonNull
    public Integer get_reminderId() {
        return _reminderId;
    }
    public long getNotificationDateInMillis(){ return notificationDateInMillis; }
    public String getReminderName() {
        return reminderName;
    }
    public String getReminderBirthdayDate() {
        return reminderBirthdayDate;
    }
    public String getReminderImage() { return reminderImage; }



}