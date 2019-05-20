package apps.testosterol.birthdayreminder.Database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import apps.testosterol.birthdayreminder.Reminder.Reminder;

@Database(entities = {Reminder.class}, version = 1, exportSchema = false)
public abstract class ReminderDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "reminder_db";

    private static ReminderDatabase instance;
    public abstract DaoAccess daoAccess() ;

    public synchronized static ReminderDatabase getInstance(Context context) {
        if (instance == null) {
            instance = createReminderDatabaseInstance(context);
        }
        return instance;
    }

    public synchronized static void createDatabase(Context context) {
        if (instance == null) {
            instance = createReminderDatabaseInstance(context);
        }
    }

    private static ReminderDatabase createReminderDatabaseInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context, ReminderDatabase.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .build();
            }
            return instance;
        }



}

