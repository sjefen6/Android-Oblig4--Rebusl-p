package no.oxycoon.android.rebus;

import android.location.Location;

//TODO: Make use of class to draw opponent's locations. Currently unimplemented class
public class UserData {
	private double lat, lng;
	
	/**
	 * @param lat
	 * @param lng
	 */
	public UserData(double lat, double lng){
		this.lat = lat;
		this.lng = lng;
	}
	
	/**
	 * @param location
	 */
	public UserData(Location location){
		this.lat = location.getLatitude();
		this.lng = location.getLongitude();
	}
	
	/**
	 * @return
	 */
	public double Latitude(){
		return lat;
	}
	
	/**
	 * @return
	 */
	public double Longitude(){
		return lng;
	}
	
	/**
	 * @param lat
	 */
	public void setLatitude(double lat){
		this.lat = lat;
	}
	
	/**
	 * @param lng
	 */
	public void setLongitude(double lng){
		this.lng = lng;
	}

}
