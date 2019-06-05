'use strict';

var URL = 'http://localhost:8000/'
var viewModel= function() {
    self.students = new studentsViewModel(),
    self.courses = new coursesViewModel(),
    self.getStudentsGrades = function(student) {
        console.log("abc");
        console.log(student["grade"]);



        window.location.href = '#grades';
    }
}


$(document).ready(function(){
    ko.applyBindings(viewModel);
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
            //console.log(data["link"])
            var observableData = ko.mapping.fromJS(data);
            var array = observableData();
            self.studentsList(array);
        },
        error: function (jq, st, error) {
            alert(error);
        }
    });
}

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



