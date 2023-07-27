package com.trak.attendanceapp;

import java.io.Serializable;

public class Student implements Serializable {
    public String name;
    public String regid;
    public Boolean marked;

    public Boolean getMarked() {
        return marked;
    }

    public void setMarked(Boolean marked) {
        this.marked = marked;
    }
    public Student(){

    }

    public Student(String name, String regid, Boolean marked ) {
        this.name = name;
        this.regid = regid;
        this.marked = marked;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegid() {
        return regid;
    }

    public void setRegid(String regid) {
        this.regid = regid;
    }

    @Override
    public String toString() {
        return regid + " - " + name;
    }
}
