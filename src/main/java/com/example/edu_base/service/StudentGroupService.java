package com.example.edu_base.service;

import com.example.edu_base.common.ServerException;
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

    public List<StudentGroupDto> getStudentGroups() throws ServerException {
        try {
            return studentGroupRepository.findAll()
                    .stream()
                    .map(this::toDto)
                    .toList();
        } catch (Exception e) {
            log.error("db error in method getStudentGroups");
            throw new ServerException("db error: getStudentGroups", e, 101, null);
        }
    }

    public StudentGroupDto getStudentGroupById(Long id) throws ServerException {
        if (id == null) {
            log.error("error in method StudentGroupService.getStudentGroupById: id = null");
            throw new IllegalArgumentException("id should not be null!");
        }
        try {
            StudentGroup groupEntity = studentGroupRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("student group: " + id + " not found"));
            return toDto(groupEntity);
        } catch (Exception e) {
            throw new ServerException("db error: getStudentGroupById", e, 102, null);
        }
    }

    @Transactional
    public StudentGroupDto addStudentGroup(StudentGroupDto studentGroupDto) throws ServerException {
        if (studentGroupDto.getId() != null) {
            log.error("error in method StudentGroupService.addStudentGroup: id should be null");
            throw new RuntimeException("id should be null!");
        }
        try {
            StudentGroup groupEntity = new StudentGroup();
            groupEntity.setGroupName(studentGroupDto.getGroupName());
            groupEntity.setCreatedAt(LocalDateTime.now());
            groupEntity.setUpdatedAt(LocalDateTime.now());
            return toDto(studentGroupRepository.save(groupEntity));
        } catch (Exception e) {
            log.error("error in method StudentGroupService.addStudentGroup");
            throw new ServerException("db error: addStudentGroup", e, 103, null);
        }
    }

    @Transactional
    public StudentGroupDto editStudentGroup(Long id, StudentGroupDto studentGroupDto) throws ServerException {
        if (id == null) {
            log.error("error in method StudentGroupService.editStudentGroup: id = null");
            throw new IllegalArgumentException("id should not be null!");
        }
        try {
            StudentGroup studentGroup = studentGroupRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("student group: " + id + " not found"));
            studentGroup.setGroupName(studentGroupDto.getGroupName());
            studentGroup.setUpdatedAt(LocalDateTime.now());
            return toDto(studentGroup);
        } catch (Exception e) {
            throw new ServerException("db error: editStudentGroup", e, 104, null);
        }
    }

    @Transactional
    public void deleteStudentGroup(Long id) throws ServerException {
        if (id == null) {
            log.error("error in method StudentGroupService.deleteStudentGroup: id = null");
            throw new IllegalArgumentException("id should not be null!");
        }
        if (!studentGroupRepository.existsById(id)) {
            log.error("db error in method StudentGroupService.deleteStudentGroup: group: {} doesn't exist", id);
            throw new EntityNotFoundException("student group: " + id + " not found");
        }
        try {
            studentGroupRepository.deleteById(id);
        } catch (Exception e) {
            throw new ServerException("db error: deleteStudentGroup", e, 105, null);
        }
    }

    public StudentGroupDto toDto(StudentGroup groupEntity) {
        return new StudentGroupDto(groupEntity.getId(),
                groupEntity.getGroupName(),
                groupEntity.getCreatedAt(),
                groupEntity.getUpdatedAt());
    }

}
