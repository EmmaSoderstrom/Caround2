package com.emmasoderstrom.caround2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by User on 2017-03-07.
 */
public class Person {

    String personId;
    String userPic;
    Integer pic = R.drawable.ball;
    String firstName;
    String lastName;
    String fullName;
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

    /*Uri userPicURI;
    Bitmap userBitmap;*/


    public Person(){

    }

    public Person(String startPersonId, String startUserPic, String startFirstName, String startLastName,
                  String startPhoneNumber, int startChosenDistance,
                  ArrayList<String> friendRequestsId, ArrayList<String> friendSendedRequests, ArrayList<String> friendAllowed){

        personId = startPersonId;
        userPic = startUserPic;


        //Uri myUri = Uri.parse(startUserPic);
        /*String userPicS = startUserPic.toString();
        Bitmap userPicBitmap = doInBackground(userPicS);
        pic = userPicBitmap;*/

        firstName = startFirstName;
        lastName = startLastName;
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

    public String getPersonId(){
        return personId;
    }

    public Integer getPic(){
        return pic;
    }

    public String getUserPic() {
        return userPic;
    }

    public void setUserPic(String userPic) {
        this.userPic = userPic;
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


       /* public Bitmap doInBackground(String... URL) {

            //String image = userPic.toString();
            String imageURL = URL[0];

            Bitmap bitmap = null;
            try {
                // Download Image from URL
                InputStream input = new java.net.URL(imageURL).openStream();
                // Decode Bitmap
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            userBitmap = bitmap;
            return bitmap;
        }*/

}
