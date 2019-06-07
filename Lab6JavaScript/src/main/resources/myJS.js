'use strict';

var URL = 'http://localhost:8000/'
var modified = false;

var viewModel = function () {
    var self = this;

    var dataPossibleType = function (name, value) {
        this.dateName = name;
        this.dateValue = value;
    };

    self.filtreDataTypes = ko.observableArray([
        new dataPossibleType("Do", "dateTo"),
        new dataPossibleType("Dokładnie", "dateExacly"),
        new dataPossibleType("Od", "dateFrom")
    ]);

    var valuePossibleType = function (name, value) {
        this.valueName = name;
        this.valueValue = value;
    };

    self.filtreValueTypes = ko.observableArray([
        new valuePossibleType("Wieksze niz", "greater"),
        new valuePossibleType("Mniejsze niz", "less"),
        new valuePossibleType("Dokladnie", "equal")
    ]);


    self.studentSubscription = null;
    self.courseSubscription = null;
    self.gradeSubscription = null;

    self.studentFilters = {
        index: ko.observable(),
        name: ko.observable(),
        surname: ko.observable(),
        birthdate: ko.observable(),
        type: ko.observable(),
        dateTo: ko.observable(),
        dateExacly: ko.observable(),
        dateFrom: ko.observable()

    };

    self.coursesFilters = {
        name: ko.observable(),
        teacherName: ko.observable()
    };

    self.gradesFilters = {
        value: ko.observable(),
        courseId: ko.observable(),
        type: ko.observable()
    };

    self.gradesList = ko.observableArray();
    self.studentsList = ko.observableArray();
    self.coursesList = ko.observableArray();

    console.log("download students");
    function downloadStudents() {
        var jsonData = ko.toJS(self.studentFilters);

        if (jsonData.birthdate === "") {
            delete jsonData.birthdate;
            delete jsonData.type;
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
        if (jsonData.type === "") {
            delete jsonData.birthdate;
        }
        else {
            if (jsonData.type == "dateTo") {
                jsonData.dateTo = jsonData.birthdate;
                delete jsonData.birthdate;
                delete jsonData.dateExacly;
                delete jsonData.dateFrom;
                delete jsonData.type;
            }

            if (jsonData.type == "dateExacly") {
                jsonData.dateExacly = jsonData.birthdate;
                delete jsonData.birthdate;
                delete jsonData.dateTo;
                delete jsonData.dateFrom;
                delete jsonData.type;
            }
            if (jsonData.type == "dateFrom") {
                jsonData.dateFrom = jsonData.birthdate;
                delete jsonData.birthdate;
                delete jsonData.dateTo;
                delete jsonData.dateExacly;
                delete jsonData.type;
            }

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
                    self.currentStudentIdx = record["index"]

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

    console.log("download courses");
    function downloadCourses() {
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
            success: function (data) {
                self.coursesList.removeAll();
                data.forEach(function (record) {
                    self.coursesList.push(new ObservableObject(record));
                });
                self.courseSubscription = self.coursesList.subscribe(removedObjectCallback, null, 'arrayChange');
            },
            error: function (jq, st, error) {
                console.log("error")
                alert(error);
            }
        });
    }
    downloadCourses();


    self.getStudentsGrades = function(student){
        var jsonStudent = ko.toJS(student);
        var index = jsonStudent["index"];
        self.modified = true;
        self.currentStudentIdx = index;
        downloadGrades();
    }


    function downloadGrades() {
        console.log("download grades");
        var jsonData = ko.toJS(self.gradesFilters);
        if (jsonData.value === "") {
            delete jsonData.value;
            delete jsonData.type;
        }
        if (jsonData.courseId === "") {
            delete jsonData.courseId;
        }
        if (jsonData.type === "") {
            delete jsonData.type;
            delete jsonData.value;
        }
        console.log("Do filtrowania ocen: ");
        console.log(jsonData);

        //var index = self.currentStudent;
        //self.currentIdx = index;
        console.log("Sciezka: " + URL + 'students/' +self.currentStudentIdx + '/grades');
        $.ajax({
            type: 'GET',
            url: URL + 'students/' +self.currentStudentIdx + '/grades',
            contentType: "application/json",
            data: jsonData,
            dataType: "json",
            success: function (data) {
                self.gradesList.removeAll();

                data.forEach(function (record) {
                    self.gradesList.push(new ObservableObject(record));
                    console.log(record);
                });
                console.log("Wchodze do grades");
                self.gradeSubscription = self.gradesList.subscribe(removedObjectCallback, null, 'arrayChange');

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
        console.log("Nowy student: ");
        console.log(self.newStudent);
        console.log(URL+'students');
        $.ajax({
            url: URL + 'students',
            type: 'POST',
            dataType : "json",
            contentType: "application/json",
            data: ko.mapping.toJSON(self.newStudent)
        }).done(function(data) {
            console.log("Nowy student: ");
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
            console.log("Nowy kurs: ");
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
            console.log("usuwam studenta")
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
        console.log("Url w usuwaniu oceny ");
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
        console.log("Nowa ocena: ");
        console.log(self.newGrade);
        $.ajax({
            url: URL + 'students/' + self.currentStudentIdx + '/grades',
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
            console.log("Download students from object");
            downloadStudents();
        });
    });

    Object.keys(self.coursesFilters).forEach(function (key) {
        self.coursesFilters[key].subscribe(function (val) {
            if (self.courseSubscription) {
                self.courseSubscription.dispose();
            }
            self.coursesList.removeAll();
            console.log("Download courses from object");
            downloadCourses();
        });
    });

    Object.keys(self.gradesFilters).forEach(function (key) {
        console.log("Jestem na zewnątrz object keys od grades");

           self.gradesFilters[key].subscribe(function (val) {
               if (self.gradeSubscription) {
                   self.gradeSubscription.dispose();
               }
               self.gradesList.removeAll();
               console.log("Download grades from object keys");
               downloadGrades();
           });
    });

}


$(document).ready(function(){
    var model = new viewModel()
    ko.applyBindings(model);
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
    console.log("Remove oject callable - changes: ");
    console.log(changes);
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