package edu.scranton.fisherc5.busybusy.db.daos;

import edu.scranton.fisherc5.busybusy.db.schema.UserTableSchema;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UserDao {
	
	private SQLiteDatabase database;
		   
	public UserDao(SQLiteDatabase db) { 
		this.database = db;
	}

	public String getUserNameById(long user_id) {
		String name = null;
		Cursor cursor = database.query(UserTableSchema.TABLE_NAME,
				UserTableSchema.USER_NAME_QUERY, 
				UserTableSchema.TABLE_ID + " = " + user_id,
				null, null, null, null);				
		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			name = cursor.getString(0);
		}
		return name;
	}

}
