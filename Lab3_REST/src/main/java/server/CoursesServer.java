package server;

import app.Constants;
import app.Deserializer;
import app.Main;
import model.Course;
import model.Model;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;

@Path("/courses")
public class CoursesServer {
    private Deserializer deserializer;
    private Model model;

    @GET
    @Produces({Constants.APPLICATION_JSON, Constants.APPLICATION_XML})
    public Response getCoursesList() {
        Response response = null;
        try {
            deserializer = Deserializer.getInstance(Main.PATH);
            model = deserializer.getModel();

            Course[] courses = new Course[model.getCourses().size()];
            int i = 0;
            for (Object o : model.getCourses().values()) {
                courses[i] = (Course) o;
                i++;
            }
            response = Response.status(200).entity(courses).build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }


    @GET
    @Path("/{courseId}")
    @Produces({Constants.APPLICATION_JSON, Constants.APPLICATION_XML})
    public Response getCourseById(@PathParam("courseId") String courseId) {
        Response response = null;
        try {
            deserializer = Deserializer.getInstance(Main.PATH);
            model = deserializer.getModel();
            if (model.getCourses().containsKey(Integer.parseInt(courseId))) {
                Course course = (Course) model.getCourses().get(Integer.parseInt(courseId));
                response = Response.status(200).entity(course).build();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (response == null) {
            response = Response.status(404).type(Constants.PLAIN_TEXT).entity(Constants.RESULT_NOT_FOUND).build();
            return response;
        }
        return response;
    }


    @DELETE
    @Path("/{courseId}") //delete course with all course's grades
    public Response deleteCourse(@PathParam("courseId") String courseId) {
        Response response;
        try {
            deserializer = Deserializer.getInstance(Main.PATH);
            model = deserializer.getModel();
            if (model.getCourses().containsKey(Integer.parseInt(courseId))) {
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
    @Consumes({Constants.APPLICATION_JSON, Constants.APPLICATION_XML})
    public Response addCourse(Course course, @Context UriInfo uriInfo) {
        Response response = null;
        try {
            deserializer = Deserializer.getInstance(Main.PATH);
            model = deserializer.getModel();
            if (course == null) {
                response = Response.status(404).type("You cannot add empty course").entity(Constants.RESULT_NOT_FOUND).build();
            } else {
                course.setId(model.getCourses().size() + 1);
                model.getCourses().put(course.getId(), course);

                UriBuilder builder = uriInfo.getAbsolutePathBuilder();
                builder.path(Integer.toString(course.getId()));
                response = Response.created(builder.build()).status(201).type(Constants.PLAIN_TEXT).entity(Constants.ADDED_SUCCESSFULY).build();
            }
        } catch (Exception e) {}

        return response;
    }


    @PUT
    @Consumes(Constants.APPLICATION_JSON)
    @Produces(Constants.APPLICATION_JSON)
    @Path("/{courseID}")
    public Response editCourse(Course course, @PathParam("courseID") String courseID) {
        Response response = null;
        try {
            deserializer = Deserializer.getInstance(Main.PATH);
            model = deserializer.getModel();

            deserializer = Deserializer.getInstance(Main.PATH);
            model = deserializer.getModel();
            if (!model.getCourses().containsKey(Integer.parseInt(courseID))) {
                response = Response.status(404).type(Constants.PLAIN_TEXT).entity("Course with given id doesn't esists").build();
            } else {
                if (!course.getName().isEmpty())
                    ((Course) model.getCourses().get(Integer.parseInt(courseID))).setName(course.getName());

                if (!course.getTeacherName().isEmpty())
                    ((Course) model.getCourses().get(Integer.parseInt(courseID))).setTeacherName(course.getTeacherName());

                response = Response.status(201).type(Constants.PLAIN_TEXT).entity(Constants.EDITED_SUCCESSFULY).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}
