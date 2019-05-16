'use strict';
console.log("aaa")
$(document).ready(function() {
    
    ko.applyBindings({
    student: [
        { index: 'Bert', name: "name", surname: "aaa", birthdate: "2019-04-03" },
        { index: 'ww', name: "name", surname: "aaa", birthdate: "2019-04-03" }
    ],
    course: [
        { name: 'Bert', teacher: "name"},
        { name: 'Bert', teacher: "name"}
    ],
    grade: [
        { grade: 3.5, course: [
                { name: 'Bert', teacher: "name"},
                { name: 'aa', teacher: "name"}
            ]}
    ]
});

});