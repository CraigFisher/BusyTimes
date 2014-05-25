package edu.scranton.fisherc5.busybusy.utils;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.util.Log;

public class DateDebugger {
	public static void print(long millis) {
		Calendar exp = new GregorianCalendar();
		exp.setTimeInMillis(millis);
		Log.i("cc", millis + "........" + exp.get(Calendar.MONTH) + ".." + exp.get(Calendar.DAY_OF_MONTH) + ".." + exp.get(Calendar.HOUR_OF_DAY));
	}
	public static void print(String location, long millis) {
		Calendar exp = new GregorianCalendar();
		exp.setTimeInMillis(millis);
		Log.i("cc", location + ".." + millis + "........" + exp.get(Calendar.MONTH) + ".." + exp.get(Calendar.DAY_OF_MONTH) + ".." + exp.get(Calendar.HOUR_OF_DAY));	
	}	
	
}
