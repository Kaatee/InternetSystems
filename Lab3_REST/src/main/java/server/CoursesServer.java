package server;

import app.Constants;
import app.Deserializer;
import app.Main;
import model.Course;
import model.Grade;
import model.Model;
import model.Student;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("/courses")
public class CoursesServer {
    private Deserializer deserializer;
    private Model model;

    @GET
    @Produces(Constants.APPLICATION_JSON)
    public String getCoursesList(){
        String result = "";
        try {
            deserializer = Deserializer.getInstance(Main.PATH);
            model = deserializer.getModel();
            result = deserializer.getMapper().writeValueAsString(model.getCourses());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    @GET
    @Path("/{courseId}")
    @Produces(Constants.APPLICATION_JSON)
    public Response getCourseById(@PathParam("courseId") String courseId){
        String result = "";
        try {
            deserializer = Deserializer.getInstance(Main.PATH);
            model = deserializer.getModel();
            if(model.getCourses().containsKey(Integer.parseInt(courseId))) {
                Course course = (Course) model.getCourses().get(Integer.parseInt(courseId));
                result = deserializer.getMapper().writeValueAsString(course);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        if(result.equals("")){
            Response response = Response.status(404).type(Constants.PLAIN_TEXT).entity(Constants.RESULT_NOT_FOUND).build();
            return response;
        }
        Response response = Response.status(200).entity(result).build();
        return response;
    }


    @DELETE
    @Path("/{courseId}") //delete course with all course's grades
    public Response deleteCourse(@PathParam("courseId") String courseId) {
        Response response;
        try {
            deserializer = Deserializer.getInstance(Main.PATH);
            model = deserializer.getModel();
            if(model.getCourses().containsKey(Integer.parseInt(courseId))) {
                model.deleteCourse(Integer.parseInt(courseId));
                response = Response.status(200).type(Constants.PLAIN_TEXT).entity(Constants.DELETED_SUCCESSFULY).build();
                return response;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        response = Response.status(404).type(Constants.PLAIN_TEXT).entity(Constants.RESULT_NOT_FOUND).build();
        return response;
    }

    @POST
    @Consumes(Constants.APPLICATION_JSON)
    public Response addCourse(Course course) {
        Response response = null;
        try {
            deserializer = Deserializer.getInstance(Main.PATH);
            model = deserializer.getModel();
            if (model.getCourses().containsKey(course.getId())) {
                response = Response.status(404).type("Course with given id exists").entity(Constants.RESULT_NOT_FOUND).build();
            } else {
                if (course == null) {
                    response = Response.status(404).type("You cannot add empty course").entity(Constants.RESULT_NOT_FOUND).build();
                } else {
                    model.getCourses().put(course.getId(), course);
                    response = Response.status(201).type(Constants.PLAIN_TEXT).entity(Constants.ADDED_SUCCESSFULY).build();
                }
            }
        } catch (Exception e) {
        }

        return response;
    }
}
