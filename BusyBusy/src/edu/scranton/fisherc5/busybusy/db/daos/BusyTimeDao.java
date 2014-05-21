package edu.scranton.fisherc5.busybusy.db.daos;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;

import edu.scranton.fisherc5.busybusy.db.schema.ActivityTableSchema;
import edu.scranton.fisherc5.busybusy.db.schema.BusyTimeTableSchema;
import edu.scranton.fisherc5.busybusy.utils.BusyTime;
import edu.scranton.fisherc5.busybusy.utils.UserActivity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class BusyTimeDao {
	
	private SQLiteDatabase database;
		   
	public BusyTimeDao(SQLiteDatabase db) { 
		this.database = db;
	}

	public BusyTime insertChecked(long start, long stop, String activity_name, 
								String location, long user_id) {
		BusyTime busyTime = null;
		ArrayList<Long> rowids = new ArrayList<Long>();
		ArrayList<Long> times = new ArrayList<Long>();
		times.add(start);
		times.add(stop);
		
		//FIND INTERSECTING BUSYTIMES FIRST
		Cursor cursor = database.query(BusyTimeTableSchema.TABLE_NAME,
				BusyTimeTableSchema.RANGE_QUERY_COLUMNS, 
					BusyTimeTableSchema.USER_ID + " = " + user_id + " and " +
					BusyTimeTableSchema.ACTIVITY_NAME + " = '" + activity_name + "' and " +
					BusyTimeTableSchema.LOCATION + " = '" + location + "' and " +
					BusyTimeTableSchema.STOP_TIME + " >= " + start + " and " +
					BusyTimeTableSchema.START_TIME + " <= " + stop,
				null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			rowids.add(cursor.getLong(0));
			times.add(cursor.getLong(1));
			times.add(cursor.getLong(2));
			cursor.moveToNext();
		}
		
		int size = rowids.size();
		String deleteString = "";		//TODO: (PERFORMANCE) USE STRINGBUILDER INSTEAD
		for(int i = 0; i < size; i++) {
			if(i != 0) {
				deleteString = deleteString + " or ROWID = " + rowids.get(i);
			} else {
				deleteString = deleteString + " ROWID = " + rowids.get(i);
			}
		}
		if(size > 0) {
			database.delete(BusyTimeTableSchema.TABLE_NAME, deleteString, null);
		}						
		Collections.sort(times);

		ContentValues values = new ContentValues();
		values.put(BusyTimeTableSchema.USER_ID, user_id);
		values.put(BusyTimeTableSchema.START_TIME, times.get(0));
		values.put(BusyTimeTableSchema.STOP_TIME, times.get(times.size() - 1));
		values.put(BusyTimeTableSchema.ACTIVITY_NAME, activity_name);
		values.put(BusyTimeTableSchema.LOCATION, location);
		long rowid = database.insert(BusyTimeTableSchema.TABLE_NAME, null, values);

		cursor = database.query(BusyTimeTableSchema.TABLE_NAME,
				BusyTimeTableSchema.ALL_COLUMNS, "ROWID = " + rowid,
						null, null, null, null);
		cursor.moveToFirst();
		busyTime = cursorToBusyTime(cursor);		
		
		cursor.close();

		return busyTime;
	}
	
	public ArrayList<BusyTime> getBusyTimes(long user_id, String name, String location) {
		ArrayList<BusyTime> busyTimes  = new ArrayList<BusyTime>();
		Cursor cursor = database.query(BusyTimeTableSchema.TABLE_NAME,
				BusyTimeTableSchema.ALL_COLUMNS, 
				BusyTimeTableSchema.USER_ID + " = " + user_id + " and " +
				BusyTimeTableSchema.ACTIVITY_NAME + " = '" + name + "' and " + 
				BusyTimeTableSchema.LOCATION + " = '" + location + "'", null, null, null, null);
		
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			BusyTime busyTime = cursorToBusyTime(cursor);
			busyTimes.add(busyTime);
			cursor.moveToNext();    
		}
		    	
		cursor.close();
		return busyTimes;
		
		
	}

	private BusyTime cursorToBusyTime(Cursor cursor) {
		BusyTime bt = new BusyTime();
		bt.setStart_time(cursor.getLong(0));
		bt.setStop_time(cursor.getLong(1));
		bt.setActivity_name(cursor.getString(2));
		bt.setUser_id(cursor.getLong(3));
		bt.setLocation(cursor.getString(4));
		return bt;
	}	
	

}
