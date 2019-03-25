package apps.testosterol.birthdayreminder.Database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import apps.testosterol.birthdayreminder.Reminder.Reminder;

@Database(entities = {Reminder.class}, version = 1, exportSchema = false)
public abstract class ReminderDatabase extends RoomDatabase {

    private static ReminderDatabase instance;

    public static ReminderDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context, ReminderDatabase.class, "reminderdb")
                    .allowMainThreadQueries()
                    .build();
            }
            return instance;
        }

    public abstract DaoAccess daoAccess() ;

}

