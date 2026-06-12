package com.example.edu_base.entity;

import java.time.LocalDate;
import java.time.ZonedDateTime;

public class Lesson {

    private Long id;

    private Long subjectId;
    private LocalDate date;
    private Long pairNumber;
    private Long teacherId;
    private Long studentGroupId;

    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

    public Lesson() {
    }

    public Lesson(Long id, Long subjectId, LocalDate date, Long pairNumber, Long teacherId, Long studentGroupId, ZonedDateTime createdAt, ZonedDateTime updatedAt) {
        this.id = id;
        this.subjectId = subjectId;
        this.date = date;
        this.pairNumber = pairNumber;
        this.teacherId = teacherId;
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

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Long getPairNumber() {
        return pairNumber;
    }

    public void setPairNumber(Long pairNumber) {
        this.pairNumber = pairNumber;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
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
        if (!(obj instanceof Lesson lesson)) return false;
        return id != null && id.equals(lesson.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
