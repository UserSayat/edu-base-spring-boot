package com.example.edu_base.model;

import java.util.Objects;

public class Student {
    private Long id;
    private String lastname;
    private String firstname;
    private String middlename;
    private Status status;
    private Group group;

    public Student(Long id, String lastname, String firstname, String middlename, Status status, Group group) {
        this.id = id;
        this.lastname = lastname;
        this.firstname = firstname;
        this.middlename = middlename;
        this.status = status;
        this.group = group;
    }

    public Long getId() {
        return id;
    }

    public String getLastname() {
        return lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getMiddlename() {
        return middlename;
    }

    public Status getStatus() {
        return status;
    }

    public Group getGroup() {
        return group;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Student student)) return false;
        return Objects.equals(id, student.id) && Objects.equals(lastname, student.lastname) && Objects.equals(firstname, student.firstname) && Objects.equals(middlename, student.middlename) && status == student.status && Objects.equals(group, student.group);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lastname, firstname, middlename, status, group);
    }
}
