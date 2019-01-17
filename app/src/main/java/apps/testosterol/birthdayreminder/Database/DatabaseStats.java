package apps.testosterol.birthdayreminder.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

public class DatabaseStats extends SQLiteOpenHelper {

    private static final String TAG = DatabaseStats.class.getSimpleName();

    private static final String TABLE_STATS = "stats";

    private static final String COLUMN_ID = "_id";
    private static final String EVENT_TYPE = "event_type";
    private static final String EVENT_NAME = "event_name";
    private static final String EVENT_VALUE = "event_value";
    private static final String EVENT_TIMESTAMP = "event_timestamp";
    private static final String LOCATION = "event_location";
    private static final String BUFFER_VARIABLE = "event_buffer_variable1";
    private static final String BUFFER_VARIABLE2 = "event_buffer_variable2";
    private static final String BUFFER_VARIABLE3 = "event_buffer_variable3";
    private static final String EVENT_MEASURED_TIME = "event_measured_time";
    private String sLocation;

    // Database name and version
    private static final String DATABASE_NAME = "stat_events.db";
    private static final int DATABASE_VERSION = 1;

    // Create table viewable impressions
    private static final String DATABASE_CREATE_STAT_TABLE = "create table " +  TABLE_STATS + "(" +
            COLUMN_ID + " integer primary key autoincrement, " +
            " text, " + EVENT_NAME + " text, " + EVENT_VALUE + " text, " + EVENT_TYPE + " text, "
            + EVENT_TIMESTAMP + " text, " + LOCATION + " text, " + BUFFER_VARIABLE + " text, "
            + BUFFER_VARIABLE2 + " text, " + BUFFER_VARIABLE3 + " text);";

    private static DatabaseStats userStatsDatabase;

    public static DatabaseStats getStatsDatabase(Context context){
        if (userStatsDatabase == null){
            userStatsDatabase = new DatabaseStats(context);
        }
        return userStatsDatabase;
    }
    public DatabaseStats(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "Upgrade database");
        onCreate(db); // TODO: Proper upgrade handling, recreates the table for now ...
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "Databases created");
        db.execSQL(DATABASE_CREATE_STAT_TABLE);
    }

    /**
     * Method to add Stat event into Stat database.
     *
     * @param eventName - String Name of the event.
     * @param eventValue - String value of the event.
     * @param eventType - String type of the event.
     * @param time - long Time when the even occured.
     * @param location - location where event occured.
     */
    public void addStatEvent(String eventName, String eventValue, String eventType, Long time, Location location){
        Log.i(TAG,"ADDING: " + eventType + ", " + eventName + " EVENT TO DATABASE");
        if(location!=null){
            sLocation = location.toString();
        }else{
            sLocation = "";
        }
        ContentValues values = new ContentValues();
        values.put(EVENT_NAME, eventName);
        values.put(EVENT_VALUE, eventValue); // impression
        values.put(EVENT_TYPE, eventType); // 1
        values.put(EVENT_TIMESTAMP, time);
        values.put(LOCATION, sLocation);
        try {
            this.getWritableDatabase().insert(TABLE_STATS, null, values);
        } catch (SQLiteException exception) {
            this.close();
        }
        this.close();
    }

    /**
     * Method to delete all rows in database - indicating that they were flushed, hence not needed anymore.
     */
    public void deleteAllRows(){
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            Log.i(TAG, "Deleting all rows from database");
            db.delete(TABLE_STATS,null,null);
            db.close();
        }catch (SQLiteException e){
            e.printStackTrace();
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
    public JSONArray getResults(String tableName) {
        SQLiteDatabase myDataBase = this.getReadableDatabase();
        Cursor cursor = myDataBase.rawQuery("SELECT * FROM " + tableName, null);
        JSONArray resultSet     = new JSONArray();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();
            for( int i=2 ;  i< totalColumn ; i++ ) {
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
