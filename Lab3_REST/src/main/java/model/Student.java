package model;

import model.Grade;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Date;

@XmlRootElement
public class Student {
    private int index; //uniq
    private String name;
    private String surname;
    private String birthdate; ///TODO change to Date
    private ArrayList<Grade> gradesList;

    public Student(){}

    public Student(int index, String name, String surname, String birthdate, ArrayList<Grade> gradesList){
        this.index = index;
        this.name = name;
        this.surname = surname;
        this.birthdate = birthdate;
        this.gradesList = new ArrayList<>(gradesList);
    }


    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public ArrayList getGradesList() {
        return gradesList;
    }

    public void setGradesList(ArrayList gradesList) {
        this.gradesList = gradesList;
    }
}

