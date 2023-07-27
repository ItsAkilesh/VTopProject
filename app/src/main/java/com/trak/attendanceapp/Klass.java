package com.trak.attendanceapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Klass implements Serializable {
    public String name;
    public String courseCode;

    public Map<String, ArrayList<Student>> getAttendances() {
        return attendances;
    }

    public void setAttendances(Map<String, ArrayList<Student>> attendances) {
        this.attendances = attendances;
    }

    public Map<String, ArrayList<Student>> attendances ; // date, list of students

    public Klass(String name, String courseCode, ArrayList<Student> students) {
        this.name = name;
        this.courseCode = courseCode;
        this.students = students;
    }
    public Klass(){
        this.attendances = new HashMap<>();
    }

    @Override
    public String toString() {
        return "Klass{" +
                "name='" + name + '\'' +
                ", courseCode='" + courseCode + '\'' +
                ", students=" + students +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public ArrayList<Student> getStudents() {
        return students;
    }

    public void setStudents(ArrayList<Student> students) {
        this.students = students;
    }

    ArrayList<Student> students;
}
