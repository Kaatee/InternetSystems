'use strict';

var URL = 'http://localhost:8000/'
var viewModel= {
    students: new studentsViewModel(),
    courses: new coursesViewModel()
}


$(document).ready(function(){
    ko.applyBindings(viewModel.students);
    ko.applyBindings(viewModel.courses);
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

