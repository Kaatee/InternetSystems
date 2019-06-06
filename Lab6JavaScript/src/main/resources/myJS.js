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
        $.ajax({
            url: URL + 'students',
            type: 'POST',
            dataType : "json",
            contentType: "application/json",
            data: ko.mapping.toJSON(self.newStudent)
        }).done(function(data) {
            self.students.studentsList.push(new ObservableObject(data));
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
            self.courses.coursesList.push(new ObservableObject(data));
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
            self.getStudentsGrades.gradesList.push(new ObservableObject(data));
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

//refresh po dodaniu studenta
//'-Wybierz-' w selekcie niewybranych
//subskrybowanie


//edycja studenciaka
//edycja przedmiotu