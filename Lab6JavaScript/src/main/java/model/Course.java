package model;

import org.bson.types.ObjectId;
import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import server.CoursesServer;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
public class Course {
    @Id
    @XmlJavaTypeAdapter(ObjectIdJaxbAdapter.class)
    @XmlAttribute
    private
    ObjectId id;

    @XmlElement
    private String name;

    @XmlElement
    private String teacherName;

    @InjectLinks({
            @InjectLink(resource = CoursesServer.class, rel = "parent"),
            @InjectLink(resource = CoursesServer.class, rel = "self",bindings = @Binding(name = "courseId", value = "${instance.id}"), method = "getCourseById")
    })

    @XmlElement(name="link")
    @XmlElementWrapper(name = "links")
    @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
    List<Link> links;

    public Course(){}

    public Course(String name, String teacherName){
        this.name = name;
        this.teacherName = teacherName;
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

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }
}
