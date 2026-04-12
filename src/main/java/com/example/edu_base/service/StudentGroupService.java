package com.example.edu_base.service;

import com.example.edu_base.dto.StudentGroupDto;
import com.example.edu_base.entity.StudentGroup;
import com.example.edu_base.repository.StudentGroupRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StudentGroupService {
    @Autowired
    private StudentGroupRepository studentGroupRepository;

    public StudentGroupService(StudentGroupRepository studentGroupRepository) {
        this.studentGroupRepository = studentGroupRepository;
    }

    public List<StudentGroupDto> getStudentGroups() {
        List<StudentGroup> groups = studentGroupRepository.findAll();
        return groups
                .stream()
                .map(entity -> StudentGroupDto
                        .builder()
                        .id(entity.getId())
                        .groupName(entity.getGroupName())
                        .build())
                .toList();
    }

    public StudentGroupDto getStudentGroupById(long id) {
        StudentGroup groupEntity = studentGroupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Группа с id " + id + " не найдена"));
        return new StudentGroupDto(
                groupEntity.getId(),
                groupEntity.getGroupName(),
                groupEntity.getCreatedAt(),
                groupEntity.getUpdatedAt());
    }

    public StudentGroupDto addStudentGroup(StudentGroupDto studentGroupDto) {
        if (studentGroupDto.getId() != null)
            throw new RuntimeException("id должен быть пустым!");
        StudentGroup groupEntity = new StudentGroup();
        groupEntity.setGroupName(studentGroupDto.getGroupName());
        groupEntity.setCreatedAt(LocalDateTime.now());
        groupEntity.setUpdatedAt(LocalDateTime.now());

        return toDto(studentGroupRepository.save(groupEntity));
    }

    @Transactional
    public StudentGroupDto editStudentGroup(Long id, StudentGroupDto studentGroupDto) {
        StudentGroup studentGroup = studentGroupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Группа с id " + id + " не найдена"));
        studentGroup.setGroupName(studentGroupDto.getGroupName());
        studentGroup.setUpdatedAt(LocalDateTime.now());
        return toDto(studentGroup);
    }

    @Transactional
    public void deleteStudentGroup(Long id) {
        if (!studentGroupRepository.existsById(id))
            throw new EntityNotFoundException("Группа с id " + id + " не найдена");
        studentGroupRepository.deleteById(id);
    }

    public StudentGroupDto toDto(StudentGroup groupEntity) {
        return new StudentGroupDto(groupEntity.getId(),
                groupEntity.getGroupName(),
                groupEntity.getCreatedAt(),
                groupEntity.getUpdatedAt());
    }
}
