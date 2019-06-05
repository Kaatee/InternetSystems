'use strict';

var URL = 'http://localhost:8000/'
var viewModel = function () {
    var self = this;
    self.gradesList = ko.observableArray();
    self.students = new studentsViewModel(),
        self.courses = new coursesViewModel(),
        self.getStudentsGrades = function(student) {
            var jsonStudent = ko.toJS(student);
            var index = jsonStudent["index"];
            $.ajax({
                type: 'GET',
                url: URL + 'students/' + index + '/grades',
                contentType: "application/json",
                dataType: "json",
                success: function (data) {
                    console.log("Oceny: ")
                    console.log(data)

                    var observableData = ko.mapping.fromJS(data);
                    var array = observableData();
                    console.log(array)
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
//dobry przedmiot w selecie
//subskrybowanie

//dodawanie oceny studenciakowi
//usuwanie oceny studenciakowi
//edycja studenciaka
//edycja przedmiotu