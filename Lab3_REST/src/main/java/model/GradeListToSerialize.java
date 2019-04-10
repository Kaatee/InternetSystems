package model;

public class GradeListToSerialize {
    private Grade[] grades;

    public GradeListToSerialize(){}

    public GradeListToSerialize(Grade[] grades){
        this.grades = grades;
    }

    public Grade[] getGrades() {
        return grades;
    }

    public void setGrades(Grade[] grades) {
        this.grades = grades;
    }
}
