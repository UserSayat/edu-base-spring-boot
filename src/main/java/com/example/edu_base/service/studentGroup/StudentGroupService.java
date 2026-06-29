package com.example.edu_base.service.studentGroup;

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
    public StudentGroupResponse addStudentGroup(StudentGroupRequest request) {
        log.info("adding student group: {}", request.getGroupName());
        StudentGroup groupEntity = new StudentGroup(null,
                request.getGroupName(),
                ZonedDateTime.now(ZoneOffset.UTC),
                ZonedDateTime.now(ZoneOffset.UTC));
        return toStudentGroupResponse(studentGroupRepository.save(groupEntity));
    }

    @Override
    public StudentGroupResponse getStudentGroupById(long id) {
        log.info("getting student group by id: {}", id);
        StudentGroup groupEntity = studentGroupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("student group: " + id + " not found"));
        return toStudentGroupResponse(groupEntity);
    }

    @Override
    public List<StudentGroupResponse> getStudentGroups() {
        log.info("getting all student groups");
        return studentGroupRepository.findAll()
                .stream()
                .map(this::toStudentGroupResponse)
                .toList();
    }

    @Override
    public StudentGroupResponse editStudentGroup(long id, StudentGroupRequest request) {
        log.info("editing student group by id: {}", id);
        StudentGroup studentGroup = studentGroupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("student group: " + id + " not found"));

        studentGroup.setGroupName(request.getGroupName());
        studentGroup.setUpdatedAt(ZonedDateTime.now(ZoneOffset.UTC));
        studentGroupRepository.update(studentGroup);

        return toStudentGroupResponse(studentGroup);
    }

    @Override
    public void deleteStudentGroup(long id) {
        log.info("deleting student group by id: {}", id);
        List<Student> students = studentRepository.findByStudentGroupId(id);
        if (!students.isEmpty())
            throw new IllegalArgumentException("group is not empty, can not delete");

        studentGroupRepository.deleteById(id);
    }

    public StudentGroupResponse toStudentGroupResponse(StudentGroup groupEntity) {
        return new StudentGroupResponse(groupEntity.getId(),
                groupEntity.getGroupName(),
                groupEntity.getCreatedAt(),
                groupEntity.getUpdatedAt());
    }

}
