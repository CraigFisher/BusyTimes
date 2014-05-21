package edu.scranton.fisherc5.busybusy;

import java.util.ArrayList;

import edu.scranton.fisherc5.busybusy.db.daos.ActivityDao;
import edu.scranton.fisherc5.busybusy.db.schema.DatabaseCreator;
import edu.scranton.fisherc5.busybusy.utils.AdminData;
import edu.scranton.fisherc5.busybusy.utils.UserActivity;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class UpdateUserActivityFragment extends Fragment implements OnClickListener {

	  private Activity parentActivity = null;
	  private ActivityDao activityDao = null;
	  
	  EditText nameView = null;
	  EditText locationView = null;
	  Button saveButton = null;
    
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
		View view = inflater.inflate(R.layout.update_activity_fragment,
			container, false);
		
		nameView = (EditText) view.findViewById(R.id.name_view);
		locationView = (EditText) view.findViewById(R.id.location_view);
		saveButton = (Button) view.findViewById(R.id.save_activity);
		
		saveButton.setOnClickListener(this);
		
		activityDao = new ActivityDao(DatabaseCreator.instance(parentActivity).getWritableDatabase());
		
		return view;
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.save_activity) {
			String newName = nameView.getText().toString();
			String newLoc = locationView.getText().toString();
			
			UserActivity newActivity = activityDao.create(AdminData.ID, newName, newLoc);
			if(newActivity != null) {
				Toast.makeText(parentActivity, "\"" + newActivity.getName() + "\"" + " for location " + "\"" + newActivity.getLocation() + "\"" + " created!", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(parentActivity, "You already have: " + newName + " for location: " + newLoc + "!", Toast.LENGTH_LONG).show();
			}
		}
		
	}	
	
}
