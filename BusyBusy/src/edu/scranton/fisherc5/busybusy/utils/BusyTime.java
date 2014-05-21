package edu.scranton.fisherc5.busybusy.utils;

public class BusyTime {
	private long start_time;
	private long stop_time;
	private long user_id;
	private String activity_name;
	private String location;
	public BusyTime() {
		
	}
	
	public BusyTime(long start, long stop) {
		this.start_time = start;
		this.stop_time = stop;
	}
	
	public long getStart_time() {
		return start_time;
	}
	public void setStart_time(long start_time) {
		this.start_time = start_time;
	}
	public long getStop_time() {
		return stop_time;
	}
	public void setStop_time(long stop_time) {
		this.stop_time = stop_time;
	}
	public long getUser_id() {
		return user_id;
	}
	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}
	public String getActivity_name() {
		return activity_name;
	}
	public void setActivity_name(String activity_name) {
		this.activity_name = activity_name;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	
}
