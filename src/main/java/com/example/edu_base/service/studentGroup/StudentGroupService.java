package com.example.edu_base.service.studentGroup;

import com.example.edu_base.common.ServerException;
import com.example.edu_base.dto.studentGroup.StudentGroupRequest;
import com.example.edu_base.dto.studentGroup.StudentGroupResponse;
import com.example.edu_base.entity.StudentGroup;
import com.example.edu_base.repository.studentGroup.IStudentGroupRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
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

    @Override
    public StudentGroupResponse addStudentGroup(StudentGroupRequest request) throws ServerException {
        try {
            StudentGroup groupEntity = new StudentGroup(null,
                    request.getGroupName(),
                    ZonedDateTime.now(ZoneOffset.UTC),
                    ZonedDateTime.now(ZoneOffset.UTC));
            return toStudentGroupResponse(studentGroupRepository.save(groupEntity));
        } catch (Exception e) {
            String message = e.getMessage() != null ? e.getMessage() : e.getClass().getName();
            throw new ServerException(message, e, 1001, null);
        }
    }

    @Override
    public StudentGroupResponse getStudentGroupById(Long id) throws ServerException {
        if (id == null) {
            throw new ValidationException("id should not be null!");
        }
        try {
            StudentGroup groupEntity = studentGroupRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("student group: " + id + " not found"));
            return toStudentGroupResponse(groupEntity);
        } catch (Exception e) {
            String message = e.getMessage() != null ? e.getMessage() : e.getClass().getName();
            throw new ServerException(message, e, 1002, null);
        }
    }

    @Override
    public List<StudentGroupResponse> getStudentGroups() throws ServerException {
        try {
            return studentGroupRepository.findAll()
                    .stream()
                    .map(this::toStudentGroupResponse)
                    .toList();
        } catch (Exception e) {
            String message = e.getMessage() != null ? e.getMessage() : e.getClass().getName();
            throw new ServerException(message, e, 1003, null);
        }
    }

    @Override
    public StudentGroupResponse editStudentGroup(Long id, StudentGroupRequest request) throws ServerException {
        if (id == null) {
            throw new ValidationException("id should not be null!");
        }
        try {
            StudentGroup studentGroup = studentGroupRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("student group: " + id + " not found"));

            studentGroup.setGroupName(request.getGroupName());
            studentGroup.setUpdatedAt(ZonedDateTime.now(ZoneOffset.UTC));
            studentGroupRepository.update(studentGroup);

            return toStudentGroupResponse(studentGroup);
        } catch (Exception e) {
            String message = e.getMessage() != null ? e.getMessage() : e.getClass().getName();
            throw new ServerException(message, e, 1004, null);
        }
    }

    @Override
    public void deleteStudentGroup(Long id) throws ServerException {
        if (id == null) {
            throw new ValidationException("id should not be null!");
        }
        boolean deleted = studentGroupRepository.deleteById(id);
        if (!deleted)
            throw new ServerException("student group wasn't delete", 1005, null);
    }

    public StudentGroupResponse toStudentGroupResponse(StudentGroup groupEntity) {
        return new StudentGroupResponse(groupEntity.getId(),
                groupEntity.getGroupName(),
                groupEntity.getCreatedAt(),
                groupEntity.getUpdatedAt());
    }

}
