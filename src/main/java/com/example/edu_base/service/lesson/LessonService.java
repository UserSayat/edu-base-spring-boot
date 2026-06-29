package com.example.edu_base.service.lesson;

import com.example.edu_base.exception.ServerException;
import com.example.edu_base.dto.lesson.LessonRequest;
import com.example.edu_base.dto.lesson.LessonResponse;
import com.example.edu_base.dto.lesson.LessonWithAttendanceResponse;
import com.example.edu_base.entity.Lesson;
import com.example.edu_base.repository.attendance.IAttendanceRepository;
import com.example.edu_base.repository.lesson.ILessonRepository;
import com.example.edu_base.repository.studentGroup.IStudentGroupRepository;
import com.example.edu_base.repository.subject.ISubjectRepository;
import com.example.edu_base.exception.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class LessonService implements ILessonService {

    private final ILessonRepository lessonRepository;
    private final IStudentGroupRepository studentGroupRepository;
    private final ISubjectRepository subjectRepository;
    private final IAttendanceRepository attendanceRepository;

    public LessonService(ILessonRepository lessonRepository,
                         IStudentGroupRepository studentGroupRepository,
                         ISubjectRepository subjectRepository,
                         IAttendanceRepository attendanceRepository) {
        this.lessonRepository = lessonRepository;
        this.studentGroupRepository = studentGroupRepository;
        this.subjectRepository = subjectRepository;
        this.attendanceRepository = attendanceRepository;
    }

    @Override
    public LessonResponse addLesson(LessonRequest request) throws ServerException {
        log.info("adding lesson for student group: {}", request.getStudentGroupId());
        try {
            subjectRepository.findById(request.getSubjectId())
                    .orElseThrow(() -> new EntityNotFoundException("subject with id: " + request.getStudentGroupId() + " not found"));

            studentGroupRepository.findById(request.getStudentGroupId())
                    .orElseThrow(() -> new EntityNotFoundException("student group with id: " + request.getStudentGroupId() + " not found"));

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
            log.error("failed to add lesson for student group: {}", request.getStudentGroupId(), e);
            throw new ServerException(message, e, 5001, null);
        }
    }

    @Override
    public LessonWithAttendanceResponse getLessonById(long id) throws ServerException {
        log.info("getting lesson by id: {}", id);
        try {
            Lesson lesson = lessonRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("lesson: " + id + " not found"));

            List<Long> studentIds = lessonRepository.findStudentsByStudentGroupId(lesson.getStudentGroupId());

            List<Pair<Long, Boolean>> attendance = new ArrayList<>();
            for (Long studentId : studentIds) {
                Boolean isPresent = attendanceRepository.findByStudentId(studentId).isPresent();
                attendance.add(new ImmutablePair<>(studentId, isPresent));
            }

            return toLessonWithAttendanceResponse(lesson, attendance);
        } catch (Exception e) {
            String message = e.getMessage() != null ? e.getMessage() : e.getClass().getName();
            log.error("failed to get lesson: {}", id, e);
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
            log.error("failed to get lessons", e);
            throw new ServerException(message, e, 5003, null);
        }
    }

    @Override
    public LessonResponse editLesson(long id, LessonRequest request) throws ServerException {
        log.info("editing lesson by id: {}", id);
        try {
            Lesson lesson = lessonRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("lesson: " + id + " not found"));

            studentGroupRepository.findById(request.getStudentGroupId())
                    .orElseThrow(() -> new EntityNotFoundException("student group: " + id + " not found"));

            if (lessonRepository.findByDateAndPairNumber(request.getDate(), request.getPairNumber()).isPresent())
                throw new IllegalArgumentException("can not edit lesson, time is busy already");

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
            log.error("failed to edit lesson: {}", id, e);
            throw new ServerException(message, e, 5004, null);
        }
    }

    @Override
    public void deleteLesson(long id) throws ServerException {
        log.info("deleting lesson by id: {}", id);

        lessonRepository.deleteById(id);
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
