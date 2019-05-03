package server;

import Mongo.CourseDAO;
import Mongo.DatabaseHandler;
import app.Constants;
import model.Course;
import model.Grade;
import model.Student;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;

@Path("/courses")
public class CoursesServer {
    private Datastore datastore;
    private CourseDAO courseDAO = CourseDAO.getInstance();

    @GET
    @Produces({Constants.APPLICATION_JSON, Constants.APPLICATION_XML})
    public Response getCoursesList(@QueryParam("name") String name,
                                   @QueryParam("teacherName") String teacherName) {
        datastore = DatabaseHandler.getInstance().getDatastore();

        List<Course> coursesList = courseDAO.findCourses(name, teacherName);
        Course[] students = coursesList.toArray(new Course[coursesList.size()]);
        return Response.status(200).entity(students).build();
    }


    @GET
    @Path("/{courseId}")
    @Produces({Constants.APPLICATION_JSON, Constants.APPLICATION_XML})
    public Response getCourseById(@PathParam("courseId") String courseId) {
        ObjectId id;
        try {
            id = new ObjectId(courseId);
        } catch (Exception e) {
            return Response.status(400).type(Constants.PLAIN_TEXT).entity("Invalid object id").build();
        }

        datastore = DatabaseHandler.getInstance().getDatastore();
        Course course = datastore.find(Course.class, "id", id).get();

        if (course == null) {
            return Response.status(404).type(Constants.PLAIN_TEXT).entity(Constants.RESULT_NOT_FOUND).build();
        } else
            return Response.status(200).entity(course).build();
    }


    @DELETE
    @Path("/{courseId}") //delete course with all course's grades
    public Response deleteCourse(@PathParam("courseId") ObjectId courseId) {
        datastore = DatabaseHandler.getInstance().getDatastore();
        Course course = datastore.find(Course.class, "id", courseId).get();

        if (course != null) {
            //delete all grades with this course
            Query<Student> studentsQuery = datastore.find(Student.class);
            List<Student> studentsList = studentsQuery.asList();
            for (Student st : studentsList) {
                Grade[] oldGrades = st.getGradesList();
                List<Grade> newGrades = new ArrayList<>();
                if(oldGrades!=null) {
                    for (Grade g : oldGrades) {
                        if (!g.getCourse().getId().equals(courseId)) {
                            newGrades.add(g);
                        }
                    }
                    Grade[] grades = newGrades.toArray(new Grade[newGrades.size()]);
                    st.setGradesList(grades);
                }
                else
                    st.setGradesList(null);
                datastore.save(st);
            }

            datastore.delete(datastore.find(Course.class, "id", courseId));
            return Response.status(200).type(Constants.PLAIN_TEXT).entity(Constants.DELETED_SUCCESSFULY).build();
        }

        return Response.status(404).type(Constants.PLAIN_TEXT).entity(Constants.RESULT_NOT_FOUND).build();
    }

    @POST
    @Consumes({Constants.APPLICATION_JSON, Constants.APPLICATION_XML})
    public Response addCourse(Course course, @Context UriInfo uriInfo) {
        datastore = DatabaseHandler.getInstance().getDatastore();

        if (course == null) {
            return Response.status(404).type("You cannot add empty course").entity(Constants.RESULT_NOT_FOUND).build();
        } else {
            datastore.save(course);

            UriBuilder builder = uriInfo.getAbsolutePathBuilder();
            builder.path(course.getId().toString());
            return Response.created(builder.build()).status(201).type(Constants.PLAIN_TEXT).entity(Constants.ADDED_SUCCESSFULY).build();
        }
    }


    @PUT
    @Consumes({Constants.APPLICATION_JSON, Constants.APPLICATION_XML})
    @Produces({Constants.APPLICATION_JSON, Constants.APPLICATION_XML})
    @Path("/{courseID}")
    public Response editCourse(Course course, @PathParam("courseID") ObjectId courseID) {
        datastore = DatabaseHandler.getInstance().getDatastore();
        Course courseFromDB = datastore.find(Course.class, "id", courseID).get();

        if (courseFromDB == null) {
            return Response.status(404).type(Constants.PLAIN_TEXT).entity("Course with given id doesn't exists").build();
        } else {
            //edit student's grades
            Query<Student> studentsQuery = datastore.find(Student.class);
            List<Student> studentsList = studentsQuery.asList();
            for(Student s: studentsList){
                for(Grade g: s.getGradesList()){
                    if(g.getCourse().getId().equals(courseFromDB.getId())){
                        if (!course.getName().isEmpty())
                            g.getCourse().setName(course.getName());

                        if (!course.getTeacherName().isEmpty())
                            g.getCourse().setTeacherName(course.getTeacherName());
                    }
                }
                datastore.save(s);
            }

            //edit course
            if (!course.getName().isEmpty())
                courseFromDB.setName(course.getName());

            if (!course.getTeacherName().isEmpty())
                courseFromDB.setTeacherName(course.getTeacherName());

            datastore.save(courseFromDB);

            return Response.status(201).type(Constants.PLAIN_TEXT).entity(Constants.EDITED_SUCCESSFULY).build();
        }

    }
}
