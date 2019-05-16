package Mongo;

import com.mongodb.*;
import model.*;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class DatabaseHandler {
    private static DatabaseHandler instance;
    private static Datastore datastore;

    private DatabaseHandler(){
        
    }

    public static synchronized DatabaseHandler getInstance(){
        if (instance == null) {
            //-----------   connecting to DB ---------------------------
            MongoClient mongoClient = new MongoClient("localhost", 8004); //"mongodb://localhost:27017"
            Morphia morphia = new Morphia();
            datastore = morphia.createDatastore(mongoClient, "students");
            morphia.mapPackage("model");

            Query<Student> studentsQuery = datastore.find(Student.class);
            List<Student> studentsList = studentsQuery.asList();

            SimpleDateFormat dateFormater = new SimpleDateFormat("dd-MM-yyyy");

            if(studentsList.size()==0) { //fill database only if it is empty
                try {

                    Course course1 = new Course("informatyka", "Jan Kowalski");
                    datastore.save(course1);
                    Course c1 = datastore.find(Course.class, "name", "informatyka").get();

                    Grade grade1 = new Grade(1, 3, dateFormater.parse("01-05-1996"), c1);
                    Grade[] grades1 = new Grade[1];
                    grades1[0] = grade1;
                    Student student1 = new Student(127001, "Katarzyna", "Jozwiak", dateFormater.parse("11-11-1996"), grades1);

                    Course course2 = new Course("matma", "Janina Nowak");
                    datastore.save(course2);
                    Course c2 = datastore.find(Course.class, "name", "matma").get();

                    Grade grade22 = new Grade(2, 4, dateFormater.parse("11-11-1996"), c2);
                    Grade grade23 = new Grade(3, 2, dateFormater.parse("01-11-1996"), c1);
                    Grade[] grades2 = new Grade[2];
                    grades2[0] = grade22;
                    grades2[1] = grade23;

                    Student student2 = new Student(127002, "Piotr", "Pawlaczyk", dateFormater.parse("01-11-1996"), grades2);

                    datastore.save(student1);
                    datastore.save(student2);

                    datastore.save(new StudentIndex(127002));
                }
                catch (ParseException e){}
            }
        }

        return instance;
    }

    public static Datastore getDatastore() {
        return datastore;
    }

    public static void setDatastore(Datastore datastore) {
        DatabaseHandler.datastore = datastore;
    }
}
