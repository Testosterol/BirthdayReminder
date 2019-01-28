package apps.testosterol.birthdayreminder.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

public class DatabaseNotifications extends SQLiteOpenHelper {

    private static final String TAG = DatabaseNotifications.class.getSimpleName();
    private static DatabaseNotifications databaseNotifications;

    private static final String COLUMN_ID = "_id";
    private static final String TABLE_NOTIFICATIONS = "notifications";
    private static final String EVENT_NAME = "name";
    private static final String EVENT_IMAGE = "image";
    private static final String EVENT_BIRTHDAY_DATE = "birthdayDate";
    private static final String EVENT_DATE_OF_NOTIFICATION = "notificationDate";
    private static final String EVENT_TIMESTAMP = "event_timestamp";
    private static final String EVENT_DAYS_DELAY_NOTIFICATION = "event_days_delay_notification";

    private static final String EVENT_EMAIL = "event_email";

    // Database name and version
    private static final String DATABASE_NAME = "notification_events.db";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE_NOTIFICATION_TABLE = "create table " + TABLE_NOTIFICATIONS + "(" +
            COLUMN_ID + " integer primary key autoincrement, " +
            " text, " + EVENT_NAME + " text, " + EVENT_BIRTHDAY_DATE + " text, " + EVENT_DATE_OF_NOTIFICATION + " text, " +
            EVENT_TIMESTAMP + " text, " + EVENT_DAYS_DELAY_NOTIFICATION + " text, " + EVENT_EMAIL +" text, " + EVENT_IMAGE +" text);";

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
                         Long time, Long eventDaysDelayNotification, boolean eventEmail, String image){

        /*Log.i(TAG, "Adding: " + eventType + ", " + eventName + " event to database");
        String dailySession = "NO";
        */

        ContentValues values = new ContentValues();
        values.put(EVENT_NAME, eventName);
        values.put(EVENT_BIRTHDAY_DATE, eventBirthdayDate);
        values.put(EVENT_DATE_OF_NOTIFICATION, eventDateToday);
        values.put(EVENT_TIMESTAMP, time);
        values.put(EVENT_DAYS_DELAY_NOTIFICATION, eventDaysDelayNotification);
        values.put(EVENT_EMAIL, eventEmail);
        values.put(EVENT_IMAGE, image);

        try{
            this.getWritableDatabase().insert(TABLE_NOTIFICATIONS, null, values);
        } catch (SQLiteException exception){
            this.close();
        }
        this.close();
    }

    public void updateEventPicture(Integer id, String image){

        ContentValues values = new ContentValues();
        values.put(EVENT_IMAGE, String.valueOf(image));

        String where = "_id=?";
        String[] whereArgs = {id.toString()};

        try{
            this.getWritableDatabase().update(TABLE_NOTIFICATIONS, values, where, whereArgs);
        } catch (SQLiteException exception){
            this.close();
        }
        this.close();
    }

    /**
     * Method to retrieve all the fields from database.
     *
     * @param tableName String name of the table that we want to use.
     * @return Returns a JSONArray of all the values from database.
     */
    public JSONArray getNotifications(String tableName) {
        SQLiteDatabase myDataBase = this.getReadableDatabase();
        Cursor cursor = myDataBase.rawQuery("SELECT * FROM " + tableName, null);
        JSONArray resultSet     = new JSONArray();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();
            for( int i=0 ;  i< totalColumn ; i++ ) {
                if( cursor.getColumnName(i) != null ) {
                    try {
                        if( cursor.getString(i) != null ) {
                            Log.i(TAG, "EVERYTHING FROM DB: " + cursor.getColumnName(i) + " : " +  cursor.getString(i));
                            Log.i(TAG, "EVERYTHGING FROM DB: " + cursor.getString(i) );
                            rowObject.put(cursor.getColumnName(i) ,  cursor.getString(i) );
                        }
                        else {
                            rowObject.put( cursor.getColumnName(i) ,  "" );
                        }
                    }
                    catch( Exception e ) {
                        Log.e(TAG, "ERROR: "  + e.getMessage()  );
                    }
                }
            }
            resultSet.put(rowObject);
            cursor.moveToNext();
        }
        cursor.close();
        Log.d("TAG_NAME", resultSet.toString() );
        return resultSet;
    }

}
