package server;

import app.Deserializer;
import app.Main;
import model.Grade;
import model.Model;
import model.Student;
import model.StudentsListToDeserialize;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.HashMap;

@Path("/students")
public class StudentsServer {
    private Deserializer deserializer;
    private Model model;

    @GET
    @Produces("application/json")
    public String getStudentsList() {
        String result = "";
        try {
            deserializer = Deserializer.getInstance(Main.PATH);
            model = deserializer.getModel();
            result = deserializer.getMapper().writeValueAsString(model.getStudents());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @GET
    @Path("/{indexNumber}")
    @Produces("application/json")
    public Response getStudentByIndex(@PathParam("indexNumber") String indexNumber) {
        String result = "";
        int indexNumberInt = Integer.parseInt(indexNumber);
        try {
            deserializer = Deserializer.getInstance(Main.PATH);
            model = deserializer.getModel();

            StudentsListToDeserialize stResult = new StudentsListToDeserialize();
            Student[] students = new Student[1];
            HashMap<Integer, Student> studentsList = model.getStudents();
            Student student = studentsList.get(indexNumberInt);

            students[0] = student;
            stResult.setStudents(students);
            result = deserializer.getMapper().writeValueAsString(stResult);

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


    @GET
    @Path("/{indexNumber}/grades/{gradeId}")
    @Produces("application/json")
    public Response getStudentGradeById(@PathParam("indexNumber") String indexNumber, @PathParam("gradeId") String gradeId) {
        String result = "";
        try {
            deserializer = Deserializer.getInstance(Main.PATH);
            model = deserializer.getModel();

            HashMap<Integer, Student> studentsList = model.getStudents();
            Student student = studentsList.get(Integer.parseInt(indexNumber));

            for(Grade g: student.getGradesList()) {
                if(g.getId()==Integer.parseInt(gradeId)) {
                    result = deserializer.getMapper().writeValueAsString(g);
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


    @GET
    @Path("/{indexNumber}/grades")
    @Produces("application/json")
    public Response getStudentGrades(@PathParam("indexNumber") String indexNumber) {
        String result = "";
        try {
            deserializer = Deserializer.getInstance(Main.PATH);
            model = deserializer.getModel();

            Student student = (Student) model.getStudents().get(Integer.parseInt(indexNumber));
            Grade[] grades = student.getGradesList();
            result = deserializer.getMapper().writeValueAsString(grades);
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
