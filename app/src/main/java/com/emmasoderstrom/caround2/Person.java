package com.emmasoderstrom.caround2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;

/**
 * Created by User on 2017-03-07.
 */
public class Person {

    String picString;
    String personId;
    String firstName;
    String lastName;
    String fullName;
    String country;
    String phoneNumber;
    int chosenDistance;
    Location location;
    double locationLatitude;
    double locationLongitude;
    int distansBetween;

    ArrayList<Person> friendRequests = new ArrayList<Person>();
    ArrayList<String> friendRequestsId = new ArrayList<String>();
    ArrayList<String> friendSendedRequests = new ArrayList<String>();
    ArrayList<String> friendAllowed = new ArrayList<String>();

    public Person(){

    }

    public Person(String picString, String personId, String startFirstName, String startLastName,
                  String country, String startPhoneNumber, int startChosenDistance,
                  ArrayList<String> friendRequestsId, ArrayList<String> friendSendedRequests, ArrayList<String> friendAllowed){

        this.picString = picString;
        this.personId = personId;

        firstName = startFirstName;
        lastName = startLastName;
        this.country = country;
        phoneNumber = startPhoneNumber;
        chosenDistance = startChosenDistance;

        this.friendRequestsId = friendRequestsId;
        this.friendSendedRequests = friendSendedRequests;
        this.friendAllowed = friendAllowed;

    }

    public Person(String startPersonId, String startFirstName, String startLastName,  String startPhoneNumber, int startChosenDistans,
                  double startLocationLatitude, double startLocationLongitude){

        personId = startPersonId;
        firstName = startFirstName;
        lastName = startLastName;
        phoneNumber = startPhoneNumber;
        chosenDistance = startChosenDistans;
        locationLatitude = startLocationLatitude;
        locationLongitude = startLocationLongitude;

    }

    public String getPicString() {
        return picString;
    }

    public String getPersonId(){
        return personId;
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

    public String getCountry() {
        return country;
    }

    public String getPhoneNumber() {
        return phoneNumber;
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

    public ArrayList<Person> getFriendRequests() {
        return friendRequests;
    }

    public void setFriendRequests(ArrayList<Person> friendRequests) {
        this.friendRequests = friendRequests;
    }

    public ArrayList<String> getFriendRequestsId() {
        return friendRequestsId;
    }

    public void addFriendRequests(String reqFriendId){
        friendRequestsId.add(reqFriendId);
    }

    public ArrayList<String> getFriendSendedRequests() {
        return friendSendedRequests;
    }

    public void addFriendSendedRequests(String sendReqFriendId){
        friendSendedRequests.add(sendReqFriendId);
    }

    public ArrayList<String> getFriendAllowed() {
        return friendAllowed;
    }

    public void setFriendAllowed(ArrayList<String> friendAllowed) {
        this.friendAllowed = friendAllowed;
    }


}
