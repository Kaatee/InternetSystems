package model;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;


@XmlRootElement
public class Grade {
    private int id;
    private float value; //2.0-5.0 step = 0.5 (can be 2.5)
    private Date date;
    private Course course;

    public Grade(){}

    public Grade(int id, float value, Date date, Course course){
        this.id = id;
        this.value = value;
        this.date = date;
        this.course = course;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}
