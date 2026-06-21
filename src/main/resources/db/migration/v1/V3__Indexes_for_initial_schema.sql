CREATE INDEX idx_lesson_date ON lessons(date);

CREATE INDEX idx_lesson_teacher_date ON lessons(teacher_id, date);

CREATE INDEX idx_lesson_group_date ON lessons(student_group_id, date);

CREATE INDEX idx_student_group ON students(student_group_id);

CREATE INDEX idx_attendance_lesson ON attendances(lesson_id);