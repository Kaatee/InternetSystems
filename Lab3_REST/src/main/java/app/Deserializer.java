package app;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.Model;
import model.StudentsListToDeserialize;

import java.io.File;
import java.io.IOException;

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
