package edu.scranton.fisherc5.busybusy;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import edu.scranton.fisherc5.busybusy.db.daos.ActivityDao;
import edu.scranton.fisherc5.busybusy.db.daos.BusyTimeDao;
import edu.scranton.fisherc5.busybusy.db.daos.UserDao;
import edu.scranton.fisherc5.busybusy.db.schema.DatabaseCreator;
import edu.scranton.fisherc5.busybusy.utils.AdminData;
import edu.scranton.fisherc5.busybusy.utils.BusyTime;
import edu.scranton.fisherc5.busybusy.utils.Keys;
import edu.scranton.fisherc5.busybusy.utils.UserActivity;

import android.app.Activity;
import android.app.ListFragment;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class DailyViewFragment extends ListFragment {
	
	private long dateMillis;
	private final long THIRTY_MINUTES = DateUtils.MINUTE_IN_MILLIS * 30;
	private GregorianCalendar calendar;
	private ArrayList<ArrayList<BusyTime>> allUserBusyTimes = new ArrayList<ArrayList<BusyTime>>();
	  
	private float clockTextWeight = 1.2f;
	private float slotSetWeight = 6.0f;
	
	private Activity parentActivity = null;
	private DailyViewAdapter dailyViewAdapter;
	private ListView listView;
	
	private ActivityDao activityDao;
	private BusyTimeDao busyTimeDao;
	private UserDao userDao;
	
	private ArrayList<UserActivity> adminActivities = null;
	  
  	@Override
  	public void onAttach(Activity parentActivity) {
  		super.onAttach(parentActivity);
  		this.parentActivity = parentActivity;
  	}

	  @Override
	  public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);
	    
	  }
	  
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.daily_view_layout_fragment,
					container, false);
			LinearLayout names = (LinearLayout) view.findViewById(R.id.name_slots);
			
			TextView blankSpace = new TextView(this.parentActivity);
			blankSpace.setLayoutParams(new LinearLayout.LayoutParams(
					0, LinearLayout.LayoutParams.MATCH_PARENT, clockTextWeight));
			names.addView(blankSpace);
			
			SQLiteDatabase db;
			db = DatabaseCreator.instance(parentActivity).getWritableDatabase();
			activityDao = new ActivityDao(db);
			busyTimeDao = new BusyTimeDao(db);
			userDao = new UserDao(db);
				
			Bundle args = getArguments();
			ArrayList<UserActivity> selectedActivities = (ArrayList<UserActivity>) args.get(
																	Keys.SELECTED_ACTIVITIES_KEY);
			
			int activityCount = selectedActivities.size();
			ArrayList<BusyTime> curBtList;
			UserActivity curActivity;
			for(int i = 0; i < activityCount; i++) {
				curActivity = selectedActivities.get(i);
				
				long user_id = curActivity.getUser_id();
				String user_name = userDao.getUserNameById(user_id);
				String activity_name = curActivity.getName();
				String location = curActivity.getLocation();

				TextView textView = new TextView(this.parentActivity);				
				textView.setText(user_name + " " + activity_name + 
								" (" + location + ")");
				textView.setLayoutParams(new LinearLayout.LayoutParams(
						0, LinearLayout.LayoutParams.MATCH_PARENT, slotSetWeight / activityCount));
				names.addView(textView);
				
				curBtList = busyTimeDao.getBusyTimes(user_id, activity_name, location);
				allUserBusyTimes.add(curBtList);
			}
	    
		    listView = (ListView) view.findViewById(android.R.id.list);
//		    listView.requestFocusFromTouch();
		    //TODO: START IN PROPER POSITION
		    listView.setSelection(17);    
		    
//		    Date date = new Date();
//		    calendar = new GregorianCalendar();
//		    calendar.setTime(date);
//		    calendar.set(Calendar.HOUR_OF_DAY, 0);
//		    dateMillis = calendar.getTimeInMillis();
//		    dateMillisAdjusted = dateMillis + 30;
//		    initTestData();
		    
		    dateMillis = args.getLong(Keys.SELECTED_DATE_KEY);

		    dailyViewAdapter = new DailyViewAdapter(getActivity(), allUserBusyTimes, dateMillis);
		    setListAdapter(dailyViewAdapter);
			
			return view;
		}

//		public void initTestData() {				    
//			    ArrayList<BusyTime> temp = new ArrayList<BusyTime>();
//				
//				BusyTime t1 = new BusyTime(dateMillis + (THIRTY_MINUTES * 0), dateMillis + (THIRTY_MINUTES * 1));
//				BusyTime t2 = new BusyTime(dateMillis + (THIRTY_MINUTES * 5), dateMillis + (THIRTY_MINUTES * 7));
//				BusyTime t3 = new BusyTime(dateMillis + (THIRTY_MINUTES * 9), dateMillis + (THIRTY_MINUTES * 42));
//				
//				temp.add(t1);
//				temp.add(t2);
//				temp.add(t3);
//				
//				allUserBusyTimes.add(temp);
//				
//			    ArrayList<BusyTime> temp2 = new ArrayList<BusyTime>();
//				
//				BusyTime t12 = new BusyTime(dateMillis + (THIRTY_MINUTES * 2), dateMillis + (THIRTY_MINUTES * 3));
//				BusyTime t22 = new BusyTime(dateMillis + (THIRTY_MINUTES * 5), dateMillis + (THIRTY_MINUTES * 15));
//				BusyTime t32 = new BusyTime(dateMillis + (THIRTY_MINUTES * 25), dateMillis + (THIRTY_MINUTES * 36));
//				
//				temp2.add(t12);
//				temp2.add(t22);
//				temp2.add(t32);
//				
//				allUserBusyTimes.add(temp2);
//				
//			    ArrayList<BusyTime> temp3 = new ArrayList<BusyTime>();
//				
//				BusyTime t13 = new BusyTime(dateMillis + (THIRTY_MINUTES * 5), dateMillis + (THIRTY_MINUTES * 13));
//				BusyTime t23 = new BusyTime(dateMillis + (THIRTY_MINUTES * 19), dateMillis + (THIRTY_MINUTES * 27));
//				BusyTime t33 = new BusyTime(dateMillis + (THIRTY_MINUTES * 45), dateMillis + (THIRTY_MINUTES * 47));
//				
//				temp3.add(t13);
//				temp3.add(t23);
//				temp3.add(t33);
//				
//				allUserBusyTimes.add(temp3);
//		}
	  
} 
