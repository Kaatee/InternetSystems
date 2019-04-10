package server;

import app.Constants;
import app.Deserializer;
import app.Main;
import model.Course;
import model.Model;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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
}
