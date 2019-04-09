package model;

import java.util.ArrayList;

public class Model { //singleton

    private ArrayList<Student> studentsList;
    private ArrayList<Course> coursesList;
    private static volatile Model instance = null;

    private Model(){}

    public static Model getInstance() {
        if (instance == null) {
            synchronized(Model.class) {
                if (instance == null) {
                    instance = new Model();
                }
            }
        }
        return instance;
    }






}
