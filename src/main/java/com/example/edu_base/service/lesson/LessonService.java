package com.example.edu_base.service.lesson;

import com.example.edu_base.dto.lesson.LessonRequest;
import com.example.edu_base.dto.lesson.LessonResponse;
import com.example.edu_base.dto.lesson.LessonWithAttendanceResponse;
import com.example.edu_base.entity.Lesson;
import com.example.edu_base.entity.Student;
import com.example.edu_base.repository.attendance.IAttendanceRepository;
import com.example.edu_base.repository.lesson.ILessonRepository;
import com.example.edu_base.repository.student.IStudentRepository;
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
    private final IStudentRepository studentRepository;
    private final IStudentGroupRepository studentGroupRepository;
    private final ISubjectRepository subjectRepository;
    private final IAttendanceRepository attendanceRepository;

    public LessonService(ILessonRepository lessonRepository,
                         IStudentRepository studentRepository,
                         IStudentGroupRepository studentGroupRepository,
                         ISubjectRepository subjectRepository,
                         IAttendanceRepository attendanceRepository) {
        this.lessonRepository = lessonRepository;
        this.studentRepository = studentRepository;
        this.studentGroupRepository = studentGroupRepository;
        this.subjectRepository = subjectRepository;
        this.attendanceRepository = attendanceRepository;
    }

    @Override
    public LessonResponse addLesson(LessonRequest request) {
        log.info("adding lesson for student group: {}", request.getStudentGroupId());
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
    }

    @Override
    public LessonWithAttendanceResponse getLessonById(long id) {
        log.info("getting lesson by id: {}", id);
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("lesson: " + id + " not found"));

        List<Long> studentIds = studentRepository.findByStudentGroupId(lesson.getStudentGroupId())
                .stream()
                .map(Student::getId)
                .toList();

        List<Pair<String, String>> attendance = new ArrayList<>();
        for (Long studentId : studentIds) {

            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new EntityNotFoundException("student " + studentId + " not found"));

            String middleName = student.getMiddleName() != null ? student.getMiddleName() : "";

            String fullName = studentId + ": " + student.getLastName() + " " + student.getFirstName() + " " + middleName;

            String isPresent = attendanceRepository.findByStudentId(studentId).isPresent() ? "present" : "absent";

            attendance.add(new ImmutablePair<>(fullName, isPresent));
        }

        return toLessonWithAttendanceResponse(lesson, attendance);
    }

    @Override
    public List<LessonResponse> getLessons() {
        log.info("getting all lessons");
        return lessonRepository.findAll().stream()
                .map(this::toLessonResponse)
                .toList();
    }

    @Override
    public LessonResponse editLesson(long id, LessonRequest request) {
        log.info("editing lesson by id: {}", id);
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
    }

    @Override
    public void deleteLesson(long id) {
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

    public LessonWithAttendanceResponse toLessonWithAttendanceResponse(Lesson lesson, List<Pair<String, String>> attendance) {

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
