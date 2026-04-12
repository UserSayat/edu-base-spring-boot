package com.example.edu_base.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class StudentGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String groupName;

    @Setter
    private LocalDateTime createdAt;
    @Setter
    private LocalDateTime updatedAt;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof StudentGroup studentGroup)) return false;
        return id != null && id.equals(studentGroup.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
