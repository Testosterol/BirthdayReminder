package apps.testosterol.birthdayreminder.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseNotifications extends SQLiteOpenHelper {

    private static final String TAG = DatabaseNotifications.class.getSimpleName();
    private static DatabaseNotifications databaseNotifications;

    private static final String COLUMN_ID = "_id";

    private static final String TABLE_NOTIFICATIONS = "notifications";

    private static final String EVENT_NAME = "event_name";
    private static final String EVENT_BIRTHDAY_DATE = "event_birthday_date";
    private static final String EVENT_DATE_TODAY = "event_date_today";
    private static final String EVENT_TIMESTAMP = "event_timestamp";
    private static final String EVENT_DAYS_DELAY_NOTIFICATION = "event_days_delay_notification";

    private static final String EVENT_EMAIL = "event_email";

    // Database name and version
    private static final String DATABASE_NAME = "notification.db";
    private static final int DATABASE_VERSION = 2;

    private static final String DATABASE_CREATE_NOTIFICATION_TABLE = "create table " + TABLE_NOTIFICATIONS + "(" +
            COLUMN_ID + " integer primary key autoincrement, " +
            " text, " + EVENT_NAME + " text, " + EVENT_BIRTHDAY_DATE + " text, " + EVENT_DATE_TODAY + " text, " +
            EVENT_TIMESTAMP + " text, " + EVENT_DAYS_DELAY_NOTIFICATION + " text, " + EVENT_EMAIL +" text);";

    public static DatabaseNotifications getDatabaseNotifications(Context context){
        if(databaseNotifications == null){
            databaseNotifications = new DatabaseNotifications(context);
        }
        return databaseNotifications;
    }


    public DatabaseNotifications(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.i(TAG, "Databeses created");
        sqLiteDatabase.execSQL(DATABASE_CREATE_NOTIFICATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.d(TAG, "Upgrade database");
        onCreate(sqLiteDatabase); // TODO: Proper upgrade handling, recreates the tables for now...
    }

    public void addEvent(String eventName, String eventBirthdayDate, String eventDateToday,
                         Long time, Long eventDaysDelayNotification, boolean eventEmail){

        /*Log.i(TAG, "Adding: " + eventType + ", " + eventName + " event to database");
        String dailySession = "NO";
        */

        ContentValues values = new ContentValues();
        values.put(EVENT_NAME, eventName);
        values.put(EVENT_BIRTHDAY_DATE, eventBirthdayDate);
        values.put(EVENT_DATE_TODAY, eventDateToday);
        values.put(EVENT_TIMESTAMP, time);
        values.put(EVENT_DAYS_DELAY_NOTIFICATION, eventDaysDelayNotification);
        values.put(EVENT_EMAIL, eventEmail);

        try{
            getWritableDatabase().insert(TABLE_NOTIFICATIONS, null, values);
        } catch (SQLiteException exception){
            close();
        }
        this.close();
    }

}
