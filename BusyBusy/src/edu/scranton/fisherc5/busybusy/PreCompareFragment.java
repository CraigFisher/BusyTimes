package edu.scranton.fisherc5.busybusy;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import edu.scranton.fisherc5.busybusy.db.daos.ActivityDao;
import edu.scranton.fisherc5.busybusy.db.schema.DatabaseCreator;
import edu.scranton.fisherc5.busybusy.utils.AdminData;
import edu.scranton.fisherc5.busybusy.utils.Keys;
import edu.scranton.fisherc5.busybusy.utils.UserActivity;
import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Toast;

public class PreCompareFragment extends ListFragment
	implements Button.OnClickListener {
	
	private Activity parentActivity = null;
	private ActivityDao activityDao = null;
	private ArrayList<UserActivity> allActivities = null;
	private ArrayList<UserActivity> selectedActivities;
	private Calendar calendar;
	
	private ActivityListAdapter adapter;
	
	public static interface onCompareButtonListener {
		public void setDailyViewFragment(Bundle args);
	}
	  
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
			View view = inflater.inflate(R.layout.precompare_fragment,
					container, false);
			selectedActivities = new ArrayList<UserActivity>();
		    activityDao = new ActivityDao(DatabaseCreator.instance(parentActivity).getWritableDatabase());
            allActivities = activityDao.getUserActivities(AdminData.ID);
        		adapter = new ActivityListAdapter(getActivity(), allActivities);
		    setListAdapter(adapter);
		    
		    calendar = new GregorianCalendar();
		    calendar.setTime(new Date());
       		calendar.clear(Calendar.MILLISECOND);
       		calendar.clear(Calendar.SECOND);
       		calendar.set(Calendar.HOUR_OF_DAY, 0);
       		calendar.set(Calendar.MINUTE, 2);    //slight offset to ensure dailyViewAdapter's getView() calculations work
       											//		possibly unnecessary
		    DatePicker datePicker = (DatePicker) view.findViewById(R.id.precompare_date_picker);
       		datePicker.init(calendar.get(Calendar.YEAR), 
       		                calendar.get(Calendar.MONTH), 
       		                calendar.get(Calendar.DAY_OF_MONTH), 
       		                new DatePicker.OnDateChangedListener() {
       							@Override
       								public void onDateChanged(DatePicker view ,int year, 
       											              int monthOfYear, int dayOfMonth) {
       									calendar.set(year, monthOfYear, dayOfMonth);       									
       								}       			
       		});

		    Button compareButton = (Button) view.findViewById(R.id.compare_button);
		    compareButton.setOnClickListener(this);
		    		
			return view;
		}
		
		//FOR NOW, METHOD IS ONLY HANDLING CLICKS FROM THE SUBMISSION BUTTON
		@Override
		public void onClick(View v) {
			if(!selectedActivities.isEmpty()) {
				long dateMillis = calendar.getTimeInMillis();
				Bundle args = new Bundle();
				args.putLong(Keys.SELECTED_DATE_KEY, dateMillis);
				args.putSerializable(Keys.SELECTED_ACTIVITIES_KEY, selectedActivities);

//				DateDebugger.print("PreCompareFragment.onClick() Selected Date", dateMillis);
				
				onCompareButtonListener listener = (onCompareButtonListener) parentActivity;
				listener.setDailyViewFragment(args);			
			} else {
				Toast.makeText(parentActivity, "Please select activities to compare.", Toast.LENGTH_LONG).show();
			}
		}
		
		public class ActivityListAdapter extends ArrayAdapter<UserActivity> {
			  private final Context context;
			  private ArrayList<UserActivity> values;
			  
			  public ActivityListAdapter(Context context, ArrayList<UserActivity> values) {
			    super(context, R.layout.activity_item_row, values);
			    this.context = context;
			    this.values = values;
			  }

			  //this getView method creates two columns for the list, corresponding to
			  //			'checkbox1' and 'checkbox2'
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
						UserActivity userActivity = allActivities.get(position * 2);
						if(isChecked) {
							selectedActivities.add(userActivity);
						} else {
							selectedActivities.remove(userActivity);
						}
					}	    	
			    });
			    checkbox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						UserActivity userActivity = allActivities.get((position * 2) + 1);
						if(isChecked) {
							selectedActivities.add(userActivity);
						} else {
							selectedActivities.remove(userActivity);
						}
					}	    	
			    });		    
			    
			    UserActivity activity = values.get(position * 2);
			    String name = activity.getName();
			    String location = activity.getLocation();
			    
			    checkbox1.setText(name + " (" + location + ")");			    			    
			    if((position * 2) < values.size() - 1) {
				    activity = values.get((position * 2) + 1);
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
				  if(values.size() % 2 == 1) {
					  return (values.size() / 2) + 1;					  
				  } else {
					  return values.size() / 2;		  
				  }

			  }
		}

	  
} 
