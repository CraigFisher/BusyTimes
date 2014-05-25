package edu.scranton.fisherc5.busybusy;

import java.util.ArrayList;

import edu.scranton.fisherc5.busybusy.utils.BusyTime;
import edu.scranton.fisherc5.busybusy.utils.DateDebugger;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DailyViewAdapter extends ArrayAdapter<BusyTime> {
	
    private Context mContext;
    private Drawable back_checked;
    private Drawable back_unchecked;
    
	private float clockTextWeight = 1.2f;
	private float slotSetWeight = 6.0f;
	
	private long dateMillis;
	private final long THIRTY_MINUTES = DateUtils.MINUTE_IN_MILLIS * 30;
	
	private static long[] dailyTimeIntervals = new long[48];
	private String[] dailyTimeStrings = {
			"12:00", "12:30", "1:00", "1:30", "2:00", "2:30", "3:00", "3:30",
			"4:00", "4:30", "5:00", "5:30", "6:00", "6:30", "7:00", "7:30",
			"8:00", "8:30", "9:00", "9:30", "10:00", "10:30", "11:00", "11:30",
			"12:00", "12:30", "1:00", "1:30", "2:00", "2:30", "3:00", "3:30",
			"4:00", "4:30", "5:00", "5:30", "6:00", "6:30", "7:00", "7:30",
			"8:00", "8:30", "9:00", "9:30", "10:00", "10:30", "11:00", "11:30" 
	};

	LayoutInflater inflater;
    ArrayList<ArrayList<BusyTime>> userBusyTimes;
    
    public DailyViewAdapter(Context context, ArrayList<ArrayList<BusyTime>> _userBusyTimes,
    								long _dateMillis) {
    		super(context, R.layout.daily_view_row);
        mContext = context;
		this.userBusyTimes = _userBusyTimes;
		this.dateMillis = _dateMillis;
        back_checked = mContext.getResources().getDrawable(R.drawable.back_checked);
		back_unchecked = mContext.getResources().getDrawable(R.drawable.back_unchecked);
		
//		DateDebugger.print("adapter.getView() Selected Date", this.dateMillis);

		inflater = (LayoutInflater) mContext.
				getSystemService(Context.LAYOUT_INFLATER_SERVICE);	    
	    initDailyTimes();
    }
    
	public void initDailyTimes() {
		for(int i = 0; i < 48; i++) {
			dailyTimeIntervals[i] = (i * THIRTY_MINUTES) + dateMillis;
		}
	}

    public long getItemId(int position) {
        return 0;
    }
    
    // create a new ImageView for each item referenced by the Adapter
    //TODO: make listview start midway down
    public View getView(int position, View view, ViewGroup parent) {
	    	ViewHolder holder = new ViewHolder(); // our view holder of the row
	        
	    	float slotCount = userBusyTimes.size();
	    	float oneSlotWeight = slotSetWeight / slotCount;
	    	
//	    	if (view == null) {
	        	LinearLayout layout = new LinearLayout(mContext);
	        layout.setOrientation(LinearLayout.HORIZONTAL);
	        TextView clockText = new TextView(mContext);
	        String am_pm = "";
	        if(position % 2 == 1) {
	        		if(position > 23) {
	        			am_pm = "pm";
	        		} else {
	        			am_pm = "am";
	        		}
	        }
	        
			clockText.setText(dailyTimeStrings[position] + am_pm);
			clockText.setLayoutParams(new LinearLayout.LayoutParams(
	                    0, LayoutParams.MATCH_PARENT, clockTextWeight));
	        layout.addView(clockText);
	        
	        ArrayList<BusyTime> curBusyList;
	        for (int i = 0; i < slotCount; i++) {
			    holder.textView = new TextView(mContext);
			    holder.textView.setLayoutParams(new LinearLayout.LayoutParams(
	                    0, LayoutParams.MATCH_PARENT, oneSlotWeight));
				
			    curBusyList = userBusyTimes.get(i);
		        boolean timeOverlapFound = false;
			    for(int j = 0; j < curBusyList.size() && !timeOverlapFound; j++) {
	        			BusyTime curBusyTime = curBusyList.get(j);
	    				if(curBusyTime.getStart_time() < dailyTimeIntervals[position] && 
	        					curBusyTime.getStop_time() > dailyTimeIntervals[position]) {
	        				timeOverlapFound = true;
	        			}	
			    }
		        if(timeOverlapFound) {
		    			holder.textView.setBackgroundDrawable(back_checked);		//TODO: CHANGE DEPRECATED METHODS?       			
		    		} else {
		    			holder.textView.setBackgroundDrawable(back_unchecked);
		    		}
				layout.addView(holder.textView);
	        }
            view = layout;	            
//	    }
	    	holder = (ViewHolder) view.getTag();
	    	return view;
	        
    }

	@Override
	public int getCount() {
		return 48;
	}
	
	private class ViewHolder {
		TextView textView;
	}
    
}