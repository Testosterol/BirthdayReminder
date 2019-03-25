package apps.testosterol.birthdayreminder.Database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import apps.testosterol.birthdayreminder.Reminder.Reminder;

@Dao
public interface DaoAccess {

    @Insert
    long insertOnlySingleNotification (Reminder reminder);

    @Insert
    void insertMultipleNotifications (List<Reminder> reminderList);

    @Query("SELECT * FROM Reminder WHERE _reminderId = :reminderId")
    Reminder fetchNotificationsById (long reminderId);

    @Query("SELECT * FROM Reminder")
    List<Reminder> fetchAllNotifications();

    @Query("UPDATE reminder SET reminder_image = :newImagePath WHERE _reminderId = :reminderId")
    void updateReminderImage(long reminderId, String newImagePath);

    @Query("DELETE FROM reminder WHERE _reminderId = :reminderId")
    void removeSpecificReminder(long reminderId);

    @Update
    void updateNotification (Reminder Reminder);

    @Delete
    void deleteNotification (Reminder Reminder);

}

