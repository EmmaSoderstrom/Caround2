package com.emmasoderstrom.caround2;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by User on 2017-03-02.
 */

public class PersonList extends ArrayAdapter<ListContiner> {

    ArrayList<Person> allPersonArrayList = new ArrayList<Person>();
    //ArrayList<Person> personArrayList = new ArrayList<Person>();
    ArrayList<Person> closePersonArrayList = new ArrayList<Person>();
    ArrayList<Integer> personPicArrayList = new ArrayList<Integer>();
    ArrayList<String> personFullNameArrayList = new ArrayList<String>();
    ArrayList<String> personFirstNameArrayList = new ArrayList<String>();
    ArrayList<String> personLastNameArrayList = new ArrayList<String>();
    ArrayList<String> personChosenDistansArrayList = new ArrayList<>();

    ArrayList<Integer> picArrayList = new ArrayList<Integer>();
    ArrayList<String> nameArrayList = new ArrayList<String>();

    Integer picArray[] = {
            R.drawable.ball,
            R.drawable.ball,
            R.drawable.ball,
            R.drawable.ball,
            R.drawable.ball,
            R.drawable.ball,
            R.drawable.ball,
            R.drawable.ball,
            R.drawable.ball,
            R.drawable.ball,
            R.drawable.ball,
            R.drawable.ball,
    };

    String nameArray[] = {
            "Jonas"+" "+"Amnesten",
            "Emma"+" "+"Södetersöm",
            "Lisa"+" "+"Svensson",
            "Sara"+" "+"Johansson",
            "Petra"+" "+"Eriksson",
            "Nils"+" "+"Hansson",
            "Patrik"+" "+"Pertersson",
            "Joel"+" "+"Joelsson",
            "Petra"+" "+"Eriksson",
            "Nils"+" "+"Hansson",
            "Patrik"+" "+"Pertersson",
            "Joel"+" "+"Joelsson"
    };

    String distansArray[] = {
            "2"+" "+"km",
            "2,6"+" "+"m",
            "1,8"+" "+"km",
            "8"+" "+"km",
            "5"+" "+"km",
            "200"+" "+"m",
            "212"+" "+"m",
            "2,1"+" "+"mil",
            "5"+" "+"km",
            "200"+" "+"m",
            "212"+" "+"m",
            "2,1"+" "+"mil"
    };

    public PersonList(Context context, int resource){
        super(context, resource);
    }

    public ArrayList picArrayToList(){
        for (int i = 0; i < picArray.length; i++) {
            picArrayList.add(picArray[i]);
        }
        return picArrayList;
    }

    public ArrayList nameArrayToList(){
        for (int i = 0; i < nameArray.length; i++) {
            nameArrayList.add(nameArray[i]);
        }
        return nameArrayList;
    }

    public void sortPersonArrayList() {
        Collections.sort(closePersonArrayList, new Comparator<Person>() {
            @Override
            public int compare(Person p1, Person p2) {
                return ((Integer)p1.getDistansBetween()).compareTo(p2.getDistansBetween());
            }
        });
    }

    public Person[] personClosePersonArrayListToArray(){
        Person[] personClosePersonArray = closePersonArrayList.toArray(new Person[closePersonArrayList.size()]);
        return personClosePersonArray;
    }

    public Integer[] personPicArrayListToArray(){
        for (int i = 0; i < closePersonArrayList.size(); i++) {
            personPicArrayList.add(closePersonArrayList.get(i).getPic());
        }
        Integer[] personPicArray = personPicArrayList.toArray(new Integer[personPicArrayList.size()]);

        return personPicArray;
    }

    public String[] personFullNameArrayListToArray(){
        for (int i = 0; i < closePersonArrayList.size(); i++) {
            personFullNameArrayList.add(closePersonArrayList.get(i).getFirstName() + " " + closePersonArrayList.get(i).getLastName());
            //personLastNameArrayList.add(personArrayList.get(i).getFirstName());
        }
        String[] personFullNameArray = personFullNameArrayList.toArray(new String[personFullNameArrayList.size()]);
        //String[] personLastNameArray = personLastNameArrayList.toArray(new String[personLastNameArrayList.size()]);

        return personFullNameArray;
    }

    public String[] personFirstNameArrayListToArray(){
        for (int i = 0; i < closePersonArrayList.size(); i++) {
            personFirstNameArrayList.add(closePersonArrayList.get(i).getFirstName());
        }
        String[] personFirstNameArray = personFirstNameArrayList.toArray(new String[personFirstNameArrayList.size()]);

        return personFirstNameArray;
    }

    public String[] personLastNameArrayListToArray(){
        for (int i = 0; i < closePersonArrayList.size(); i++) {
            personLastNameArrayList.add(closePersonArrayList.get(i).getLastName());
        }
        String[] personLastNameArray = personLastNameArrayList.toArray(new String[personLastNameArrayList.size()]);

        return personLastNameArray;
    }

    public String[] personChosenDistansArrayListToArray(){
        for (int i = 0; i < closePersonArrayList.size(); i++) {
            personChosenDistansArrayList.add(closePersonArrayList.get(i).getChosenDistans().toString());
        }
        String[] personChosenDistansArray = personChosenDistansArrayList.toArray(new String[personChosenDistansArrayList.size()]);

        return personChosenDistansArray;
    }

}
