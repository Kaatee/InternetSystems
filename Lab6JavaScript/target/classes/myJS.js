'use strict';

var URL = 'http://localhost:8000/'

$(document).ready(function(){
    console.log("Abc")
    ko.applyBindings(new studentsViewModel());
});

function studentsViewModel() {
    var self = this;
    self.studentsList = ko.observableArray();

    $.ajax({
        type: 'GET',
        url: URL + 'students',
        contentType: "application/json",
        dataType: "json",
        success: function(data) {
            var observableData = ko.mapping.fromJS(data);
            var array = observableData();
            self.studentsList(array);
         },
        error:function(jq, st, error){
            alert(error);
        }
    });
}

