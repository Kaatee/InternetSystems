package server;

import app.Constants;
import app.Deserializer;
import app.Main;
import model.Grade;
import model.Model;
import model.Student;
import model.StudentsListToDeserialize;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.HashMap;

@Path("/students")
public class StudentsServer {
    private Deserializer deserializer;
    private Model model;

    @GET
    @Produces(Constants.APPLICATION_JSON)
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
    @Produces(Constants.APPLICATION_JSON)
    public Response getStudentByIndex(@PathParam("indexNumber") String indexNumber) {
        String result = "";
        int indexNumberInt = Integer.parseInt(indexNumber);
        try {
            deserializer = Deserializer.getInstance(Main.PATH);
            model = deserializer.getModel();

            HashMap<Integer, Student> studentsList = model.getStudents();
            Student student = studentsList.get(indexNumberInt);
            result = deserializer.getMapper().writeValueAsString(student);

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (result.equals("")) {
            Response response = Response.status(404).type(Constants.PLAIN_TEXT).entity(Constants.RESULT_NOT_FOUND).build();
            return response;
        }
        Response response = Response.status(200).entity(result).build();
        return response;
    }


    @GET
    @Path("/{indexNumber}/grades/{gradeId}")
    @Produces(Constants.APPLICATION_JSON)
    public Response getStudentGradeById(@PathParam("indexNumber") String indexNumber, @PathParam("gradeId") String gradeId) {
        String result = "";
        try {
            deserializer = Deserializer.getInstance(Main.PATH);
            model = deserializer.getModel();

            HashMap<Integer, Student> studentsList = model.getStudents();
            Student student = studentsList.get(Integer.parseInt(indexNumber));

            for (Grade g : student.getGradesList()) {
                if (g.getId() == Integer.parseInt(gradeId)) {
                    result = deserializer.getMapper().writeValueAsString(g);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (result.equals("")) {
            Response response = Response.status(404).type(Constants.PLAIN_TEXT).entity(Constants.RESULT_NOT_FOUND).build();
            return response;
        }

        Response response = Response.status(200).entity(result).build();
        return response;
    }


    @GET
    @Path("/{indexNumber}/grades")
    @Produces(Constants.APPLICATION_JSON)
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

        if (result.equals("")) {
            Response response = Response.status(404).type(Constants.PLAIN_TEXT).entity(Constants.RESULT_NOT_FOUND).build();
            return response;
        }
        Response response = Response.status(200).entity(result).build();
        return response;
    }


    @DELETE
    @Path("/{indexNumber}") //delete student
    public Response deleteStudent(@PathParam("indexNumber") String indexNumber) {
        Response response;
        try {
            deserializer = Deserializer.getInstance(Main.PATH);
            model = deserializer.getModel();
            if (model.getStudents().containsKey(Integer.parseInt(indexNumber))) {
                //Delete student's grades
                Student student = (Student) model.getStudents().get(Integer.parseInt(indexNumber));
                Grade[] grades = student.getGradesList();
                for (Grade g : grades) {
                    model.getGrades().remove(g.getId());
                }

                //delete student
                model.getStudents().remove(Integer.parseInt(indexNumber));
                response = Response.status(200).type(Constants.PLAIN_TEXT).entity(Constants.DELETED_SUCCESSFULY).build();
                return response;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        response = Response.status(404).type(Constants.PLAIN_TEXT).entity(Constants.RESULT_NOT_FOUND).build();
        return response;
    }

    @DELETE
    @Path("/{indexNumber}/grades/{gradeId}") //delete student's grade
    public Response deleteStudentGrade(@PathParam("indexNumber") String indexNumber, @PathParam("gradeId") String gradeId) {
        Response response;
        try {
            deserializer = Deserializer.getInstance(Main.PATH);
            model = deserializer.getModel();
            if (model.getStudents().containsKey(Integer.parseInt(indexNumber))) {
                //Delete student's grades
                Student student = (Student) model.getStudents().get(Integer.parseInt(indexNumber));
                Grade[] grades = student.getGradesList();
                Grade[] newGrades = new Grade[grades.length];
                int i = 0;
                for (Grade g : grades) {
                    if (g.getId() == Integer.parseInt(gradeId)) {
                        model.getGrades().remove(g.getId());
                    } else {
                        newGrades[i] = g;
                        i++;
                    }
                }
                student.setGradesList(newGrades);

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
    @Produces(Constants.APPLICATION_JSON)
    public Response addStudent(Student student) {
        Response response = null;
        try {
            deserializer = Deserializer.getInstance(Main.PATH);
            model = deserializer.getModel();
            if (model.getStudents().containsKey(student.getIndex())) {
                response = Response.status(404).type("Student with given index exists").entity(Constants.RESULT_NOT_FOUND).build();
            } else {
                if (student == null) {
                    response = Response.status(404).type("You cannot add empty student").entity(Constants.RESULT_NOT_FOUND).build();
                } else {
                    model.addStudent(student);
                    response = Response.status(201).type(Constants.PLAIN_TEXT).entity(Constants.ADDED_SUCCESSFULY).build();

                }
            }
        } catch (Exception e) {
        }

        return response;
    }


    @POST
    @Consumes(Constants.APPLICATION_JSON)
    @Produces(Constants.APPLICATION_JSON)
    @Path("/{indexNumber}/grades")
    public Response addSStudentGrade(@PathParam("indexNumber") String indexNumber, Grade grade) {
        Response response = null;
        try {
            deserializer = Deserializer.getInstance(Main.PATH);
            model = deserializer.getModel();

            deserializer = Deserializer.getInstance(Main.PATH);
            model = deserializer.getModel();
            if (model.getGrades().containsKey(grade.getId())) {
                response = Response.status(404).type("Grade with given id exists").entity(Constants.RESULT_NOT_FOUND).build();
            } else {
                if (grade == null) {
                    response = Response.status(404).type("You cannot add empty grade").entity(Constants.RESULT_NOT_FOUND).build();
                } else {
                    model.getGrades().put(grade.getId(), grade);
                    model.addGradeToStudent(grade, Integer.parseInt(indexNumber));
                    if(!model.getCourses().containsKey(grade.getCourse().getId())) {
                        model.getCourses().put(grade.getCourse().getId(), grade.getCourse());
                    }
                    response = Response.status(201).type(Constants.PLAIN_TEXT).entity(Constants.ADDED_SUCCESSFULY).build();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    @PUT
    @Consumes(Constants.APPLICATION_JSON)
    @Produces(Constants.APPLICATION_JSON)
    @Path("/{indexNumber}")
    public Response editStudent(Student student, @PathParam("indexNumber") String indexNumber) {
        Response response = null;
        try {
            deserializer = Deserializer.getInstance(Main.PATH);
            model = deserializer.getModel();

            deserializer = Deserializer.getInstance(Main.PATH);
            model = deserializer.getModel();
            if (!model.getStudents().containsKey(Integer.parseInt(indexNumber))) {
                response = Response.status(404).type(Constants.PLAIN_TEXT).entity("Student with given index doesn't esists").build();
            } else {
                try {
                    if (!student.getName().isEmpty())
                        ((Student) model.getStudents().get(Integer.parseInt(indexNumber))).setName(student.getName());
                }catch (Exception e){}

                try {
                if(!student.getSurname().isEmpty())
                    ((Student) model.getStudents().get(Integer.parseInt(indexNumber))).setSurname(student.getSurname());
                }catch (Exception e){}

                try {
                if(!student.getBirthdate().isEmpty())
                    ((Student) model.getStudents().get(Integer.parseInt(indexNumber))).setBirthdate(student.getBirthdate());
                }catch (Exception e){}

                response = Response.status(201).type(Constants.PLAIN_TEXT).entity(Constants.EDITED_SUCCESSFULY).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }


    @PUT
    @Consumes(Constants.APPLICATION_JSON)
    @Produces(Constants.APPLICATION_JSON)
    @Path("/{indexNumber}/grades/{gradeID}")
    public Response editGrade(Grade grade, @PathParam("indexNumber") String indexNumber, @PathParam("gradeID") String gradeID) {
        Response response = null;
        try {
            deserializer = Deserializer.getInstance(Main.PATH);
            model = deserializer.getModel();

            deserializer = Deserializer.getInstance(Main.PATH);
            model = deserializer.getModel();
            if (!model.getStudents().containsKey(Integer.parseInt(indexNumber))) {
                response = Response.status(404).type(Constants.PLAIN_TEXT).entity("Student with given index doesn't esists").build();
            } else {
                if(grade.getValue()!=0.0)
                    ((Grade) model.getGrades().get(Integer.parseInt(gradeID))).setValue(grade.getValue());

                if(!grade.getDate().isEmpty())
                    ((Grade) model.getGrades().get(Integer.parseInt(gradeID))).setDate(grade.getDate());

               model.updateStudentGrade(Integer.parseInt(gradeID),Integer.parseInt(indexNumber), grade);
                response = Response.status(201).type(Constants.PLAIN_TEXT).entity(Constants.EDITED_SUCCESSFULY).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}
