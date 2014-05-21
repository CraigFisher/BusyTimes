package edu.scranton.fisherc5.busybusy.db.schema;

public class BusyTimeTableSchema {
	public static final String TABLE_NAME = "busy_time";
	public static final String ROW_ID = "ROWID";
	public static final String START_TIME = "start_time";
	public static final String STOP_TIME = "stop_time";
	public static final String ACTIVITY_NAME = "activity_name";
	public static final String USER_ID = "user_id";
	public static final String LOCATION = "location";
	public static final String[] ALL_COLUMNS = {
		START_TIME,
		STOP_TIME,
		ACTIVITY_NAME,
		USER_ID,
		LOCATION
	};
	public static final String[] RANGE_QUERY_COLUMNS = {
		ROW_ID,
		START_TIME,
		STOP_TIME
	};
	
}
