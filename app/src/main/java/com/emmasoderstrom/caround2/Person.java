package com.emmasoderstrom.caround2;

import android.location.Location;

import java.util.ArrayList;

/**
 * Created by User on 2017-03-07.
 */
public class Person {

    String personId;
    Integer pic = R.drawable.ball;
    String firstName;
    String lastName;
    String fullName;
    int chosenDistance;
    Location location;
    double locationLatitude;
    double locationLongitude;
    int distansBetween;

    ArrayList<String> friendRequests = new ArrayList<String>();
    ArrayList<String> friendAllowed = new ArrayList<String>();


    public Person(){

    }

    public Person(String startPersonId, String startFirstName, String startLastName, int startChosenDistance){

        personId = startPersonId;
        firstName = startFirstName;
        lastName = startLastName;
        chosenDistance = startChosenDistance;

    }

    public Person(String startPersonId, String startFirstName, String startLastName, int startChosenDistans, boolean startSignedIn,
                  double startLocationLatitude, double startLocationLongitude){

        personId = startPersonId;
        firstName = startFirstName;
        lastName = startLastName;
        chosenDistance = startChosenDistans;
        locationLatitude = startLocationLatitude;
        locationLongitude = startLocationLongitude;

    }

    public String getPersonId(){
        return personId;
    }

    public Integer getPic(){
        return pic;
    }

    public String getFirstName(){
        return firstName;
    }

    public String getLastName(){
        return lastName;
    }

    public String getFullName(){
        fullName = firstName + " " + lastName;
        return fullName;
    }


    /**public String getChosenDistans(){
        return String.valueOf (chosenDistance);
    }**/
    /*public void setChosenDistans(int changeDistans){
        chosenDistance = changeDistans;
    }*/

    public int getChosenDistansInt(){
        return (chosenDistance);
    }

    public void setChosenDistansInt(int changeDistans){
        chosenDistance = changeDistans;
    }

    public Location getLocation(){
        return location;
    }

    public void setLocation(Location location){
        location = location;
    }

    public double getLocationLatitude(){
        return locationLatitude;
    }

    public void setLocationLatitude(double latitude){
        locationLatitude = latitude;
    }

    public double getLocationLongitude(){
        return locationLongitude;
    }

    public void setLocationLongitude(double longitude){
        locationLongitude = longitude;
    }


    public int getDistansBetween(){
        return distansBetween;
    }

    public void setDistansBetween(int distans){
        distansBetween = distans;
    }

    public ArrayList<String> getFriendRequests() {
        return friendRequests;
    }

    public void setFriendRequests(ArrayList<String> friendRequests) {
        this.friendRequests = friendRequests;
    }

    public ArrayList<String> getFriendAllowed() {
        return friendAllowed;
    }

    public void setFriendAllowed(ArrayList<String> friendAllowed) {
        this.friendAllowed = friendAllowed;
    }
}
