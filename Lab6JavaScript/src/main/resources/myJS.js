"use strict";

console.log("aaa")
$(document).ready(function() {

    ko.applyBindings({
        student: [
            { index: '123', name: "Abc", surname: "fgbdv", birthdate: "2019-04-13" },
            { index: '234', name: "rtef", surname: "hgfbvd", birthdate: "2019-04-03" },
            { index: '654', name: "gbfd", surname: "gfbvd", birthdate: "2019-04-23" }
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