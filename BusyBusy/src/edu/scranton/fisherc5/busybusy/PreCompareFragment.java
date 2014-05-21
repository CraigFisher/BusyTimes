package edu.scranton.fisherc5.busybusy;

import java.util.ArrayList;

import edu.scranton.fisherc5.busybusy.UpdateFragment.ActivityListAdapter;
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
import android.widget.Toast;

public class PreCompareFragment extends ListFragment
	implements Button.OnClickListener {
	
	private Activity parentActivity = null;
	private ActivityDao activityDao = null;
	private ArrayList<UserActivity> allActivities = null;
	private ArrayList<UserActivity> selectedActivities;
	
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
		    
		    Button compareButton = (Button) view.findViewById(R.id.compare_button);
		    compareButton.setOnClickListener(this);
		    		
			return view;
		}
		
		//FOR NOW, METHOD IS ONLY HANDLING CLICKS FROM THE SUBMISSION BUTTON
		@Override
		public void onClick(View v) {
			onCompareButtonListener listener = (onCompareButtonListener) parentActivity;
			Bundle args = new Bundle();
			
			
		    Date date = new Date();
		    calendar = new GregorianCalendar();
		    calendar.setTime(date);
		    calendar.set(Calendar.HOUR_OF_DAY, 0);
		    dateMillis = calendar.getTimeInMillis();
		    dateMillisAdjusted = dateMillis + 30;
			
			
			if(!selectedActivities.isEmpty()) {
				args.putSerializable(Keys.SELECTED_ACTIVITIES_KEY, selectedActivities);	
			} else {
				Toast.makeText(parentActivity, "Please selected activities to compare.", Toast.LENGTH_LONG).show();
			}			
			listener.setDailyViewFragment(args);
		}
		
		public class ActivityListAdapter extends ArrayAdapter<UserActivity> {
			  private final Context context;
			  private ArrayList<UserActivity> values;
			  
			  public ActivityListAdapter(Context context, ArrayList<UserActivity> values) {
			    super(context, R.layout.activity_item_row, values);
			    this.context = context;
			    this.values = values;
			  }

			  @Override
			  public View getView(final int position, View convertView, ViewGroup parent) {
			    LayoutInflater inflater = (LayoutInflater) context
			        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			    View rowView = inflater.inflate(R.layout.activity_item_row, parent, false);
			    CheckBox checkbox1 = (CheckBox) rowView.findViewById(R.id.activity_selection_1);
			    CheckBox checkbox2 = (CheckBox) rowView.findViewById(R.id.activity_selection_2);
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
