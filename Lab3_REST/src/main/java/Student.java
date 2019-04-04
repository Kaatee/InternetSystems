import java.util.ArrayList;
import java.util.Date;

public class Student {
    private int index;
    private String name;
    private String surname;
    private Date birthdate;
    private ArrayList<Grade> gradesList;

    public Student(int index, String name, String surname, Date birthdate, ArrayList<Grade> gradesList){
        this.index = index;
        this.name = name;
        this.surname = surname;
        this.birthdate = birthdate;
        this.gradesList = new ArrayList<>(gradesList);
    }


    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public ArrayList getGradesList() {
        return gradesList;
    }

    public void setGradesList(ArrayList gradesList) {
        this.gradesList = gradesList;
    }
}

