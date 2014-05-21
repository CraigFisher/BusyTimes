package edu.scranton.fisherc5.busybusy.utils;

public class UserActivity {
	private long user_id;
	private String name;
	private String location;
	public long getUser_id() {
		return user_id;
	}
	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	
	@Override
	public boolean equals(Object obj) {
		UserActivity that = (UserActivity) obj;
		return this.user_id == that.user_id &&
			   this.name == that.name &&
			   this.location == that.location; 
	}
	

}
