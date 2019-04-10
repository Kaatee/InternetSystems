package server;

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
    @Produces("application/json")
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
    @Produces("application/json")
    public Response getCourseById(@PathParam("courseId") String courseId){
        int courseIdInt = Integer.parseInt(courseId);
        String result = "";
        try {
            deserializer = Deserializer.getInstance(Main.PATH);
            model = deserializer.getModel();
            for(Course course: model.getCourses()){
                if(course.getId()==courseIdInt){
                    result = deserializer.getMapper().writeValueAsString(course);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(result.equals("")){
            Response response = Response.status(404).type("text/plain").entity("No results fount").build();
            return response;
        }

        Response response = Response.status(200).entity(result).build();
        return response;
    }
}
