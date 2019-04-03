package apps.testosterol.birthdayreminder.Database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import apps.testosterol.birthdayreminder.Reminder.Reminder;
import apps.testosterol.birthdayreminder.Reminder.ReminderIdNotificationDateTuple;

@Dao
public interface DaoAccess {


    //---------------------------------------------------------------------------------------------
    /**
     * INSERT
     */
    @Insert
    long insertOnlySingleNotification (Reminder reminder);

    @Insert
    void insertMultipleNotifications (List<Reminder> reminderList);

    //---------------------------------------------------------------------------------------------
    /**
     * RETREIVE
     */
    @Query("SELECT * FROM Reminder")
    List<Reminder> fetchAllNotifications();

    @Query("SELECT reminder_image FROM reminder WHERE _reminderId = :reminderId")
    String getReminderImageFromDatabase(long reminderId);

    @Query("SELECT reminder_name FROM reminder WHERE _reminderId = :reminderId")
    String getReminderNameFromDatabase(long reminderId);

    @Query("SELECT notification_date FROM reminder WHERE _reminderId = :reminderId")
    long getNotificationDate(long reminderId);

    @Query("SELECT reminder_birthday_date FROM reminder WHERE _reminderId = :reminderId")
    String getReminderBirthdayDate(long reminderId);

    @Query("SELECT _reminderId FROM reminder WHERE reminder_name = :reminderName")
    long getReminderIdBasedOnName(String reminderName);

    @Query("SELECT _reminderId, notification_date FROM reminder")
    List<ReminderIdNotificationDateTuple> getAllNotificationDatesAndIdsIntoTuple();


    //---------------------------------------------------------------------------------------------
    /**
     * UPDATE
     */
    @Update
    void updateNotification (Reminder Reminder);

    @Query("UPDATE reminder SET reminder_image = :newImagePath WHERE _reminderId = :reminderId")
    void updateReminderImage(long reminderId, String newImagePath);

    @Query("UPDATE reminder SET notification_date = :newNotificationDate WHERE _reminderId = :reminderId")
    void updateReminderNotificationDate(long reminderId, long newNotificationDate);

    //---------------------------------------------------------------------------------------------
    /**
     * REMOVE
     */
    @Delete
    void deleteNotification (Reminder Reminder);

    @Query("DELETE FROM reminder WHERE _reminderId = :reminderId")
    void removeSpecificReminder(long reminderId);

}

