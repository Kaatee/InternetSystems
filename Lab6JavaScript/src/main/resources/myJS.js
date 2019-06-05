'use strict';

var URL = 'http://localhost:8000/'
var viewModel = function () {
    var self = this;
    self.gradesList = ko.observableArray();
    self.students = new studentsViewModel(),
        self.courses = new coursesViewModel(),
        self.getStudentsGrades = function(student) {
        console.log("Dziala klikania")
            var jsonStudent = ko.toJS(student);
            var index = jsonStudent["index"];
            console.log(index)
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
