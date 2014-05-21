package edu.scranton.fisherc5.busybusy.db.schema;

import edu.scranton.fisherc5.busybusy.utils.AdminData;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.util.Log;

public class DatabaseCreator extends SQLiteOpenHelper {

	private static DatabaseCreator databaseCreator = null;
	
	//TODO: POSSIBLY CHECK IF CONTEXT HAS CHANGED
	public static DatabaseCreator instance(Context context) {
		if (databaseCreator == null){
			databaseCreator = new DatabaseCreator(context);
		}
		return databaseCreator;
	}
	
	private static final String USER_TABLE_SCHEMA = "CREATE TABLE " + 
				UserTableSchema.TABLE_NAME + " (" +	
				UserTableSchema.TABLE_ID + " INTEGER primary key, " +
				UserTableSchema.NAME + " TEXT, " +
				UserTableSchema.ADMIN + " INTEGER" +
				")";
	
	private static final String ACTIVITY_TABLE_SCHEMA = "CREATE TABLE " +
				ActivityTableSchema.TABLE_NAME + " (" +
				ActivityTableSchema.USER_ID + " INTEGER, " +
				ActivityTableSchema.NAME + " TEXT, " + 
				ActivityTableSchema.LOCATION + " TEXT, " + 
				"FOREIGN KEY(" + ActivityTableSchema.USER_ID + ") REFERENCES " +
							UserTableSchema.TABLE_NAME + "(" + UserTableSchema.TABLE_ID + 
									") ON UPDATE CASCADE, " +
				"PRIMARY KEY(" + ActivityTableSchema.USER_ID + ", " + 
								ActivityTableSchema.NAME + ", " +
								ActivityTableSchema.LOCATION + ")" +
				")";
	
	private static final String BUSY_TIME_TABLE_SCHEMA = "CREATE TABLE " +
				BusyTimeTableSchema.TABLE_NAME + " (" +
				BusyTimeTableSchema.ACTIVITY_NAME + " TEXT, " + 
				BusyTimeTableSchema.USER_ID + " TEXT, " + 
				BusyTimeTableSchema.LOCATION + " TEXT, " +
				BusyTimeTableSchema.START_TIME + " DATETIME, " +
				BusyTimeTableSchema.STOP_TIME + " DATETIME, " +
				"FOREIGN KEY( " + BusyTimeTableSchema.ACTIVITY_NAME + ", " + 
				                  BusyTimeTableSchema.USER_ID  + ", " +
				                  BusyTimeTableSchema.LOCATION + ") REFERENCES " +
				                  	ActivityTableSchema.TABLE_NAME + 
				                  	"(" + ActivityTableSchema.NAME + ", " +
				                  	      ActivityTableSchema.USER_ID + ", " +
				                  	      ActivityTableSchema.LOCATION + ") ON UPDATE CASCADE, " +
				"PRIMARY KEY(" + BusyTimeTableSchema.ACTIVITY_NAME + ", " + 
				                 BusyTimeTableSchema.START_TIME + ", " +
				                 BusyTimeTableSchema.STOP_TIME + ", " +
				                 BusyTimeTableSchema.USER_ID + ", " +
				                 BusyTimeTableSchema.LOCATION + ")" +
//				"CREATE UNIQUE INDEX a ON t1(" + BusyTimeTableSchema.ACTIVITY_ID + ", " + BusyTimeTableSchema.START_TIME + "), " + 
//				"CREATE UNIQUE INDEX b ON t1(" + BusyTimeTableSchema.ACTIVITY_ID + ", " + BusyTimeTableSchema.STOP_TIME + ") " + 
				")";
	
	public DatabaseCreator(Context context) {
		super(context, DatabaseSchema.DATABASE_NAME, null, DatabaseSchema.DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onCreate(SQLiteDatabase database) {		
		buildDatabase(database);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		Log.w(DatabaseCreator.class.getName(), "Upgrade from " +
				oldVersion + " to " + newVersion);
		database.execSQL("DROP TABLE IF EXISTS " + BusyTimeTableSchema.TABLE_NAME);
		database.execSQL("DROP TABLE IF EXISTS " + ActivityTableSchema.TABLE_NAME);
		database.execSQL("DROP TABLE IF EXISTS " + UserTableSchema.TABLE_NAME);

		buildDatabase(database);
	}
	
	public void buildDatabase(SQLiteDatabase database) {
		database.execSQL(USER_TABLE_SCHEMA);
		database.execSQL(ACTIVITY_TABLE_SCHEMA);
		database.execSQL(BUSY_TIME_TABLE_SCHEMA);
		
		ContentValues values = new ContentValues();
		values.put(UserTableSchema.TABLE_ID, AdminData.ID);
		values.put(UserTableSchema.NAME, AdminData.NAME);
		values.put(UserTableSchema.ADMIN, 1);
		database.insert(UserTableSchema.TABLE_NAME, null, values);
	}	
}
