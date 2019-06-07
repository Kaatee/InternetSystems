package server;

import Mongo.DatabaseHandler;
import Mongo.StudentDAO;
import app.Constants;
import model.Course;
import model.Grade;
import model.Student;
import model.StudentIndex;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Path("/students")
public class StudentsServer {
    Datastore datastore;
    private StudentDAO studentDAO = StudentDAO.getInstance();


    @GET
    @Produces({Constants.APPLICATION_JSON, Constants.APPLICATION_XML})
    public Response getStudentsList(@QueryParam("name") String name, @QueryParam("surname") String surname,
                                    @QueryParam("dateFrom") Date dateFrom, @QueryParam("dateTo") Date dateTo,
                                    @QueryParam("index") Integer index, @QueryParam("dateExacly") Date dateExacly) {

        List<Student> studentsList = studentDAO.findStudents(name, surname, dateFrom, dateTo, index, dateExacly);
        Student[] students = studentsList.toArray(new Student[studentsList.size()]);
        System.out.println("Get Students");
        return Response.status(200).entity(students).build();
    }

    @GET
    @Path("/{indexNumber}")
    @Produces({Constants.APPLICATION_JSON, Constants.APPLICATION_XML})
    public Response getStudentByIndex(@PathParam("indexNumber") int indexNumber) {
        Response response;

        datastore = DatabaseHandler.getInstance().getDatastore();
        Student student = datastore.find(Student.class, "index", indexNumber).get();

        if (student == null) {
            response = Response.status(404).type(Constants.PLAIN_TEXT).entity(Constants.RESULT_NOT_FOUND).build();
            return response;
        } else
            return Response.status(200).entity(student).build();
    }


    @GET
    @Path("/{indexNumber}/grades/{gradeId}")
    @Produces({Constants.APPLICATION_JSON, Constants.APPLICATION_XML})
    public Response getStudentGradeById(@PathParam("indexNumber") int indexNumber, @PathParam("gradeId") int gradeId) {
        datastore = DatabaseHandler.getInstance().getDatastore();
        Student student = datastore.find(Student.class, "index", indexNumber).get();

        if (student != null && student.getGradesList()!=null) {
            ArrayList<Grade> grades = new ArrayList<>(Arrays.asList(student.getGradesList()));
            System.out.println("Rozmiar listy ocen: " + grades.size());
            for (Grade g : grades) {
                System.out.println(g);
                if (g.getId() == gradeId) {
                    return Response.status(200).entity(g).build();
                }
            }
            return Response.status(404).type(Constants.PLAIN_TEXT).entity(Constants.RESULT_NOT_FOUND).build();
        } else
            return Response.status(404).type(Constants.PLAIN_TEXT).entity(Constants.RESULT_NOT_FOUND).build();
    }


    @GET
    @Path("/{indexNumber}/grades")
    @Produces({Constants.APPLICATION_JSON, Constants.APPLICATION_XML})
    public Response getStudentGrades(@PathParam("indexNumber") int indexNumber,
                                     @QueryParam("courseId") ObjectId courseId,
                                     @QueryParam("value") float value,
                                     @QueryParam("type") String type) {


        datastore = DatabaseHandler.getInstance().getDatastore();
        Student student = datastore.find(Student.class, "index", indexNumber).get();

        if (student != null) {
            Grade[] gradesList = studentDAO.findStudentGrades(student, courseId, value, type);
            return Response.status(200).entity(gradesList).build();
        } else
            return Response.status(404).type(Constants.PLAIN_TEXT).entity(Constants.RESULT_NOT_FOUND).build();
    }


    @DELETE
    @Path("/{indexNumber}") //delete student with his grades
    public Response deleteStudent(@PathParam("indexNumber") int indexNumber) {
        datastore = DatabaseHandler.getInstance().getDatastore();
        try {
            datastore.find(Student.class, "index", indexNumber).get();
        } catch (Exception e) {
            return Response.status(404).build();
        }

        datastore.delete(datastore.find(Student.class, "index", indexNumber));
        return Response.status(204).build();
    }

    @DELETE
    @Path("/{indexNumber}/grades/{gradeId}") //delete student's grade
    public Response deleteStudentGrade(@PathParam("indexNumber") int indexNumber, @PathParam("gradeId") int gradeId) {
        datastore = DatabaseHandler.getInstance().getDatastore();
        Student student = datastore.find(Student.class, "index", indexNumber).get();

        //check if grade with given id exists
        int counter = 0;
        for (Grade g : student.getGradesList()) {
            if (g.getId() == gradeId)
                counter++;
        }

        if (student != null && counter > 0) {
            Grade[] newGrades = new Grade[student.getGradesList().length - 1];
            int i = 0;
            for (Grade g : student.getGradesList()) {
                if (g.getId() != gradeId) {
                    newGrades[i] = g;
                    i++;
                }
            }
            student.setGradesList(newGrades, indexNumber);
            datastore.save(student);

            return Response.status(204).build();
        }
        return Response.status(404).build();
    }

