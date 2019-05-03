package Mongo;

import model.Course;
import model.Student;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class CourseDAO {
    Datastore datastore;
    private static CourseDAO instance = null;

    public static CourseDAO getInstance() {
        if (instance == null) {
            instance = new CourseDAO();
        }
        return instance;
    }

    public List<Course> findCourses(String nameParam, String teacherNameParam){
        datastore = DatabaseHandler.getInstance().getDatastore();
        Query<Course> coursesQuery = datastore.find(Course.class);
        List<Course> coursesList = new ArrayList<>();

        Optional<String> name = Optional.ofNullable(nameParam);
        Optional<String> teacherName = Optional.ofNullable(teacherNameParam);

        name.ifPresent(s -> coursesQuery.and(coursesQuery.criteria("name").containsIgnoreCase(s)));
        teacherName.ifPresent(s -> coursesQuery.and(coursesQuery.criteria("teacherName").containsIgnoreCase(s)));

        coursesList = coursesQuery.asList();

        return coursesList;
    }
}
