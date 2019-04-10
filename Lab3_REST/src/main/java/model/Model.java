package model;

import java.util.ArrayList;
import java.util.HashMap;

public class Model { //singleton
    HashMap<Integer, Student> students = new HashMap<>();
    HashMap<Integer, Course> courses = new HashMap<>();
    HashMap<Integer, Grade> grades = new HashMap<>();
    private static volatile Model instance = null;

    //private ArrayList<Student> students;
    //private ArrayList<Course> courses;
    //private ArrayList<Grade> grades;


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


    public HashMap getStudents() {
        return students;
    }

    public void setStudents(HashMap students) {
        this.students = students;
    }

    public HashMap getCourses() {
        return courses;
    }

    public void setCourses(HashMap courses) {
        this.courses = courses;
    }

    public HashMap getGrades() { return grades; }

    public void setGrades(HashMap grades) { this.grades = grades; }
}
