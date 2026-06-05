package com.example.edu_base.service.lesson;

import com.example.edu_base.common.ServerException;
import com.example.edu_base.dto.lesson.LessonRequest;
import com.example.edu_base.dto.lesson.LessonResponse;
import com.example.edu_base.dto.lesson.LessonWithAttendanceResponse;
import com.example.edu_base.entity.Lesson;
import com.example.edu_base.entity.Student;
import com.example.edu_base.repository.attendance.IAttendanceRepository;
import com.example.edu_base.repository.lesson.ILessonRepository;
import com.example.edu_base.repository.student.IStudentRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class LessonService implements ILessonService {

    private final ILessonRepository lessonRepository;
    private final IStudentRepository studentRepository;
    private final IAttendanceRepository attendanceRepository;

    public LessonService(ILessonRepository lessonRepository,
                         IStudentRepository studentRepository,
                         IAttendanceRepository attendanceRepository) {
        this.lessonRepository = lessonRepository;
        this.studentRepository = studentRepository;
        this.attendanceRepository = attendanceRepository;
    }

    @Override
    public LessonResponse addLesson(LessonRequest request) throws ServerException {
        try {
            Lesson lesson = new Lesson(null,
                    request.getSubjectId(),
                    request.getDate(),
                    request.getPairNumber(),
                    request.getTeacherId(),
                    request.getStudentGroupId(),
                    ZonedDateTime.now(ZoneOffset.UTC),
                    ZonedDateTime.now(ZoneOffset.UTC));

            return toLessonResponse(lessonRepository.save(lesson));
        } catch (Exception e) {
            String message = e.getMessage() != null ? e.getMessage() : e.getClass().getName();
            throw new ServerException(message, e, 5001, null);
        }
    }

    @Override
    public LessonWithAttendanceResponse getLessonById(Long id) throws ServerException {
        if (id == null) {
            throw new ValidationException("id should not be null!");
        }
        try {
            Lesson lesson = lessonRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("lesson: " + id + " not found"));

            List<Long> studentIds = studentRepository.findByStudentGroupId(lesson.getStudentGroupId())
                    .stream()
                    .map(Student::getId)
                    .toList();

            List<Pair<Long, Boolean>> attendance = new ArrayList<>();
            for (Long studentId : studentIds) {
                Boolean isPresent = attendanceRepository.findByStudentId(studentId).isPresent();
                attendance.add(new Pair<>(studentId, isPresent));
            }

            return toLessonWithAttendanceResponse(lesson, attendance);
        } catch (Exception e) {
            String message = e.getMessage() != null ? e.getMessage() : e.getClass().getName();
            throw new ServerException(message, e, 5002, null);
        }
    }

    @Override
    public List<LessonResponse> getLessons() throws ServerException {
        try {
            return lessonRepository.findAll().stream()
                    .map(this::toLessonResponse)
                    .toList();
        } catch (Exception e) {
            String message = e.getMessage() != null ? e.getMessage() : e.getClass().getName();
            throw new ServerException(message, e, 5003, null);
        }
    }

    @Override
    public LessonResponse editLesson(Long id, LessonRequest request) throws ServerException {
        if (id == null) {
            throw new ValidationException("id should not be null!");
        }
        try {
            Lesson lesson = lessonRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("lesson: " + id + " not found"));

            lesson.setSubjectId(request.getSubjectId());
            lesson.setDate(request.getDate());
            lesson.setPairNumber(request.getPairNumber());
            lesson.setTeacherId(request.getTeacherId());
            lesson.setStudentGroupId(request.getStudentGroupId());
            lesson.setUpdatedAt(ZonedDateTime.now(ZoneOffset.UTC));

            lessonRepository.update(lesson);

            return toLessonResponse(lesson);
        } catch (Exception e) {
            String message = e.getMessage() != null ? e.getMessage() : e.getClass().getName();
            throw new ServerException(message, e, 5004, null);
        }
    }

    @Override
    public void deleteLesson(Long id) throws ServerException {
        if (id == null) {
            throw new ValidationException("id should not be null!");
        }
        boolean deleted = lessonRepository.deleteById(id);
        if (!deleted)
            throw new ServerException("lesson wasn't delete", 5005, null);
    }



    public LessonResponse toLessonResponse(Lesson lesson) {
        return new LessonResponse(lesson.getId(),
                lesson.getSubjectId(),
                lesson.getDate(),
                lesson.getPairNumber(),
                lesson.getTeacherId(),
                lesson.getStudentGroupId(),
                lesson.getCreatedAt(),
                lesson.getUpdatedAt());
    }

    public LessonWithAttendanceResponse toLessonWithAttendanceResponse(Lesson lesson, List<Pair<Long, Boolean>> attendance) {
        return new LessonWithAttendanceResponse(lesson.getId(),
                lesson.getSubjectId(),
                lesson.getDate(),
                lesson.getPairNumber(),
                lesson.getTeacherId(),
                lesson.getStudentGroupId(),
                attendance,
                lesson.getCreatedAt(),
                lesson.getUpdatedAt());
    }
}