    @POST
    @Consumes({Constants.APPLICATION_JSON, Constants.APPLICATION_XML})
    @Produces({Constants.APPLICATION_JSON, Constants.APPLICATION_XML})
    public Response addStudent(Student student, @Context UriInfo uriInfo) {
        System.out.println("POST student");
        datastore = DatabaseHandler.getInstance().getDatastore();
        List<Student> studentsList = datastore.find(Student.class).asList();
        //int newIdx = studentsList.size() + 127000 + 1;
        StudentIndex newIdxSt = datastore.findAndModify(datastore.find(StudentIndex.class).limit(1),datastore.createUpdateOperations(StudentIndex.class).inc("studentIndex"));
        int newIdx = newIdxSt.getStudentIndex();

        if (student == null) {
            return Response.status(404).type("You cannot add empty student").entity(Constants.RESULT_NOT_FOUND).build();
        } else {
            student.setIndex(newIdx);
            datastore.save(student);

            UriBuilder builder = uriInfo.getAbsolutePathBuilder();
            builder.path(Integer.toString(student.getIndex()));
            return Response.created(builder.build()).status(201).entity(student).build();
        }
    }


    @POST
    @Consumes({Constants.APPLICATION_JSON, Constants.APPLICATION_XML})
    @Produces({Constants.APPLICATION_JSON, Constants.APPLICATION_XML})
    @Path("/{indexNumber}/grades")
    public Response addStudentGrade(@PathParam("indexNumber") int indexNumber, Grade grade, @Context UriInfo uriInfo) {
        datastore = DatabaseHandler.getInstance().getDatastore();
        Student student = datastore.find(Student.class, "index", indexNumber).get();

        if (grade == null) {
            return Response.status(404).build();
        } else {
            if (student == null) {
                return Response.status(404).build();
            } else {
                Course course = grade.getCourse();

                if(course.getId()!= null){
                    course = datastore.find(Course.class, "id", course.getId()).get();
                    grade.setCourse(course);
                    grade.setStudentId(student.getIndex());
                }
                else {
                    return Response.status(404).build();
                }
                Grade[] oldGrades = student.getGradesList();
                Grade[] newGrades;

                if(oldGrades==null) {
                    newGrades = new Grade[1];
                    newGrades[0] = grade;
                }
                else {
                    newGrades = new Grade[oldGrades.length + 1];

                    int i = 0;
                    for (Grade g : oldGrades) {
                        newGrades[i] = g;
                        i++;
                    }
                    newGrades[i] = grade;
                }
                //set grade's id
                Query<Student> studentsQuery = datastore.find(Student.class);
                List<Student> studentsList = studentsQuery.asList();
                int counter = 0;
                for (Student s: studentsList)
                    if(s.getGradesList()!=null)
                        counter+=s.getGradesList().length;

                grade.setId(counter + 1);

                student.setGradesList(newGrades, indexNumber);
                datastore.save(student);

                UriBuilder builder = uriInfo.getAbsolutePathBuilder();
                builder.path(Integer.toString(grade.getId()));
                return Response.created(builder.build()).status(201).entity(grade).build();
            }
        }
    }

    @PUT
    @Consumes({Constants.APPLICATION_JSON, Constants.APPLICATION_XML})
    @Produces({Constants.APPLICATION_JSON, Constants.APPLICATION_XML})
    @Path("/{indexNumber}")
    public Response editStudent(Student student, @PathParam("indexNumber") int indexNumber) {
        datastore = DatabaseHandler.getInstance().getDatastore();
        Student studentFromDB = datastore.find(Student.class, "index", indexNumber).get();

        if (studentFromDB == null) {
            return Response.status(404).type(Constants.PLAIN_TEXT).entity("Student with given index doesn't esists").build();
        } else {
            try {
                if (!student.getName().isEmpty())
                    studentFromDB.setName(student.getName());
            } catch (Exception e) {
            }

            try {
                if (!student.getSurname().isEmpty())
                    studentFromDB.setSurname(student.getSurname());
            } catch (Exception e) {
            }

            try {
                if (student.getBirthdate()!=null)
                    studentFromDB.setBirthdate(student.getBirthdate());
            } catch (Exception e) {
            }

            datastore.save(studentFromDB);
            return Response.status(201).type(Constants.PLAIN_TEXT).entity(Constants.EDITED_SUCCESSFULY).build();
        }
    }

    @PUT
    @Consumes({Constants.APPLICATION_JSON, Constants.APPLICATION_XML})
    @Produces({Constants.APPLICATION_JSON, Constants.APPLICATION_XML})
    @Path("/{indexNumber}/grades/{gradeID}")
    public Response editGrade(Grade grade, @PathParam("indexNumber") int indexNumber, @PathParam("gradeID") int gradeID) {
        datastore = DatabaseHandler.getInstance().getDatastore();
        Student student = datastore.find(Student.class, "index", indexNumber).get();

        if (student == null) {
            return Response.status(404).type(Constants.PLAIN_TEXT).entity("Student with given index doesn't esists").build();
        } else {
            for (int i=0; i< student.getGradesList().length; i++) {
                if (student.getGradesList()[i].getId() == gradeID) {
                    if (grade.getValue() != 0.0)
                        student.getGradesList()[i].setValue(grade.getValue());

                    if (grade.getDate()!=null)
                        student.getGradesList()[i].setDate(grade.getDate());
                    datastore.save(student);
                    return Response.status(201).type(Constants.PLAIN_TEXT).entity(Constants.EDITED_SUCCESSFULY).build();
                }
            }
            return Response.status(404).type(Constants.PLAIN_TEXT).entity("Student hasn't got grade with given index").build();
        }
    }


}
