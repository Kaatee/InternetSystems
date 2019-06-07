'use strict';

var URL = 'http://localhost:8000/'
var viewModel = function () {
    var self = this;

    self.studentSubscription = null;
    self.courseSubscription = null;

    self.studentFilters = {
        index: ko.observable(),
        name: ko.observable(),
        surname: ko.observable(),
        birthdate: ko.observable()
    }; //dodac filtr do daty cos typu type

    self.coursesFilters = {
        name: ko.observable(),
        teacherName: ko.observable()
    };

    self.gradesFilters = {
        value: ko.observable(), //< == > od podanej
        courseName: ko.observable()
    };

    self.currentIdx = 0;

    self.gradesList = ko.observableArray();
    self.studentsList = ko.observableArray();
    self.coursesList = ko.observableArray();


    function downloadStudents() {
        var jsonData = ko.toJS(self.studentFilters);
        if (jsonData.birthdate === "") {
            delete jsonData.birthdate;
        }
        if (jsonData.name === "") {
            delete jsonData.name;
        }
        if (jsonData.surname === "") {
            delete jsonData.surname;
        }
        if (jsonData.index === "") {
            delete jsonData.index;
        }

        $.ajax({
            type: 'GET',
            url: URL + 'students',
            contentType: "application/json",
            data: jsonData,
            dataType: "json",
            success: function (data) {
                self.studentsList.removeAll();
                data.forEach(function (record) {
                    self.studentsList.push(new ObservableObject(record));
                });
                self.studentSubscription = self.studentsList.subscribe(removedObjectCallback, null, 'arrayChange');
            },
            error: function (jq, st, error) {
                console.log("error");
                alert(error);
            }
        });
    }
    downloadStudents();

    function downloadCourses(){
        var jsonData = ko.toJS(self.coursesFilters);
        if (jsonData.name === "") {
            delete jsonData.name;
        }
        if (jsonData.teacherName === "") {
            delete jsonData.teacherName;
        }

        $.ajax({
            type: 'GET',
            url: URL + 'courses',
            contentType: "application/json",
            data: jsonData,
            dataType: "json",
            success: function(data) {
                self.coursesList.removeAll();
                data.forEach(function (record) {
                    self.coursesList.push(new ObservableObject(record));
                });
                self.studentSubscription = self.coursesList.subscribe(removedObjectCallback, null, 'arrayChange');
            },
            error:function(jq, st, error){
                console.log("error")
                alert(error);
            }
        });
    }
    downloadCourses();


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
                    self.gradesList.removeAll();

                    data.forEach(function (record) {
                        self.gradesList.push(new ObservableObject(record));
                    });
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

    self.studentSubscription = null;

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
            self.studentsList.push(new ObservableObject(data));

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
            self.coursesList.push(new ObservableObject(data));
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
            self.studentsList.remove(student);
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
            self.coursesList.remove(course);
        });
    };

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

    Object.keys(self.studentFilters).forEach(function (key) {
        self.studentFilters[key].subscribe(function (val) {
            // Disable auto delete from database
            if (self.studentSubscription) {
                self.studentSubscription.dispose();
            }
            self.studentsList.removeAll();
            downloadStudents();
        });
    });

    Object.keys(self.coursesFilters).forEach(function (key) {
        self.coursesFilters[key].subscribe(function (val) {
            if (self.courseSubscription) {
                self.courseSubscription.dispose();
            }
            self.coursesList.removeAll();
            downloadCourses();
        });
    });

}


$(document).ready(function(){
    ko.applyBindings(new viewModel());

});




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
        console.log(data);
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

function removedObjectCallback(changes) {
    changes.forEach(function(change) {
        // Student / Course deleted from database
        if (change.status === 'deleted') {
            $.ajax({
                url: resourceUrl(change.value),
                type: 'DELETE',
                dataType : "json",
                contentType: "application/json"
            }).done(function() {
                console.log('Object removed from eStudent service');
            });
        }
    })
}