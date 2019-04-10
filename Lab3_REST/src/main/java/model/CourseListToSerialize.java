package model;

public class CourseListToSerialize {
    private Course[] courses;

    public CourseListToSerialize(){}

    public CourseListToSerialize(Course[] courses){
        this.courses = courses;
    }

    public Course[] getCourses() {
        return courses;
    }

    public void setCourses(Course[] courses) {
        this.courses = courses;
    }
}
