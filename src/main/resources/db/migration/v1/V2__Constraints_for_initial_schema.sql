ALTER TABLE students
    ADD CONSTRAINT fk_student_group
    FOREIGN KEY (student_group_id) REFERENCES student_groups(id) ON DELETE RESTRICT;

ALTER TABLE lessons
    ADD CONSTRAINT fk_lesson_subject
    FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE RESTRICT,
    ADD CONSTRAINT fk_lesson_teacher
    FOREIGN KEY (teacher_id) REFERENCES teachers(id) ON DELETE RESTRICT,
    ADD CONSTRAINT fk_lesson_group
    FOREIGN KEY (student_group_id) REFERENCES student_groups(id) ON DELETE RESTRICT;

ALTER TABLE attendances
    ADD CONSTRAINT fk_attendance_lesson
    FOREIGN KEY (lesson_id) REFERENCES lessons(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_attendance_student
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE;

ALTER TABLE attendances
    ADD CONSTRAINT unique_lesson_student
    UNIQUE (lesson_id, student_id);
