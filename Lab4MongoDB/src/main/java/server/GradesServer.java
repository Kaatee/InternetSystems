package server;

import Mongo.DatabaseHandler;
import app.Constants;
import model.Grade;
import model.Student;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Path("/grades")
public class GradesServer {
    private Datastore datastore;

    @GET
    @Path("/{gradeIdx}")
    @Produces({Constants.APPLICATION_JSON, Constants.APPLICATION_XML})
    public Response getSingleGrade(@PathParam("gradeIdx") int gradeIdx) {
        datastore = DatabaseHandler.getInstance().getDatastore();

        Query<Student> studentsQuery = datastore.find(Student.class);
        List<Student> studentsList = studentsQuery.asList();

        for (Student s : studentsList) {
            ArrayList<Grade> grades = new ArrayList<>(Arrays.asList(s.getGradesList()));
            for (Grade g : grades) {
                if (g.getId() == gradeIdx) {
                    return Response.status(200).entity(g).build();
                }
            }
        }
        return Response.status(404).type(Constants.PLAIN_TEXT).entity(Constants.RESULT_NOT_FOUND).build();
    }

}
