package com.emmasoderstrom.caround2;

import android.location.Location;

/**
 * Created by User on 2017-03-07.
 */
public class Person {

    Integer pic = R.drawable.ball;
    String firstName;
    String lastName;
    String fullName;
    int chosenDistance;
    Location location;
    double locationLatitude;
    double locationLongitude;
    int distansBetween;

    public Person(String startFirstName, String startLastName, int startChosenDistance){

        firstName = startFirstName;
        lastName = startLastName;
        chosenDistance = startChosenDistance;

    }

    public Person(String startFirstName, String startLastName, int startChosenDistans,
                  double startLocationLatitude, double startLocationLongitude){

        firstName = startFirstName;
        lastName = startLastName;
        chosenDistance = startChosenDistans;
        locationLatitude = startLocationLatitude;
        locationLongitude = startLocationLongitude;

    }

    public Integer getPic(){
        return pic;
    }

    public String getFirstName(){
        return firstName;
    }

    public String getFullName(){
        fullName = firstName + " " + lastName;
        return fullName;
    }

    public String getLastName(){
        return lastName;
    }

    public String getChosenDistans(){
        return String.valueOf (chosenDistance);
    }

    public int getChosenDistansInt(){
        return (chosenDistance);
    }

    public void setChosenDistans(int changeDistans){
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

}
