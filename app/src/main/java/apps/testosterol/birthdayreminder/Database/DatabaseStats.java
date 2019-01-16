package apps.testosterol.birthdayreminder.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

public class DatabaseStats extends SQLiteOpenHelper {

    private static final String TAG = DatabaseStats.class.getSimpleName();
    private static DatabaseStats databaseStats;

    private static final String COLUMN_ID = "_id";
    private static final String TABLE_STATS = "stats";
    private static final String EVENT_TYPE = "event_type";
    private static final String EVENT_NAME = "event_name";
    private static final String EVENT_VALUE = "event_timestamp";
    private static final String EVENT_TIMESTAMP = "event_timestamp";
    private static final String EVENT_MEASURED_TIME = "event_measured_time";
    private static final String DAILY_SESSION = "daily_session";

    // Database name and version
    private static final String DATABASE_NAME = "stat_events.db";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE_STAT_TABLE = "create table " + TABLE_STATS + "(" +
            COLUMN_ID + " integer primary key autoincrement, " +
            " text, " + EVENT_TYPE + " text, " + EVENT_NAME + " text, " + EVENT_VALUE + " text, " +
            EVENT_TIMESTAMP + " text, " + EVENT_MEASURED_TIME + " text, " + DAILY_SESSION +" text);";

    public static DatabaseStats getDatabaseStats(Context context){
        if(databaseStats == null){
            databaseStats = new DatabaseStats(context);
        }
        return databaseStats;
    }


    private DatabaseStats(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.i(TAG, "Databeses created");
        sqLiteDatabase.execSQL(DATABASE_CREATE_STAT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.d(TAG, "Upgrade database");
        onCreate(sqLiteDatabase); // TODO: Proper upgrade handling, recreates the tables for now...
    }

    public void addEvent(String eventType, String eventName, String eventValue, Long time){
        Log.i(TAG, "Adding: " + eventType + ", " + eventName + " event to database");
        String dailySession = "NO";
        ContentValues values = new ContentValues();
        values.put(EVENT_TYPE, eventType);
        values.put(EVENT_NAME, eventName);
        values.put(EVENT_VALUE, eventValue);
        values.put(EVENT_TIMESTAMP, time);
        values.put(EVENT_MEASURED_TIME, time);
        values.put(DAILY_SESSION, dailySession);

        try{
            this.getWritableDatabase().insert(TABLE_STATS, null, values);
        } catch (SQLiteException exception){
            this.close();
        }
        this.close();
    }

    public JSONArray getStats(String tableName){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + tableName, null);
        JSONArray resultSet = new JSONArray();
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            int totalcolumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();
            for(int i = 0; i<totalcolumn ; i++){
                if(cursor.getColumnName(i) != null){
                    try{
                        if( cursor.getString(i) != null){
                            Log.i(TAG, "Everything from DB: " + cursor.getColumnName(i) + " : " + cursor.getString(i));
                            rowObject.put(cursor.getColumnName(i), cursor.getString(i));
                        }else{
                            rowObject.put(cursor.getColumnName(i), "");
                        }
                    } catch ( Exception e){
                        e.printStackTrace();
                    }
                }
            }
            resultSet.put(rowObject);
            cursor.moveToNext();
        }
        cursor.close();
        Log.d(TAG, "Result set stats : " + resultSet.toString());
        return resultSet;
    }

    public void deleteAllRows(){
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            Log.d(TAG, "Deleting all rows from database");
            db.delete(TABLE_STATS, null, null);
            db.close();
        }   catch (SQLiteException e){
            e.printStackTrace();
            this.close();
        }
        this.close();
    }
}
