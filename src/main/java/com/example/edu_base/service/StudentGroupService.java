package com.example.edu_base.service;

import com.example.edu_base.dto.StudentGroupDto;
import com.example.edu_base.entity.StudentGroup;
import com.example.edu_base.repository.StudentGroupRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class StudentGroupService {

    private final StudentGroupRepository studentGroupRepository;

    public StudentGroupService(StudentGroupRepository studentGroupRepository) {
        this.studentGroupRepository = studentGroupRepository;
    }

    public List<StudentGroupDto> getStudentGroups() {
        return studentGroupRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public StudentGroupDto getStudentGroupById(Long id) {
        if (id == null) {
            log.error("Ошибка в методе StudentGroupService.getStudentGroupById: id = null");
            throw new IllegalArgumentException("Id не должно быть пустым!");
        }
        StudentGroup groupEntity = studentGroupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Группа с id " + id + " не найдена"));
        return toDto(groupEntity);
    }

    @Transactional
    public StudentGroupDto addStudentGroup(StudentGroupDto studentGroupDto) {
        if (studentGroupDto.getId() != null) {
            log.error("Ошибка в методе StudentGroupService.addStudentGroup: id должен быть пустым");
            throw new RuntimeException("id должен быть пустым!");
        }
        StudentGroup groupEntity = new StudentGroup();
        groupEntity.setGroupName(studentGroupDto.getGroupName());
        groupEntity.setCreatedAt(LocalDateTime.now());
        groupEntity.setUpdatedAt(LocalDateTime.now());

        return toDto(studentGroupRepository.save(groupEntity));
    }

    @Transactional
    public StudentGroupDto editStudentGroup(Long id, StudentGroupDto studentGroupDto) {
        if (id == null) {
            log.error("Ошибка в методе StudentGroupService.editStudentGroup: id = null");
            throw new IllegalArgumentException("Id не должно быть пустым!");
        }
        StudentGroup studentGroup = studentGroupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Группа с id " + id + " не найдена"));
        studentGroup.setGroupName(studentGroupDto.getGroupName());
        studentGroup.setUpdatedAt(LocalDateTime.now());
        return toDto(studentGroup);
    }

    @Transactional
    public void deleteStudentGroup(Long id) {
        if (id == null) {
            log.error("Ошибка в методе StudentGroupService.deleteStudentGroup: id = null");
            throw new IllegalArgumentException("Id не должно быть пустым!");
        }
        if (!studentGroupRepository.existsById(id)) {
            log.error("Ошибка в методе StudentGroupService.deleteStudentGroup: группы с id: {} не существует", id);
            throw new EntityNotFoundException("Группа с id " + id + " не найдена");
        }
        studentGroupRepository.deleteById(id);
    }

    public StudentGroupDto toDto(StudentGroup groupEntity) {
        return new StudentGroupDto(groupEntity.getId(),
                groupEntity.getGroupName(),
                groupEntity.getCreatedAt(),
                groupEntity.getUpdatedAt());
    }

}
