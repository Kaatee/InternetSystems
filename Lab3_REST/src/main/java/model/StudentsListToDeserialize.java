package model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class StudentsListToDeserialize {
    private Student[] students;


    public StudentsListToDeserialize(){}

    public StudentsListToDeserialize(Student[] students){
        this.students = students;
    }

    public Student[] getStudents() {
        return students;
    }

    public void setStudents(Student[] students) {
        this.students = students;
    }
}
