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

    /*
     var resource = ko.mapping.fromJSON(res);
        $.ajax({
            url: resourceUrl(resource),
     */
    self.deleteGrade = function(grade) {
        var jsonGrade = ko.toJS(grade);
        var id = jsonGrade["id"];
        var urlTmp = resourceUrl(grade);
        var url = urlTmp.substring(1);
        console.log(url)
        $.ajax({
            url: URL + url,
            type: 'DELETE',
            dataType : "json",
            contentType: "application/json",
        }).done(function(data) {
            self.gradesList.remove(grade)
        });
    };

    self.addNewGrade = function() {
        console.log(self.newGrade);
        $.ajax({
            url: URL + 'students/' + self.currentIdx + '/grades',
            type: 'POST',
            dataType : "json",
            contentType: "application/json",
            data: ko.mapping.toJSON(self.newGrade)
        }).done(function(data) {
            self.gradesList.push(new ObservableObject(data));
            self.newGrade.value("");
            self.newGrade.date("");
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
            // var observableData = ko.mapping.fromJS(data);
            // var array = observableData();
            // self.studentsList(array);

            data.forEach(function (record) {
                self.studentsList.push(new ObservableObject(record));
            });
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
        console.log(resourceUrl(resource).substring(1));
        $.ajax({
            url: URL + resourceUrl(resource).substring(1),
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
            //var observableData = ko.mapping.fromJS(data);
            //var array = observableData();
            //self.coursesList(array);

            data.forEach(function (record) {
                self.coursesList.push(new ObservableObject(record));
            });
        },
        error:function(jq, st, error){
            alert(error);
        }
    });
}

//subskrybowanie - https://knockoutjs.com/documentation/observables.html#explicitly-subscribing-to-observables

//edycja oceny
    //edycja zeby przy dodawaniu bylo ObservableObject

//filtrowanie - ignorowanie wielkosci liter