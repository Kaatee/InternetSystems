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

@Path("/grades")
public class GradesServer {
    private Deserializer deserializer;
    private Model model;

    @GET
    @Path("/{gradeIdx}")
    @Produces(Constants.APPLICATION_JSON)
    public Response getSingleGrade(@PathParam("gradeIdx") String gradeIdx) {
        String result = "";
        try {
            deserializer = Deserializer.getInstance(Main.PATH);
            model = deserializer.getModel();
            if(model.getGrades().containsKey(Integer.parseInt(gradeIdx))) {
                Grade grade = (Grade) model.getGrades().get(Integer.parseInt(gradeIdx));
                result = deserializer.getMapper().writeValueAsString(grade);
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
