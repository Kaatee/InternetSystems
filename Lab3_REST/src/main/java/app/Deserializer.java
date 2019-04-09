package app;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.Model;

import java.io.File;
import java.io.IOException;

public class Deserializer {
    private static Deserializer instance;
    public static ObjectMapper mapper;
    private String path;
    private static Model lists;


    private Deserializer(String path){
        this.path = path;
    }

    public static synchronized Deserializer getInstance(String path) throws IOException {
        if (instance == null) {
            instance = new Deserializer(path);

            instance.mapper = new ObjectMapper();
            instance.lists = mapper.readValue(new File(instance.path), Model.class);
        }

        return instance;
    }

}
