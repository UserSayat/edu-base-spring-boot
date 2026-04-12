package com.example.edu_base.entity;

import com.example.edu_base.common.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String lastName;
    @Setter
    private String firstName;
    @Setter
    private String middleName;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Status status;

    @Setter
    @ManyToOne
    private StudentGroup studentGroup;

    @Setter
    private LocalDateTime createdAt;
    @Setter
    private LocalDateTime updatedAt;

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
