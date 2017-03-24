package com.emmasoderstrom.caround2;

import android.location.Location;

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
    boolean signedIn;


    public Person(){

    }

    public Person(String startPersonId, String startFirstName, String startLastName, int startChosenDistance, boolean startSignedIn){

        personId = startPersonId;
        firstName = startFirstName;
        lastName = startLastName;
        chosenDistance = startChosenDistance;
        signedIn = startSignedIn;

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

    public boolean getSignedIn(){
        return signedIn;
    }
    public void setSignedIn(boolean changeSignedIn){
        signedIn = changeSignedIn;
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
