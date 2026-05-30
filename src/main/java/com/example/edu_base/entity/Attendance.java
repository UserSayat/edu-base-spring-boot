package com.example.edu_base.entity;

import jakarta.persistence.*;

import java.time.ZonedDateTime;

@Entity
@Table(name = "attendances")
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long lessonId;
    private Long studentId;
    private boolean isPresent;

    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

    public Attendance() {
    }

    public Attendance(Long id, Long lessonId, Long studentId, boolean isPresent, ZonedDateTime createdAt, ZonedDateTime updatedAt) {
        this.id = id;
        this.lessonId = lessonId;
        this.studentId = studentId;
        this.isPresent = isPresent;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLessonId() {
        return lessonId;
    }

    public void setLessonId(Long lessonId) {
        this.lessonId = lessonId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public boolean isPresent() {
        return isPresent;
    }

    public void setPresent(boolean present) {
        isPresent = present;
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
        if (!(obj instanceof Attendance attendance)) return false;
        return id != null && id.equals(attendance.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
