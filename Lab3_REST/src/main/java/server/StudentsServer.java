package server;

import app.Deserializer;
import app.Main;
import model.Model;
import model.StudentsListToDeserialize;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("/students")
public class StudentsServer {
    private Deserializer deserializer;
    private Model model;
    private StudentsListToDeserialize studentsList;

    @GET
    @Produces("application/json")
    public String getStudentsList() {
        String result = "";
        try {
            deserializer = Deserializer.getInstance(Main.PATH);
            studentsList = deserializer.getStudentsList();


            result = deserializer.getMapper().writeValueAsString(studentsList);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }



}
