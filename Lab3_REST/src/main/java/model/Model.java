package model;

import java.util.ArrayList;

public class Model { //singleton

    private ArrayList<Student> students;
    private ArrayList<Course> courses;
    private ArrayList<Grade> grades;
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


    public ArrayList<Student> getStudents() {
        return students;
    }

    public void setStudents(ArrayList<Student> students) {
        this.students = students;
    }

    public ArrayList<Course> getCourses() {
        return courses;
    }

    public void setCourses(ArrayList<Course> courses) {
        this.courses = courses;
    }

    public ArrayList<Grade> getGrades() {
        return grades;
    }

    public void setGrades(ArrayList<Grade> grades) {
        this.grades = grades;
    }
}
