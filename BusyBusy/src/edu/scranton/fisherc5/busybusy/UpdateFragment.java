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
import edu.scranton.fisherc5.busybusy.utils.DateDebugger;
import edu.scranton.fisherc5.busybusy.utils.UserActivity;
import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.format.DateUtils;
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
           DatePicker.OnDateChangedListener, TimePicker.OnTimeChangedListener,
           NumberPicker.OnValueChangeListener {
	  private Activity parentActivity = null;
	  private ActivityDao activityDao = null;
	  private BusyTimeDao busyTimeDao = null;
	  private ActivityListAdapter adapter = null;
	  private View mainView = null;
	  
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
	  
	  private GregorianCalendar mStartTimeDaily = new GregorianCalendar();
	  private GregorianCalendar mFinishedTimeDaily = new GregorianCalendar();
	  private GregorianCalendar mStartTimeWeekly = null; //only initialized if "Set Weekly" is set
	  private int mWeekCount;
	  
	  private ArrayList<UserActivity> adminActivities = null;
	  private boolean[] adminActivities_Checked;
	  private int adminActivities_Size;
	  
	  private boolean[] WEEKDAYS_SELECTED = new boolean[7];
	  private boolean SELECT_DAILY = false;
	  private boolean SELECT_WEEKLY = false;
	  
	  private final long DAY_IN_MILLIS = DateUtils.DAY_IN_MILLIS;
	  private final long WEEK_IN_MILLIS = DateUtils.WEEK_IN_MILLIS;
	  private final long MINUTE_IN_MILLIS = DateUtils.MINUTE_IN_MILLIS;
	  private final long HOUR_IN_MILLIS = DateUtils.HOUR_IN_MILLIS;
      
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
           		
           		mStartTimeDaily.setTime(new Date());
           		mFinishedTimeDaily.setTime(new Date());

           		startPicker = (TimePicker) mainView.findViewById(R.id.start_time);
           		stopPicker = (TimePicker) mainView.findViewById(R.id.stop_time);
           		startPicker.setOnTimeChangedListener(this);
           		stopPicker.setOnTimeChangedListener(this);
           		
           		int hour = mStartTimeDaily.get(Calendar.HOUR_OF_DAY);
           		int minute = mStartTimeDaily.get(Calendar.MINUTE);
           		
           		mStartTimeDaily.clear(Calendar.MILLISECOND);
           		mStartTimeDaily.clear(Calendar.SECOND);
           		mStartTimeDaily.clear(Calendar.MINUTE);
           		mStartTimeDaily.set(Calendar.HOUR_OF_DAY, 0);
           		mFinishedTimeDaily.clear(Calendar.MILLISECOND);
           		mFinishedTimeDaily.clear(Calendar.SECOND);
           		mFinishedTimeDaily.clear(Calendar.MINUTE);
           		mFinishedTimeDaily.set(Calendar.HOUR_OF_DAY, 0);
           		
           		startPicker.setCurrentHour(hour);
           		startPicker.setCurrentMinute(minute);
           		stopPicker.setCurrentHour(hour);
           		stopPicker.setCurrentMinute(minute);
           		           		    
           		DatePicker datePicker = (DatePicker) mainView.findViewById(R.id.date_picker);
           		datePicker.init(mStartTimeDaily.get(Calendar.YEAR), 
           		                mStartTimeDaily.get(Calendar.MONTH), 
           		                mStartTimeDaily.get(Calendar.DAY_OF_MONTH), 
           		                this);
           		
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
		
		private void saveUpdate() {
		    	if(SELECT_DAILY) {
		    		insertDaily();
		    	} else if(SELECT_WEEKLY) {
		    		insertWeekly();
		    	} else {
		    		Toast.makeText(parentActivity, "Select either 'Single Date' or 'Repeat Weekly'", Toast.LENGTH_LONG).show();
		    	}			
		}
		
		private void insertWeekly() {
			if(busyTimeDao == null) {
				busyTimeDao = new BusyTimeDao(DatabaseCreator.instance(parentActivity).getWritableDatabase());				
			}

		    if(adminActivities_Size < 1) {
		    		Toast.makeText(parentActivity, "No activities selected.", Toast.LENGTH_LONG).show();
		    	} else {
		    		long startDate = mStartTimeWeekly.getTimeInMillis();
		    		
		    		DateDebugger.print(startDate);
		    		
				long stopDate = startDate + (WEEK_IN_MILLIS * mWeekCount);
				
									//TODO: REFACTOR OUT 'SELECTED TIME' BASED ON NEW XML LAYOUT (THE NEW DIALOGFRAGMENT)
				long startTime = ((mStartTimeDaily.get(Calendar.HOUR_OF_DAY) * HOUR_IN_MILLIS) + 
										 (mStartTimeDaily.get(Calendar.MINUTE) * MINUTE_IN_MILLIS));
				long stopTime = ((mFinishedTimeDaily.get(Calendar.HOUR_OF_DAY) * HOUR_IN_MILLIS) + 
						 		(mFinishedTimeDaily.get(Calendar.MINUTE) * MINUTE_IN_MILLIS));
				long curDate;
				long curStartTime;
				long curStopTime;
				
				boolean successful = true;  //tracks whether every update has succesfully inserted a busytime
				UserActivity activity;
				BusyTime bt = null;
				
				int curWeekday = mStartTimeWeekly.get(Calendar.DAY_OF_WEEK);

				//for each weekday,
				for(int i = 0; i < 7; i++) {
					curDate = startDate + (i * DAY_IN_MILLIS);					
					
					//if this weekday is selected,
					if(WEEKDAYS_SELECTED[curWeekday]) {
						
						//then for every week until the stop date,
						while(curDate < stopDate) {
						    
							//for each activity,
						    	curStartTime = startTime + curDate;
						    	curStopTime = stopTime + curDate;
							for (int j = 0; j < adminActivities_Size; j++) {

								//if the activity is selected, set the BusyTime
								if(adminActivities_Checked[j]) {
						    			activity = adminActivities.get(j);
						    			
					    				DateDebugger.print("UpdateFragment.saveUpdate()", curStartTime);
					    				DateDebugger.print("UpdateFragment.saveUpdate()", curStopTime);						    			
						    			
						    			bt = busyTimeDao.insertChecked(curStartTime, curStopTime, activity.getName(), activity.getLocation(), AdminData.ID);		    			
						    	
						    			//and then check result for obvious failures
						    			if(bt == null) {
						    				successful = false;
						    			} else { //success
						    				DateDebugger.print("UpdateFragment.saveUpdate()", bt.getStart_time());
						    				DateDebugger.print("UpdateFragment.saveUpdate()", bt.getStop_time());
						    			}
						    		}
							}
							curDate = curDate + WEEK_IN_MILLIS;
						}						
					}
					curWeekday = (curWeekday + 1) % 7;
				}
				
			    if(successful) {
			    		Toast.makeText(parentActivity, "Update Saved!", Toast.LENGTH_LONG).show();
			    } else {
			    		Toast.makeText(parentActivity, "Error occured.  Update NOT saved!", Toast.LENGTH_LONG).show();
			    }
		    	}
		}
		
		private void insertDaily() {
			BusyTime bt = null;
			if(busyTimeDao == null) {
				busyTimeDao = new BusyTimeDao(DatabaseCreator.instance(parentActivity).getWritableDatabase());				
			}

		    long start = mStartTimeDaily.getTimeInMillis();
		    long stop = mFinishedTimeDaily.getTimeInMillis();   
		    DateDebugger.print("saveUpdate()", start);
		    DateDebugger.print("saveUpdate()", stop);
		    
		    UserActivity activity;
		    boolean successful = true;
		    if(adminActivities_Size < 1) {
		    		Toast.makeText(parentActivity, "No activities selected.", Toast.LENGTH_LONG).show();
		    } else {
			    for (int i = 0; i < adminActivities_Size; i++) {
				    	if(adminActivities_Checked[i]) {
					    	activity = adminActivities.get(i);
					    	bt = busyTimeDao.insertChecked(start, stop, activity.getName(), activity.getLocation(), AdminData.ID);		    			
					    	
					    	if(bt == null) {
					    		successful = false;
					    	} else { //success
//					    		DateDebugger.print("UpdateFragment.saveUpdate()", bt.getStart_time());
//					    		DateDebugger.print("UpdateFragment.saveUpdate()", bt.getStop_time());
					    	}
				    	}
			    }
			    if(successful) {
			    		Toast.makeText(parentActivity, "Update Saved!", Toast.LENGTH_LONG).show();
			    } else {
			    		Toast.makeText(parentActivity, "Error occured.  Update NOT saved!", Toast.LENGTH_LONG).show();
			    }			    	
		    }
		    
		}
		
		////////////////////////////////////////////////////////////////////////////////////////////
		//                                       LISTENERS                                        //                         
		////////////////////////////////////////////////////////////////////////////////////////////
		
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
	           			SELECT_DAILY = true;
	           			SELECT_WEEKLY = false;	           			
	           		} else if(id == R.id.select_weekdays) {
	           			rbSingleDate.setChecked(false);
	           			SELECT_DAILY = false;
	           			SELECT_WEEKLY = true;
	           			if(mStartTimeWeekly == null) {
	           				//initialize start time weekly
	           				mStartTimeWeekly = new GregorianCalendar();
	           				mStartTimeWeekly.setTime(new Date());
	                   		mStartTimeWeekly.set(Calendar.MILLISECOND, 0);
	                   		mStartTimeWeekly.set(Calendar.SECOND, 0);
	                   		mStartTimeWeekly.set(Calendar.MINUTE, 0);
	                   		mStartTimeWeekly.set(Calendar.HOUR_OF_DAY, 0);
	                   		
	                   		//initialize week count picker
	                   		mWeekCount = 0;
	                   		NumberPicker numberPicker = (NumberPicker) mainView.findViewById(R.id.week_count_picker);
	                   		numberPicker.setMinValue(0);
	                   		numberPicker.setMaxValue(16);
	                   		numberPicker.setOnValueChangedListener(this);
	           			}
	           			repeatLayout.setVisibility(View.VISIBLE);
	           			pickerLayout.setVisibility(View.VISIBLE);
	           			datePicker.setVisibility(View.GONE);
	           		}	       			
	       		}

           		buttonView.setChecked(true);
            }
		}

		@SuppressWarnings("deprecation")
		public void onClick(View v) {
			boolean checked = true;
			
			switch (v.getId()) {	
	    		case R.id.sunday :
	    			WEEKDAYS_SELECTED[0] = !WEEKDAYS_SELECTED[0];
	    			checked = WEEKDAYS_SELECTED[0] ? true : false;
	    			break;
	    		case R.id.monday :
	    			WEEKDAYS_SELECTED[1] = !WEEKDAYS_SELECTED[1];
	    			checked = WEEKDAYS_SELECTED[1] ? true : false;
	    			break;
	    		case R.id.tuesday :
	    			WEEKDAYS_SELECTED[2] = !WEEKDAYS_SELECTED[2];
	    			checked = WEEKDAYS_SELECTED[2] ? true : false;
	    			break;	
	    		case R.id.wednesday :
	    			WEEKDAYS_SELECTED[3] = !WEEKDAYS_SELECTED[3];
	    			checked = WEEKDAYS_SELECTED[3] ? true : false;
	    			break;	
	    		case R.id.thursday :
	    			WEEKDAYS_SELECTED[4] = !WEEKDAYS_SELECTED[4];
	    			checked = WEEKDAYS_SELECTED[4] ? true : false;
	    			break;	
	    		case R.id.friday :
	    			WEEKDAYS_SELECTED[5] = !WEEKDAYS_SELECTED[5];
	    			checked = WEEKDAYS_SELECTED[5] ? true : false;
	    			break;	
	    		case R.id.saturday :
	    			WEEKDAYS_SELECTED[6] = !WEEKDAYS_SELECTED[6];
	    			checked = WEEKDAYS_SELECTED[6] ? true : false;
	    			break;	
			}

		    if(checked) {
		        v.setBackgroundDrawable(back_checked);
		    } else {
		    		v.setBackgroundDrawable(back_unchecked);
		    }
		}

		@Override
		public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
			mWeekCount = newVal;
		}
		
		@Override
		public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
			if(view.getId() == R.id.start_time) {				
			    	mStartTimeDaily.set(Calendar.HOUR_OF_DAY, hourOfDay);
			    	mStartTimeDaily.set(Calendar.MINUTE, minute);
			} else if(view.getId() == R.id.stop_time) {
			    	mFinishedTimeDaily.set(Calendar.HOUR_OF_DAY, hourOfDay);
			    	mFinishedTimeDaily.set(Calendar.MINUTE, minute);
			}
			
			//TODO: make sure stop time doesn't surpass start time
			//     if it does, move stop time to start time
		}
		
		@Override
		public void onDateChanged(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mStartTimeDaily.set(year, monthOfYear, dayOfMonth);
			mFinishedTimeDaily.set(year, monthOfYear, dayOfMonth);
		}
		
		////////////////////////////////////////////////////////////////////////////////////////////
		//                                 	LIST VIEW ADAPTER                                    //                         
		////////////////////////////////////////////////////////////////////////////////////////////		
		
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
