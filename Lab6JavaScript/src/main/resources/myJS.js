'use strict';

var URL = 'http://localhost:8000/'
var viewModel = function () {
    var self = this;
    self.currentIdx = 0;
    self.gradesList = ko.observableArray();
    self.students = new studentsViewModel(),
        self.courses = new coursesViewModel(),
        self.getStudentsGrades = function(student) {
            var jsonStudent = ko.toJS(student);
            var index = jsonStudent["index"];
            self.currentIdx = index;
            $.ajax({
                type: 'GET',
                url: URL + 'students/' + index + '/grades',
                contentType: "application/json",
                dataType: "json",
                success: function (data) {
                    var observableData = ko.mapping.fromJS(data);
                    var array = observableData();
                    self.gradesList(array);
                },
                error: function (jq, st, error) {
                    alert(error);
                }
            });
            window.location.href = '#grades';
        }

    self.newStudent = {
        name: ko.observable(),
        surname: ko.observable(),
        birthdate: ko.observable()
    };

    self.newCourse = {
        name: ko.observable(),
        teacherName: ko.observable()
    };

    self.newGrade = {
        date: ko.observable(),
        value: ko.observable(),
        course: {
            id: ko.observable()
        }
    };

    self.addNewStudent = function() {
        console.log(self.newStudent);
        console.log(URL+'students');
        $.ajax({
            url: URL + 'students',
            type: 'POST',
            dataType : "json",
            contentType: "application/json",
            data: ko.mapping.toJSON(self.newStudent)
        }).done(function(data) {
            console.log(data);
            self.students.studentsList.push(new ObservableObject(data));
            self.newStudent.name("");
            self.newStudent.surname("");
            self.newStudent.birthdate("");
        });
    };

    self.addNewCourse = function() {
        $.ajax({
            url: URL + 'courses',
            type: 'POST',
            dataType : "json",
            contentType: "application/json",
            data: ko.mapping.toJSON(self.newCourse)
        }).done(function(data) {
            console.log(data);
            self.courses.coursesList.push(new ObservableObject(data));
            self.newCourse.name("");
            self.newCourse.teacherName("");
        });
    };

    self.deleteStudent = function(student) {
        var jsonStudent = ko.toJS(student);
        var idx = jsonStudent["index"];
        $.ajax({
            url: URL + 'students/'+ idx,
            type: 'DELETE',
            dataType : "json",
            contentType: "application/json",
        }).done(function(data) {
            console.log("abc")
            self.students.studentsList.remove(student);
        });
    };

    self.deleteCourse = function(course) {
        var jsonCourse = ko.toJS(course);
        var id = jsonCourse["id"];
        $.ajax({
            url: URL + 'courses/'+ id,
            type: 'DELETE',
            dataType : "json",
            contentType: "application/json",
        }).done(function(data) {
            self.courses.coursesList.remove(course);
        });
    };

    self.deleteGrade = function(grade) {
        var jsonGrade = ko.toJS(grade);
        var id = jsonGrade["id"];
        var studentId = jsonGrade["studentId"];
        $.ajax({
            url: URL + 'students/'+ studentId + '/grades/' + id,
            type: 'DELETE',
            dataType : "json",
            contentType: "application/json",
        }).done(function(data) {
            self.gradesList.remove(grade)
        });
    };

/*
  <grade>
        <id>3</id>
        <value>3.0</value>
        <date>1996-11-01T00:00:00+01:00</date>
        <course id="5cf7f917c20940463203c438"></course>
    </grade>
 */

    self.addNewGrade = function() {
        console.log(self.newGrade);
        $.ajax({
            url: URL + 'students/' + self.currentIdx + '/grades',
            type: 'POST',
            dataType : "json",
            contentType: "application/json",
            data: ko.mapping.toJSON(self.newGrade)
        }).done(function(data) {
            console.log("Dodaje ocene")
            console.log(data.grade)
            self.gradesList.push(new ObservableObject(data));
            console.log("abc");
            self.newGrade.value("");
            console.log("xyz");
            self.newGrade.date("");
            console.log("123");
        });
    };
}



$(document).ready(function(){
    ko.applyBindings(new viewModel());

});


function studentsViewModel() {
    var self = this;
    self.studentsList = ko.observableArray();
    $.ajax({
        type: 'GET',
        url: URL + 'students',
        contentType: "application/json",
        dataType: "json",
        success: function (data) {
            //console.log(data)
            var observableData = ko.mapping.fromJS(data);
            var array = observableData();
            //console.log(array)
            self.studentsList(array);
        },
        error: function (jq, st, error) {
            alert(error);
        }
    });
}

function resourceUrl(record) {
    var links = record.link();
    var resourceUrl = links.find(function(link) {
        return link.params.rel() === "self"
    });
    return resourceUrl.href();
}

function ObservableObject(data) {
    var self = this;
    ko.mapping.fromJS(data, {}, self);

    ko.computed(function() {
        return ko.mapping.toJSON(self);
    }).subscribe(function(res) {
        var resource = ko.mapping.fromJSON(res);
        $.ajax({
            url: resourceUrl(resource),
            type: 'PUT',
            dataType : "json",
            contentType: "application/json",
            data: ko.mapping.toJSON(self)
        }).done(function(data) {
            console.log('Record updated');
        });
    });
}

//$root - zawsze z VM

function coursesViewModel(){
    var self = this;
    self.coursesList = ko.observableArray();
    $.ajax({
        type: 'GET',
        url: URL + 'courses',
        contentType: "application/json",
        dataType: "json",
        success: function(data) {
            var observableData = ko.mapping.fromJS(data);
            var array = observableData();
            self.coursesList(array);
        },
        error:function(jq, st, error){
            alert(error);
        }
    });
}

//refresh po usunieciu oceny + naprawa usuniecia

//'-Wybierz-' w selekcie niewybranych i dobre w selekcie dodanych
//subskrybowanie

//edycja studenciaka
    //edycja zeby przy dodawaniu bylo ObservableObject
//edycja przedmiotu
    //edycja zeby przy dodawaniu bylo ObservableObject