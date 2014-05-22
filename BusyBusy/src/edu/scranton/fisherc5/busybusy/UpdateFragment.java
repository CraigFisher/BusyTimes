package edu.scranton.fisherc5.busybusy;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import edu.scranton.fisherc5.busybusy.db.daos.ActivityDao;
import edu.scranton.fisherc5.busybusy.db.daos.BusyTimeDao;
import edu.scranton.fisherc5.busybusy.db.schema.DatabaseCreator;
import edu.scranton.fisherc5.busybusy.utils.AdminData;
import edu.scranton.fisherc5.busybusy.utils.BusyTime;
import edu.scranton.fisherc5.busybusy.utils.UserActivity;
import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class UpdateFragment extends ListFragment
implements OnCheckedChangeListener, OnClickListener,
		   TimePicker.OnTimeChangedListener {
	  private Activity parentActivity = null;
	  private ActivityDao activityDao = null;
	  private BusyTimeDao busyTimeDao = null;
	  private ActivityListAdapter adapter = null;
	  private View mainView = null;
	  
	  private ArrayList<UserActivity> adminActivities = null;
	  private boolean[] adminActivities_Checked;
	  private int adminActivities_Size;
	  
	  public static interface CreateNewActivityListener {
		  public void createNewActivity();
	  }
	  
	  private Button newActivityButton;
	  private Button saveButton;
	  private RadioButton rbSingleDate;
	  private RadioButton rbWeekly;
	  private RadioButton rbBusyTime;
	  private RadioButton rbClearTime;
	  TimePicker startPicker;
 	  TimePicker stopPicker;
	  
	  Drawable back_checked;
	  Drawable back_unchecked;
	  
	  private GregorianCalendar mStartTime = new GregorianCalendar();
	  private GregorianCalendar mFinishedTime = new GregorianCalendar();
	  
	  private boolean SUNDAY = false;
	  private boolean MONDAY = false;
	  private boolean TUESDAY = false;
	  private boolean WEDNESDAY = false;
	  private boolean THURSDAY = false;
	  private boolean FRIDAY = false;
	  private boolean SATURDAY = false;
      
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
		    activityDao = new ActivityDao(DatabaseCreator.instance(parentActivity).getWritableDatabase());
		    adminActivities = activityDao.getUserActivities(AdminData.ID);
		    View view;
		    
            adminActivities_Size = adminActivities.size();
            if(adminActivities_Size > 0) {	//AS LONG AS USER HAS ACTIVITIES, CREATE FRAGMENT NORMALLY
                adminActivities_Checked = new boolean[adminActivities_Size];
                for(int i = 0; i < adminActivities_Size; i++) {
                		adminActivities_Checked[i] = false;
                }
            	
		        view = inflater.inflate(R.layout.update_fragment, container, false);
		        this.mainView = view;
		      		        	
	            adapter = new ActivityListAdapter(getActivity(), adminActivities);
	    			setListAdapter(adapter);
	    		    
	    			rbSingleDate = (RadioButton) view.findViewById(R.id.select_single_date);
	    			rbWeekly = (RadioButton) view.findViewById(R.id.select_weekdays);
	    			rbBusyTime = (RadioButton) view.findViewById(R.id.select_new_busy_time);
	    			rbClearTime = (RadioButton) view.findViewById(R.id.select_clear_time);
	    		   
	    			rbSingleDate.setOnCheckedChangeListener(this);
	    			rbWeekly.setOnCheckedChangeListener(this);
	    			rbBusyTime.setOnCheckedChangeListener(this);
	    			rbClearTime.setOnCheckedChangeListener(this);	    		    
	    			
	    			TextView sundayView = (TextView) view.findViewById(R.id.sunday);
	    			TextView mondayView = (TextView) view.findViewById(R.id.monday);
	    			TextView tuesdayView = (TextView) view.findViewById(R.id.tuesday);
	    			TextView wednesdayView = (TextView) view.findViewById(R.id.wednesday);
	    			TextView thursdayView = (TextView) view.findViewById(R.id.thursday);
	    			TextView fridayView = (TextView) view.findViewById(R.id.friday);
	    			TextView saturdayView = (TextView) view.findViewById(R.id.saturday);
	    			sundayView.setOnClickListener(this);
	    			mondayView.setOnClickListener(this);
	    			tuesdayView.setOnClickListener(this);
	    			wednesdayView.setOnClickListener(this);
	    			thursdayView.setOnClickListener(this);
	    			fridayView.setOnClickListener(this);
	    			saturdayView.setOnClickListener(this);
	    			
	    			back_checked = getResources().getDrawable(R.drawable.back_checked);
	    			back_unchecked = getResources().getDrawable(R.drawable.back_unchecked);
            
        			NumberPicker numberPicker = (NumberPicker) mainView.findViewById(R.id.number_picker);
           		numberPicker.setMinValue(0);
           		numberPicker.setMaxValue(16);
           		
           		mStartTime.setTime(new Date());
           		mFinishedTime.setTime(new Date());

           		startPicker = (TimePicker) mainView.findViewById(R.id.start_time);
           		stopPicker = (TimePicker) mainView.findViewById(R.id.stop_time);
           		startPicker.setOnTimeChangedListener(this);
           		stopPicker.setOnTimeChangedListener(this);
           		
           		int hour = mStartTime.get(Calendar.HOUR_OF_DAY);
           		int minute = mStartTime.get(Calendar.MINUTE);
           		
           		mStartTime.clear(Calendar.MILLISECOND);
           		mStartTime.clear(Calendar.SECOND);
           		mStartTime.clear(Calendar.MINUTE);
           		mStartTime.set(Calendar.HOUR_OF_DAY, 0);
           		mFinishedTime.clear(Calendar.MILLISECOND);
           		mFinishedTime.clear(Calendar.SECOND);
           		mFinishedTime.clear(Calendar.MINUTE);
           		mFinishedTime.set(Calendar.HOUR_OF_DAY, 0);
           		
           		startPicker.setCurrentHour(hour);
           		startPicker.setCurrentMinute(minute);
           		stopPicker.setCurrentHour(hour);
           		stopPicker.setCurrentMinute(minute);
           		           		    
           		DatePicker datePicker = (DatePicker) mainView.findViewById(R.id.date_picker);
           		datePicker.init(mStartTime.get(Calendar.YEAR), 
           		                mStartTime.get(Calendar.MONTH), 
           		                mStartTime.get(Calendar.DAY_OF_MONTH), 
           		                new DatePicker.OnDateChangedListener() {
						
           							@Override
           								public void onDateChanged(DatePicker view ,int year, 
           											              int monthOfYear, int dayOfMonth) {
           									mStartTime.set(year, monthOfYear, dayOfMonth);
           									mFinishedTime.set(year, monthOfYear, dayOfMonth);
           								}       			
           		});
           		
	    		    saveButton = (Button) view.findViewById(R.id.save_button);
	    		    saveButton.setOnClickListener(new Button.OnClickListener() {
						@Override
						public void onClick(View v) {
							saveUpdate();
						}
	    		    });
            } else {									//INFLATE EMPTY ACTIVITY VIEW INSTEAD
            		view = inflater.inflate(R.layout.update_fragment_empty, container, false);
            		newActivityButton = (Button) view.findViewById(R.id.new_activity_button);
            		newActivityButton.setOnClickListener(new Button.OnClickListener() {
						@Override
						public void onClick(View v) {
							CreateNewActivityListener listener = (CreateNewActivityListener) parentActivity;
							listener.createNewActivity();
						}
            		});
            }
			
			return view;
		}
		
		@SuppressWarnings("deprecation")
		public void onClick(View v) {
			boolean checked = true;
			
			switch (v.getId()) {	
	    		case R.id.sunday :
	    			SUNDAY = !SUNDAY;
	    			checked = SUNDAY ? true : false;
	    			break;
	    		case R.id.monday :
	    			MONDAY = !MONDAY;
	    			checked = MONDAY ? true : false;
	    			break;
	    		case R.id.tuesday :
	    			TUESDAY = !TUESDAY;
	    			checked = TUESDAY ? true : false;
	    			break;	
	    		case R.id.wednesday :
	    			WEDNESDAY = !WEDNESDAY;
	    			checked = WEDNESDAY ? true : false;
	    			break;	
	    		case R.id.thursday :
	    			THURSDAY = !THURSDAY;
	    			checked = THURSDAY ? true : false;
	    			break;	
	    		case R.id.friday :
	    			FRIDAY = !FRIDAY;
	    			checked = FRIDAY ? true : false;
	    			break;	
	    		case R.id.saturday :
	    			SATURDAY = !SATURDAY;
	    			checked = SATURDAY ? true : false;
	    			break;	
			}

		    if(checked) {
		        v.setBackgroundDrawable(back_checked);
		    } else {
		    		v.setBackgroundDrawable(back_unchecked);
		    }
		}
		
		private void saveUpdate() {
			BusyTime bt = null;
			if(busyTimeDao == null) {
				busyTimeDao = new BusyTimeDao(DatabaseCreator.instance(parentActivity).getWritableDatabase());				
			}

		    long start = mStartTime.getTimeInMillis();
		    long stop = mFinishedTime.getTimeInMillis();   
//		    DateDebugger.print("saveUpdate()", start);
//		    DateDebugger.print("saveUpdate()", stop);
		    
		    UserActivity activity;
		    boolean successful = true;
		    for (int i = 0; i < adminActivities_Size; i++) {
		    	if(adminActivities_Checked[i]) {
			    	activity = adminActivities.get(i);
			    	bt = busyTimeDao.insertChecked(start, stop, activity.getName(), activity.getLocation(), AdminData.ID);		    			
			    	if(bt == null) {
			    		successful = false;
			    	} else { //success
//			    		DateDebugger.print("UpdateFragment.saveUpdate()", bt.getStart_time());
//			    		DateDebugger.print("UpdateFragment.saveUpdate()", bt.getStop_time());
			    	}
		    	}
		    }
		    if(successful) {
		    		Toast.makeText(parentActivity, "Update Saved!", Toast.LENGTH_LONG).show();
		    } else {
		    		Toast.makeText(parentActivity, "Error occured.  Update NOT saved!", Toast.LENGTH_LONG).show();
		    }	
		}

		//For the RadioButtons only
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (isChecked) {
	            int id = buttonView.getId();
	            if(id == R.id.select_clear_time) {  
	            		rbBusyTime.setChecked(false);	
	       		} else if(id == R.id.select_new_busy_time) {
	       			rbClearTime.setChecked(false);
	       		} else {
	           		RelativeLayout pickerLayout = (RelativeLayout) mainView.findViewById(R.id.weekday_wrapper);
	           		RelativeLayout repeatLayout = (RelativeLayout) mainView.findViewById(R.id.repeat_weeks_picker);		
	           		DatePicker datePicker = (DatePicker) mainView.findViewById(R.id.date_picker);            	
	            	
	           		if(id == R.id.select_single_date) {
	           			rbWeekly.setChecked(false);
	           			repeatLayout.setVisibility(View.GONE);
	           			pickerLayout.setVisibility(View.GONE);           			
	           			datePicker.setVisibility(View.VISIBLE);
	           			
	           		} else if(id == R.id.select_weekdays) {
	           			rbSingleDate.setChecked(false);
	           			repeatLayout.setVisibility(View.VISIBLE);
	           			pickerLayout.setVisibility(View.VISIBLE);
	           			datePicker.setVisibility(View.GONE);
	           		}	       			
	       		}

           		buttonView.setChecked(true);
            }
		}
		
		@Override
		public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
			if(view.getId() == R.id.start_time) {				
			    	mStartTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
			    	mStartTime.set(Calendar.MINUTE, minute);
			} else if(view.getId() == R.id.stop_time) {
			    	mFinishedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
			    	mFinishedTime.set(Calendar.MINUTE, minute);
			}
			
			//TODO: make sure stop time doesn't surpass start time
			//     if it does, move stop time to start time
		} 
		
		public class ActivityListAdapter extends ArrayAdapter<UserActivity> {
			  private final Context context;
			  private ArrayList<UserActivity> activityList;
			  
			  public ActivityListAdapter(Context context, ArrayList<UserActivity> activityList) {
			    super(context, R.layout.activity_item_row, activityList);
			    this.context = context;
			    this.activityList = activityList;
			  }

			  @Override
			  public View getView(final int position, View convertView, ViewGroup parent) {
			    LayoutInflater inflater = (LayoutInflater) context
			        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			    View rowView = inflater.inflate(R.layout.activity_item_row, parent, false);
			    CheckBox checkbox1 = (CheckBox) rowView.findViewById(R.id.activity_selection_1);
			    CheckBox checkbox2 = (CheckBox) rowView.findViewById(R.id.activity_selection_2);
			    
			    //the following two callbacks depend on both 'position' from the getView() method and
			    //		'selectedActivities' from the PreCompareFragment class, so they cannot 
			    //		be declared in one callback.
			    checkbox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if(isChecked) {
							adminActivities_Checked[position * 2] = true;
						} else {
							adminActivities_Checked[position * 2] = false;
						}				
					}    	
			    });
			    checkbox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if(isChecked) {
							adminActivities_Checked[(position * 2) + 1] = true;
						} else {
							adminActivities_Checked[(position * 2) + 1] = false;
						}				
					}    	
			    });
			    
			    UserActivity activity = activityList.get(position * 2);
			    String name = activity.getName();
			    String location = activity.getLocation();
			    
			    checkbox1.setText(name + " (" + location + ")");			    
			    
			    if((position * 2) < activityList.size() - 1) {
				    activity = activityList.get((position * 2) + 1);
				    name = activity.getName();
				    location = activity.getLocation();			    
	
				    checkbox2.setText(name + " (" + location + ")");
			    } else {
			    		checkbox2.setVisibility(View.INVISIBLE);
			    }
				return rowView;	
			  }
			  
			  
			  
			  @Override
			  public int getCount() {
				  if(activityList.size() % 2 == 1) {
					  return (activityList.size() / 2) + 1;					  
				  } else {
					  return activityList.size() / 2;		  
				  }

			  }
		}
		
} 
