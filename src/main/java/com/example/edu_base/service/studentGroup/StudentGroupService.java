package com.example.edu_base.service.studentGroup;

import com.example.edu_base.common.ServerException;
import com.example.edu_base.dto.studentGroup.StudentGroupRequest;
import com.example.edu_base.dto.studentGroup.StudentGroupResponse;
import com.example.edu_base.entity.StudentGroup;
import com.example.edu_base.repository.studentGroup.IStudentGroupRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@Slf4j
public class StudentGroupService implements IStudentGroupService {

    private final IStudentGroupRepository studentGroupRepository;

    public StudentGroupService(IStudentGroupRepository studentGroupRepository) {
        this.studentGroupRepository = studentGroupRepository;
    }

    public List<StudentGroupResponse> getStudentGroups() throws ServerException {
        try {
            return studentGroupRepository.findAll()
                    .stream()
                    .map(this::toResponse)
                    .toList();
        } catch (Exception e) {
            log.error("db error in method getStudentGroups");
            throw new ServerException("db error: getStudentGroups", e, 101, null);
        }
    }

    public StudentGroupResponse getStudentGroupById(Long id) throws ServerException {
        if (id == null) {
            log.error("error in method StudentGroupService.getStudentGroupById: id = null");
            throw new IllegalArgumentException("id should not be null!");
        }
        try {
            StudentGroup groupEntity = studentGroupRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("student group: " + id + " not found"));
            return toResponse(groupEntity);
        } catch (Exception e) {
            throw new ServerException("db error: getStudentGroupById", e, 102, null);
        }
    }

    public StudentGroupResponse addStudentGroup(StudentGroupRequest request) throws ServerException {
        if (request.getId() != null) {
            log.error("error in method StudentGroupService.addStudentGroup: id should be null");
            throw new RuntimeException("id should be null!");
        }
        try {
            StudentGroup groupEntity = new StudentGroup();
            groupEntity.setGroupName(request.getGroupName());
            groupEntity.setCreatedAt(ZonedDateTime.now(ZoneOffset.UTC));
            groupEntity.setUpdatedAt(ZonedDateTime.now(ZoneOffset.UTC));
            return toResponse(studentGroupRepository.save(groupEntity));
        } catch (Exception e) {
            log.error("error in method StudentGroupService.addStudentGroup");
            throw new ServerException("db error: addStudentGroup", e, 103, null);
        }
    }

    public StudentGroupResponse editStudentGroup(Long id, StudentGroupRequest request) throws ServerException {
        if (id == null) {
            log.error("error in method StudentGroupService.editStudentGroup: id = null");
            throw new IllegalArgumentException("id should not be null!");
        }
        try {
            StudentGroup studentGroup = studentGroupRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("student group: " + id + " not found"));
            studentGroup.setGroupName(request.getGroupName());
            studentGroup.setUpdatedAt(ZonedDateTime.now(ZoneOffset.UTC));
            studentGroupRepository.update(studentGroup);

            return toResponse(studentGroup);
        } catch (Exception e) {
            throw new ServerException("db error: editStudentGroup", e, 104, null);
        }
    }

    public void deleteStudentGroup(Long id) throws ServerException {
        if (id == null) {
            log.error("error in method StudentGroupService.deleteStudentGroup: id = null");
            throw new IllegalArgumentException("id should not be null!");
        }
        boolean deleted = studentGroupRepository.deleteById(id);
        if (!deleted)
            throw new ServerException("Student group wasn't delete", 105, null);
    }

    public StudentGroupResponse toResponse(StudentGroup groupEntity) {
        return new StudentGroupResponse(groupEntity.getId(),
                groupEntity.getGroupName(),
                groupEntity.getCreatedAt(),
                groupEntity.getUpdatedAt());
    }

}
