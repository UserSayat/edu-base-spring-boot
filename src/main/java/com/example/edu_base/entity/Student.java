package com.example.edu_base.entity;

import com.example.edu_base.common.StudentStatus;

import java.time.ZonedDateTime;

public class Student {
    private Long id;

    private String lastName;
    private String firstName;
    private String middleName;

    private StudentStatus studentStatus;

    private Long studentGroupId;

    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

    public Student() {
    }

    public Student(Long id, String lastName, String firstName, String middleName, StudentStatus studentStatus, Long studentGroupId, ZonedDateTime createdAt, ZonedDateTime updatedAt) {
        this.id = id;
        this.lastName = lastName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.studentStatus = studentStatus;
        this.studentGroupId = studentGroupId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public StudentStatus getStudentStatus() {
        return studentStatus;
    }

    public void setStatus(StudentStatus studentStatus) {
        this.studentStatus = studentStatus;
    }

    public Long getStudentGroupId() {
        return studentGroupId;
    }

    public void setStudentGroupId(Long studentGroupId) {
        this.studentGroupId = studentGroupId;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Student student)) return false;
        return id != null && id.equals(student.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
