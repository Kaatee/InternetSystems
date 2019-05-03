package model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Id;

public class StudentIndex {
    @Id
    private ObjectId id;

    private Integer studentIndex;

    public StudentIndex(){}

    public StudentIndex(int studentIndex){
        this.studentIndex = studentIndex;
    }

    public ObjectId getId() {
        return id;
    }
    public void setId(ObjectId id) {
        this.id = id;
    }

    public Integer getStudentIndex() {
        return studentIndex;
    }
    public void setStudentIndex(Integer studentIndex) {
        this.studentIndex = studentIndex;
    }
}
