package com.example.edu_base.service.lesson;

import com.example.edu_base.common.ServerException;
import com.example.edu_base.dto.lesson.LessonRequest;
import com.example.edu_base.dto.lesson.LessonResponse;
import com.example.edu_base.entity.Lesson;
import com.example.edu_base.repository.lesson.ILessonRepository;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Service
public class LessonService implements ILessonService {

    private final ILessonRepository lessonRepository;

    public LessonService(ILessonRepository lessonRepository) {
        this.lessonRepository = lessonRepository;
    }

    @Override
    public LessonResponse addLesson(LessonRequest request) throws ServerException {
        if (request.getId() != null) {
            throw new IllegalArgumentException("id should be null!");
        }

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
            throw new ServerException(e.getCause().toString(), e, 203, null);
        }
    }

    @Override
    public LessonResponse editLesson(Long id, LessonRequest request) throws ServerException {
        if (request.getId() == null) {
            throw new IllegalArgumentException("id should not be null!");
        }
        try {
            Lesson lesson = lessonRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("lesson: " + id + " not found"));

            lesson.setSubjectId(request.getSubjectId());
            lesson.setDate(request.getDate());
            lesson.setPairNumber(request.getPairNumber());
            lesson.setTeacherId(request.getTeacherId());
            lesson.setStudentGroupId(request.getStudentGroupId());
            lesson.setUpdatedAt(ZonedDateTime.now(ZoneOffset.UTC));

            lessonRepository.update(lesson);

            return toLessonResponse(lesson);
        } catch (Exception e) {
            throw new ServerException("db error: editLesson()", e, 204, null);
        }
    }

    @Override
    public void deleteLesson(Long id) throws ServerException {
        if (id == null) {
            throw new IllegalArgumentException("id should not be null!");
        }
        boolean deleted = lessonRepository.deleteById(id);
        if (!deleted)
            throw new ServerException("Lesson wasn't delete", 205, null);
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
}
