package model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Course {
    private int id;
    private String name;
    private String teacherName;

    public Course(){}

    public Course(int id, String name, String teacherName){
        this.id = id;
        this.name = name;
        this.teacherName = teacherName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }
}
