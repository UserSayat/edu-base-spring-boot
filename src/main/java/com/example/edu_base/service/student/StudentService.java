package com.example.edu_base.service.student;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import com.example.edu_base.exception.ServerException;
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
    public StudentResponse addStudent(StudentRequest request) throws ServerException {
        log.info("adding student: {} {} {}",
                request.getLastName(),
                request.getFirstName(),
                request.getMiddleName());
        try {
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
        } catch (Exception e) {
            log.error("failed to add student: {} {} {}",
                    request.getLastName(), request.getFirstName(), request.getMiddleName(), e);
            String message = e.getMessage() != null ? e.getMessage() : e.getClass().getName();
            throw new ServerException(message, e, 2001, null);
        }
    }

    @Override
    public StudentResponse getStudentById(long id) throws ServerException {
        log.info("getting student by id: {}", id);
        try {
            Student student = studentRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("student: " + id + " not found"));
            return toStudentResponse(student);
        } catch (Exception e) {
            String message = e.getMessage() != null ? e.getMessage() : e.getClass().getName();
            log.error("failed to get student by id: {}", id, e);
            throw new ServerException(message, e, 2002, null);
        }
    }

    @Override
    public List<StudentResponse> getStudentsByGroup(long id) throws ServerException {
        try {
            return studentRepository.findByStudentGroupId(id)
                    .stream()
                    .map(this::toStudentResponse)
                    .toList();
        } catch (Exception e) {
            String message = e.getMessage() != null ? e.getMessage() : e.getClass().getName();
            log.error("failed to get students");
            throw new ServerException(message, e, 2003, null);
        }
    }

    @Override
    public StudentResponse editStudent(long id, StudentRequest request) throws ServerException {
        log.info("editing student by id: {}", id);
        try {
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
        } catch (Exception e) {
            String message = e.getMessage() != null ? e.getMessage() : e.getClass().getName();
            log.error("failed to edit student by id: {}", id, e);
            throw new ServerException(message, e, 2004, null);
        }
    }

    @Override
    public void deleteStudent(long id) throws ServerException {
        log.info("deleting student by id: {}", id);
        try {
            boolean deletedStudent = studentRepository.deleteById(id);
            attendanceRepository.deleteByStudentId(id);
        } catch (Exception e) {
            throw new ServerException("student wasn't delete", e, 2005, null);
        }
    }

    public StudentResponse toStudentResponse(Student student) {
        StudentGroup studentGroup = studentGroupRepository
                .findById(student.getStudentGroupId())
                .orElseThrow(() -> new EntityNotFoundException("group with id: " + student.getStudentGroupId() + " not found"));


        return new StudentResponse(student.getId(),
                student.getLastName(),
                student.getFirstName(),
                student.getMiddleName(),
                student.getStatus(),
                new StudentGroupResponse(studentGroup.getId(),
                        studentGroup.getGroupName(),
                        studentGroup.getCreatedAt(),
                        studentGroup.getUpdatedAt()),
                student.getCreatedAt(),
                student.getUpdatedAt());
    }
}
