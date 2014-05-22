package edu.scranton.fisherc5.busybusy;

import java.util.ArrayList;

import edu.scranton.fisherc5.busybusy.db.daos.BusyTimeDao;
import edu.scranton.fisherc5.busybusy.db.daos.UserDao;
import edu.scranton.fisherc5.busybusy.db.schema.DatabaseCreator;
import edu.scranton.fisherc5.busybusy.utils.BusyTime;
import edu.scranton.fisherc5.busybusy.utils.Keys;
import edu.scranton.fisherc5.busybusy.utils.UserActivity;

import android.app.Activity;
import android.app.ListFragment;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class DailyViewFragment extends ListFragment {
	
	private long dateMillis;
	private ArrayList<ArrayList<BusyTime>> allUserBusyTimes = new ArrayList<ArrayList<BusyTime>>();
	  
	private float clockTextWeight = 1.2f;
	private float slotSetWeight = 6.0f;
	
	private Activity parentActivity = null;
	private DailyViewAdapter dailyViewAdapter;
	private ListView listView;
	
	private BusyTimeDao busyTimeDao;
	private UserDao userDao;
	  
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
			busyTimeDao = new BusyTimeDao(db);
			userDao = new UserDao(db);
				
			Bundle args = getArguments();
			ArrayList<UserActivity> selectedActivities = (ArrayList<UserActivity>) args.get(
																	Keys.SELECTED_ACTIVITIES_KEY);
			
			int activityCount = selectedActivities.size();
			ArrayList<BusyTime> curBtList;
			UserActivity curActivity;
			//set TextView header for each activity, and add each activity's
			//		list of busytimes to the arraylist
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
		    	    
		    dateMillis = args.getLong(Keys.SELECTED_DATE_KEY);
		    dailyViewAdapter = new DailyViewAdapter(getActivity(), allUserBusyTimes, dateMillis);
		    setListAdapter(dailyViewAdapter);
		    
//		    DateDebugger.print("DailyViewFragment.onCreateView() Selected Date", dateMillis);
			
			return view;
		}
	  
} 
