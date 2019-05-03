package model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.bson.types.ObjectId;
import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import server.StudentsServer;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.*;

@XmlRootElement(name="student")
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
public class Student {

    @Id
    //@XmlJavaTypeAdapter(ObjectIdJaxbAdapter.class)
    @XmlTransient
    private
    ObjectId _id;

    @XmlElement
    private int index; //uniq

    @XmlElement
    private String name;

    @XmlElement
    private String surname;

    @XmlElement
    @JsonFormat(shape=JsonFormat.Shape.STRING,pattern="yyyy-MM-dd", timezone="CET")
    private Date birthdate;

    @Embedded
    //@XmlTransient
    @XmlElementWrapper(name="grades")
    @XmlElement(name="grade")
    private Grade[] gradesList;


    @InjectLinks({
            @InjectLink(resource = StudentsServer.class, rel = "self",bindings = @Binding(name = "indexNumber",
                    value = "${instance.index}"), method = "getStudentByIndex"),
            @InjectLink(rel = "parent",method = "findStudents", resource = StudentsServer.class),
            @InjectLink( rel = "grades",method = "findStudentGrades", resource = StudentsServer.class,
                    bindings = @Binding(name = "indexNumber", value = "${instance.index}"))
    })
    @XmlElement(name="link")
    @XmlElementWrapper(name = "links")
    @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
    List<Link> links;

    public Student(){
    }

    public Student(int index, String name, String surname, Date birthdate, Grade[] gradesList){
        this.index = index;
        this.name = name;
        this.surname = surname;
        this.birthdate = birthdate;
        for(Grade g: gradesList){
            g.setStudentId(index);
        }
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

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public Grade[] getGradesList() {
        return gradesList;
    }

    public void setGradesList(Grade[] gradesList) {
        if(gradesList!=null)
        for(Grade g: gradesList){
            g.setStudentId(this.index);
        }
        this.gradesList = gradesList;
    }

    public void setGradesList(Grade[] gradesList, int studentIndex) {
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


    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId id) {
        this._id = id;
    }
}

