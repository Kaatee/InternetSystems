'use strict';

var URL = 'http://localhost:8000/';
var sampleData = [{
    "index": 127001,
    "name": "John",
    "surname": "Smith",
    "birthdate": "1996-11-11"
},
    {
        "index": 127002,
        "name": "Abcd",
        "surname": "Xyz",
        "birthdate": "1996-11-01"
    }
];


var StateViewModel = function() {
    var self = this;
    self.students = ko.observableArray();

    setTimeout(function() {
        //console.log(sampleData)
        ko.mapping.fromJS(sampleData, {}, self.students);
    }, 1000);
}

var model = new StateViewModel();
ko.applyBindings(model);

