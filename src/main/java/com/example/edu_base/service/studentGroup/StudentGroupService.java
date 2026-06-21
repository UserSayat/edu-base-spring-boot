package com.example.edu_base.service.studentGroup;

import com.example.edu_base.exception.ServerException;
import com.example.edu_base.dto.studentGroup.StudentGroupRequest;
import com.example.edu_base.dto.studentGroup.StudentGroupResponse;
import com.example.edu_base.entity.Student;
import com.example.edu_base.entity.StudentGroup;
import com.example.edu_base.repository.student.IStudentRepository;
import com.example.edu_base.repository.studentGroup.IStudentGroupRepository;
import com.example.edu_base.exception.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@Slf4j
public class StudentGroupService implements IStudentGroupService {

    private final IStudentGroupRepository studentGroupRepository;
    private final IStudentRepository studentRepository;

    public StudentGroupService(IStudentGroupRepository studentGroupRepository, IStudentRepository studentRepository) {
        this.studentGroupRepository = studentGroupRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    public StudentGroupResponse addStudentGroup(StudentGroupRequest request) throws ServerException {
        log.info("adding student group: {}", request.getGroupName());
        try {
            StudentGroup groupEntity = new StudentGroup(null,
                    request.getGroupName(),
                    ZonedDateTime.now(ZoneOffset.UTC),
                    ZonedDateTime.now(ZoneOffset.UTC));
            return toStudentGroupResponse(studentGroupRepository.save(groupEntity));
        } catch (Exception e) {
            String message = e.getMessage() != null ? e.getMessage() : e.getClass().getName();
            log.error("failed to add student group: {}", request.getGroupName(), e);
            throw new ServerException(message, e, 1001, null);
        }
    }

    @Override
    public StudentGroupResponse getStudentGroupById(long id) throws ServerException {
        log.info("getting student group by id: {}", id);
        try {
            StudentGroup groupEntity = studentGroupRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("student group: " + id + " not found"));
            return toStudentGroupResponse(groupEntity);
        } catch (Exception e) {
            String message = e.getMessage() != null ? e.getMessage() : e.getClass().getName();
            log.error("failed to get student group by id: {}", id, e);
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
            log.error("failed to get student groups");
            throw new ServerException(message, e, 1003, null);
        }
    }

    @Override
    public StudentGroupResponse editStudentGroup(long id, StudentGroupRequest request) throws ServerException {
        log.info("editing student group by id: {}", id);
        try {
            StudentGroup studentGroup = studentGroupRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("student group: " + id + " not found"));

            studentGroup.setGroupName(request.getGroupName());
            studentGroup.setUpdatedAt(ZonedDateTime.now(ZoneOffset.UTC));
            studentGroupRepository.update(studentGroup);

            return toStudentGroupResponse(studentGroup);
        } catch (Exception e) {
            String message = e.getMessage() != null ? e.getMessage() : e.getClass().getName();
            log.error("failed to edit student group by id: {}", id, e);
            throw new ServerException(message, e, 1004, null);
        }
    }

    @Override
    public void deleteStudentGroup(long id) throws ServerException {
        log.info("deleting student group by id: {}", id);
        try {
            List<Student> students = studentRepository.findByStudentGroupId(id);
            if (!students.isEmpty())
                throw new IllegalArgumentException("group is not empty, can not delete");

            boolean deleted = studentGroupRepository.deleteById(id);
        } catch (Exception e) {
            throw new ServerException("student group wasn't delete", e, 1005, null);
        }
    }

    public StudentGroupResponse toStudentGroupResponse(StudentGroup groupEntity) {
        return new StudentGroupResponse(groupEntity.getId(),
                groupEntity.getGroupName(),
                groupEntity.getCreatedAt(),
                groupEntity.getUpdatedAt());
    }

}
