package model;

import model.Grade;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

@XmlRootElement
public class Student {

    @XmlElement(name="surname")
    private int index; //uniq

    private String name;
    private String surname;
    private String birthdate; ///TODO change to Date
    private Grade[] gradesList;

    public Student(){}

    public Student(int index, String name, String surname, String birthdate, Grade[] gradesList){
        this.index = index;
        this.name = name;
        this.surname = surname;
        this.birthdate = birthdate;
        this.gradesList = gradesList;
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

    public Grade[] getGradesList() {
        return gradesList;
    }

    public void setGradesList(Grade[] gradesList) {
        this.gradesList = gradesList;
    }
}

