package model;

import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;
import server.CoursesServer;
import server.GradesServer;
import server.StudentsServer;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Course {
    @XmlElement
    private int id;

    @XmlElement
    private String name;

    @XmlElement
    private String teacherName;

    @InjectLinks({
            @InjectLink(resource = CoursesServer.class, rel = "parent"),
            @InjectLink(resource = CoursesServer.class, rel = "self",bindings = @Binding(name = "courseId",
                    value = "${instance.id}"), method = "getCourseById")
    })
    @XmlElement(name="link")
    @XmlElementWrapper(name = "links")
    @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
    List<Link> links;

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
