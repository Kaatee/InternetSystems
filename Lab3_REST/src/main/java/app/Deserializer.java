package app;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Deserializer {
    private static Deserializer instance;
    private static ObjectMapper mapper;
    private String path;
    private static StudentsListToDeserialize studentsList;
    private static Model model;


    private Deserializer(String path){
        this.path = path;
    }

    public static synchronized Deserializer getInstance(String path) throws IOException {
        if (instance == null) {
            instance = new Deserializer(path);

            instance.mapper = new ObjectMapper();
            instance.studentsList = mapper.readValue(new File(instance.path), StudentsListToDeserialize.class);

            //construct model object
            model = Model.getInstance();
            HashMap<Integer, Student> studentsList = new HashMap<>();
            for(Student s: instance.studentsList.getStudents()){
                studentsList.put(s.getIndex(), s);
            }

            HashMap gradesList = new HashMap();
            HashMap coursesList = new HashMap();

            for(Student student: studentsList.values()){
                for(Grade g: student.getGradesList()) {
                    gradesList.put(g.getId(), g);
                    coursesList.put(g.getCourse().getId(), g.getCourse());
                }
            }

            model.setCourses(coursesList);
            model.setGrades(gradesList);
            model.setStudents(studentsList);
        }

        return instance;
    }

    public static ObjectMapper getMapper() {
        return mapper;
    }

    public static void setMapper(ObjectMapper mapper) {
        Deserializer.mapper = mapper;
    }

    public static Model getModel() {
        return model;
    }

    public static void setModel(Model model) {
        Deserializer.model = model;
    }

    public static StudentsListToDeserialize getStudentsList() {
        return studentsList;
    }

    public static void setStudentsList(StudentsListToDeserialize studentsList) {
        Deserializer.studentsList = studentsList;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
