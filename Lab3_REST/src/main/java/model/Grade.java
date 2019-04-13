package model;

import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;
import server.GradesServer;
import server.StudentsServer;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Grade {
    @XmlElement
    private int id;

    @XmlElement
    private float value; //2.0-5.0 step = 0.5 (can be 2.5)

    @XmlElement
    private String date; ///TODO change to date

    @XmlElement
    private Course course;

    @InjectLinks({
            @InjectLink(resource = GradesServer.class, rel = "parent"),
            //@InjectLink(resource = GradesServer.class, rel = "parent")
            //@InjectLink( rel = "self",method = "getStudentGradeById", resource = StudentsServer.class),//
                  //  bindings = @Binding(name = "gradeId", value = "${instance.id}"))
    })
    @XmlElement(name="link")
    @XmlElementWrapper(name = "links")
    @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
    List<Link> links;

    public Grade(){}

    public Grade(int id, float value, String date, Course course){
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}
