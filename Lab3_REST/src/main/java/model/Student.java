package model;

import model.Grade;
import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;
import org.glassfish.jersey.server.Uri;
import server.GradesServer;
import server.StudentsServer;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.*;

@XmlRootElement(name="student")
@XmlAccessorType(XmlAccessType.FIELD)
public class Student {

    @XmlElement
    private int index; //uniq

    @XmlElement
    private String name;

    @XmlElement
    private String surname;

    @XmlElement
    private String birthdate; ///TODO change to Date

    @XmlElementWrapper(name="grades")
    @XmlElement(name="grade")
    private Grade[] gradesList;


    @InjectLinks({
            @InjectLink(resource = StudentsServer.class, rel = "self",bindings = @Binding(name = "indexNumber",
                    value = "${instance.index}"), method = "getStudentByIndex"),
            @InjectLink(rel = "parent",method = "getStudentsList", resource = StudentsServer.class),
            @InjectLink( rel = "grades",method = "getStudentGrades", resource = StudentsServer.class,
                    bindings = @Binding(name = "indexNumber", value = "${instance.index}"))
    })
    @XmlElement(name="link")
    @XmlElementWrapper(name = "links")
    @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
    List<Link> links;

    public Student(){
    }

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

    public void addGrade(Grade grade){
        Grade[] newGrades = new Grade[gradesList.length + 1];
        for (int i=0 ;  i< gradesList.length; i++){
            newGrades[i] = gradesList[i];
        }
        newGrades[gradesList.length] = grade;
        this.setGradesList(newGrades);
    }
}

