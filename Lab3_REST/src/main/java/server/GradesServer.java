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
    @Produces({Constants.APPLICATION_JSON, Constants.APPLICATION_XML})
    public Response getSingleGrade(@PathParam("gradeIdx") String gradeIdx) {
        Response response = null;
        try {
            deserializer = Deserializer.getInstance(Main.PATH);
            model = deserializer.getModel();
            if(model.getGrades().containsKey(Integer.parseInt(gradeIdx))) {
                Grade grade = (Grade) model.getGrades().get(Integer.parseInt(gradeIdx));
                response = Response.status(200).entity(grade).build();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(response == null){
            response = Response.status(404).type(Constants.PLAIN_TEXT).entity(Constants.RESULT_NOT_FOUND).build();
            return response;
        }
        return response;
    }

}
