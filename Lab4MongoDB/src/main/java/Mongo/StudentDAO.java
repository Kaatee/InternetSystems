package Mongo;

import model.Grade;
import model.Student;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import java.util.*;

public class StudentDAO {
    Datastore datastore;
    private static StudentDAO instance = null;


    public static StudentDAO getInstance() {
        if (instance == null) {
            instance = new StudentDAO();
        }
        return instance;
    }

    public List<Student> findStudents(String nameParam, String surnameParam, Date dateFromParam, Date dateToParam, Integer indexParam, Date dateExaclyParam) {
        datastore = DatabaseHandler.getInstance().getDatastore();
        Query<Student> studentsQuery = datastore.find(Student.class);
        List<Student> studentsList = new ArrayList<>();

        Optional<String> name = Optional.ofNullable(nameParam);
        Optional<String> surname = Optional.ofNullable(surnameParam);
        Optional<Date> dateFrom = Optional.ofNullable(dateFromParam);
        Optional<Date> dateTo = Optional.ofNullable(dateToParam);
        Optional<Integer> index = Optional.ofNullable(indexParam);
        Optional<Date> dateExacly = Optional.ofNullable(dateExaclyParam);

        if (!index.isPresent()) {
            name.ifPresent(s -> studentsQuery.and(studentsQuery.criteria("name").containsIgnoreCase(s)));
            surname.ifPresent(s -> studentsQuery.and(studentsQuery.criteria("surname").containsIgnoreCase(s)));
            dateFrom.ifPresent(date -> studentsQuery.and(studentsQuery.criteria("birthdate").greaterThanOrEq(date)));
            dateTo.ifPresent(date -> studentsQuery.and(studentsQuery.criteria("birthdate").lessThanOrEq(date)));
            dateExacly.ifPresent(date -> studentsQuery.and(studentsQuery.criteria("birthdate").equal(date)));

            studentsList = studentsQuery.asList();
        } else {
            Integer indexInt = index.get();
            Student student = datastore.find(Student.class, "index", indexInt).get();
            studentsList.add(student);
        }

        return studentsList;
    }

    public Grade[] findStudentGrades(Student student, ObjectId courseIdParam, Float valueParam, String typeParam) {
        Optional<ObjectId> courseId = Optional.ofNullable(courseIdParam);
        Optional<Float> value = Optional.ofNullable(valueParam);
        Optional<String> type = Optional.ofNullable(typeParam);

        if (student.getGradesList() != null) {

            Collection<Grade> gradesTmp = new ArrayList(Arrays.asList(student.getGradesList()));
            courseId.ifPresent(n -> gradesTmp.removeIf(grade -> !(grade.getCourse().getId().toString().contains(n.toString()))));
            List<Grade> gradesNew = new ArrayList<>();

            if(value.isPresent()){
                float valueF = value.get().floatValue();

                if (type.isPresent()) {
                    if (type.get().equals("equal")) {
                        for(Grade g: gradesTmp){
                            if(g.getValue() == valueF){
                                gradesNew.add(g);
                            }
                        }

                    } else if (type.get().equals("greater")) {
                        for(Grade g: gradesTmp){
                            if(g.getValue() > valueF){
                                gradesNew.add(g);
                            }
                        }

                    } else if (type.get().equals("less")) {
                        for(Grade g: gradesTmp){
                            if(g.getValue() < valueF){
                                gradesNew.add(g);
                            }
                        }

                    }
                }
            }

            if(!type.isPresent()){
                for(Grade g: gradesTmp) {
                    gradesNew.add(g);
                }
            }



            Grade[] grades = gradesNew.toArray(new Grade[gradesNew.size()]);
            return grades;
        }
        return new Grade[0];
    }

}
