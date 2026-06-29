package com.example.edu_base.service.student;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import com.example.edu_base.dto.studentGroup.StudentGroupResponse;
import com.example.edu_base.dto.student.StudentRequest;
import com.example.edu_base.dto.student.StudentResponse;
import com.example.edu_base.entity.Student;
import com.example.edu_base.entity.StudentGroup;
import com.example.edu_base.repository.attendance.IAttendanceRepository;
import com.example.edu_base.repository.studentGroup.IStudentGroupRepository;
import com.example.edu_base.repository.student.IStudentRepository;
import com.example.edu_base.exception.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class StudentService implements IStudentService {

    private final IStudentRepository studentRepository;
    private final IStudentGroupRepository studentGroupRepository;
    private final IAttendanceRepository attendanceRepository;

    public StudentService(IStudentRepository studentRepository,
                          IStudentGroupRepository studentGroupRepository,
                          IAttendanceRepository attendanceRepository) {
        this.studentRepository = studentRepository;
        this.studentGroupRepository = studentGroupRepository;
        this.attendanceRepository = attendanceRepository;
    }

    @Override
    public StudentResponse addStudent(StudentRequest request) {
        log.info("adding student: {} {} {}",
                request.getLastName(),
                request.getFirstName(),
                request.getMiddleName());

        studentGroupRepository.findById(request.getStudentGroupId())
                .orElseThrow(() -> new EntityNotFoundException("group with id:" + request.getStudentGroupId() + " not found"));

        Student student = new Student(null,
                request.getLastName(),
                request.getFirstName(),
                request.getMiddleName(),
                request.getStudentStatus(),
                request.getStudentGroupId(),
                ZonedDateTime.now(ZoneOffset.UTC),
                ZonedDateTime.now(ZoneOffset.UTC));
        return toStudentResponse(studentRepository.save(student));
    }

    @Override
    public StudentResponse getStudentById(long id) {
        log.info("getting student by id: {}", id);
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("student: " + id + " not found"));
        return toStudentResponse(student);
    }

    @Override
    public List<StudentResponse> getStudentsByGroup(long id) {
        log.info("getting students by group: {}", id);
        return studentRepository.findByStudentGroupId(id)
                .stream()
                .map(this::toStudentResponse)
                .toList();
    }

    @Override
    public StudentResponse editStudent(long id, StudentRequest request) {
        log.info("editing student by id: {}", id);
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("student: " + id + " not found"));

        studentGroupRepository.findById(request.getStudentGroupId())
                .orElseThrow(() -> new EntityNotFoundException("group with id: " + request.getStudentGroupId() + " not found"));

        student.setLastName(request.getLastName());
        student.setFirstName(request.getFirstName());
        student.setMiddleName(request.getMiddleName());
        student.setStudentGroupId(request.getStudentGroupId());
        student.setStatus(request.getStudentStatus());
        student.setUpdatedAt(ZonedDateTime.now(ZoneOffset.UTC));

        studentRepository.update(student);

        return toStudentResponse(student);
    }

    @Transactional
    @Override
    public void deleteStudent(long id) {
        log.info("deleting student by id: {}", id);

        studentRepository.deleteById(id);

        attendanceRepository.deleteByStudentId(id);
    }

    public StudentResponse toStudentResponse(Student student) {
        StudentGroup studentGroup = studentGroupRepository
                .findById(student.getStudentGroupId())
                .orElseThrow(() -> new EntityNotFoundException("group with id: " + student.getStudentGroupId() + " not found"));


        return new StudentResponse(student.getId(),
                student.getLastName(),
                student.getFirstName(),
                student.getMiddleName(),
                student.getStudentStatus(),
                new StudentGroupResponse(studentGroup.getId(),
                        studentGroup.getGroupName(),
                        studentGroup.getCreatedAt(),
                        studentGroup.getUpdatedAt()),
                student.getCreatedAt(),
                student.getUpdatedAt());
    }
}
