package edu.scranton.fisherc5.busybusy.db.daos;

import java.util.ArrayList;

import edu.scranton.fisherc5.busybusy.db.schema.ActivityTableSchema;
import edu.scranton.fisherc5.busybusy.utils.UserActivity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ActivityDao {
	
	private SQLiteDatabase database;
		   
	public ActivityDao(SQLiteDatabase db) { 
		this.database = db; 		
	}

	public UserActivity create(long id, String name, String location) {
		UserActivity activity = null;
		
		ContentValues values = new ContentValues();
		values.put(ActivityTableSchema.USER_ID, id);
		values.put(ActivityTableSchema.NAME, name);
		values.put(ActivityTableSchema.LOCATION, location);
		long new_id = database.insert(ActivityTableSchema.TABLE_NAME, null, values);
		if(new_id != -1) {
			Cursor pCursor = database.query(ActivityTableSchema.TABLE_NAME,
					ActivityTableSchema.ALL_COLUMNS, 
							"ROWID " + " = " + new_id,
							null, null, null, null);
			pCursor.moveToFirst();
			activity = cursorToUserActivity(pCursor);
			pCursor.close();			
		}
		return activity;
	}
	
	public UserActivity getActivity(long user_id, String name, String location) {
		UserActivity activity = null;
		Cursor cursor = database.query(ActivityTableSchema.TABLE_NAME,
				ActivityTableSchema.ALL_COLUMNS, 
					ActivityTableSchema.USER_ID + " = " + user_id + " and " +
					ActivityTableSchema.NAME + " = " + name + " and " +
					ActivityTableSchema.LOCATION + " = " + location,
				null, null, null, null);				
		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			activity = cursorToUserActivity(cursor);
		}
		return activity;
	}
	
	public ArrayList<UserActivity> getUserActivities(long user_id) {
		ArrayList<UserActivity> activities = new ArrayList<UserActivity>();
		Cursor cursor = database.query(ActivityTableSchema.TABLE_NAME,
				ActivityTableSchema.ALL_COLUMNS, 
					ActivityTableSchema.USER_ID + " = " + user_id,
				null, null, null, null);				
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			UserActivity activity = cursorToUserActivity(cursor);
			activities.add(activity);
			cursor.moveToNext();
		}
		return activities;
	}
	
	private UserActivity cursorToUserActivity(Cursor pCursor) {
		UserActivity activity = new UserActivity();
		activity.setUser_id(pCursor.getLong(0));
		activity.setName(pCursor.getString(1));
		activity.setLocation(pCursor.getString(2));
		return activity;
	}
	
	

}
