package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.System.in;

public class Model { //singleton
    Map<Integer, Student> students = new HashMap<>();
    Map<Integer, Course> courses = new HashMap<>();
    Map<Integer, Grade> grades = new HashMap<>();

    private static volatile Model instance = null;

    //private ArrayList<Student> students;
    //private ArrayList<Course> courses;
    //private ArrayList<Grade> grades;


    private Model(){}

    public static Model getInstance() {
        if (instance == null) {
            synchronized(Model.class) {
                if (instance == null) {
                    instance = new Model();
                }
            }
        }
        return instance;
    }


    public Map getStudents() {
        return students;
    }

    public void setStudents(Map students) {
        this.students = students;
    }

    public Map getCourses() {
        return courses;
    }

    public void setCourses(Map courses) {
        this.courses = courses;
    }

    public Map getGrades() { return grades; }

    public void setGrades(Map grades) { this.grades = grades; }

    public void deleteCourse(int courseID){
        this.getCourses().remove(courseID);

        //for each student participated course delete grade
        for(Student s : students.values()){
            Grade[] studentsGrades = s.getGradesList();
            List<Grade> newGrades = new ArrayList<>();
            for(Grade g: studentsGrades){
                if(g.getCourse().getId()==courseID){
                    this.grades.remove(g.getCourse().getId());
                }
                else{
                    newGrades.add(g);
                }
            }
            s.setGradesList(newGrades.toArray(new Grade[newGrades.size()]));
        }
    }

    public void addStudent(Student student){
        student.setIndex(this.students.values().size() + 127001);
        this.students.put(student.getIndex(), student);

        if(student.getGradesList()!=null) {
            for (Grade grade : student.getGradesList()) {
                this.grades.put(grade.getId(), grade);
                this.courses.put(grade.getCourse().getId(), grade.getCourse());
            }
        }
    }

    public void addGradeToStudent(Grade grade, int studentId){
        students.get(studentId).addGrade(grade);
    }

    public void updateStudentGrade(int gradeId, int studentIndex , Grade grade){
        int len = this.students.get(studentIndex).getGradesList().length;
        Grade[] gr = this.students.get(studentIndex).getGradesList();
        for(int i=0; i<len; i++){
            if(gr[i].getId()==gradeId){
                gr[i].setValue(grade.getValue());
                gr[i].setDate(grade.getDate());
                break;
            }
        }
        this.students.get(studentIndex).setGradesList(gr);
    }
}
