CREATE TABLE IF NOT EXISTS `student_groups` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `group_name` VARCHAR(20) NOT NULL UNIQUE,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE IF NOT EXISTS `students` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `last_name` VARCHAR(20) NOT NULL,
    `first_name` VARCHAR(20) NOT NULL,
    `middle_name` VARCHAR(20) NOT NULL,
    `student_status` VARCHAR(20) NOT NULL UNIQUE,
    `student_group_id` BIGINT NOT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE IF NOT EXISTS `teachers` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `last_name` VARCHAR(20) NOT NULL,
    `first_name` VARCHAR(20) NOT NULL,
    `middle_name` VARCHAR(20) NOT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE IF NOT EXISTS `subjects` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `subject_name` VARCHAR(20) NOT NULL UNIQUE,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE IF NOT EXISTS `lessons` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `subject_id` BIGINT NOT NULL,
    `date` DATE NOT NULL,
    `pair_number` INT NOT NULL CHECK (`pair_number` BETWEEN 1 AND 8),
    `teacher_id` BIGINT NOT NULL,
    `student_group_id` BIGINT NOT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE IF NOT EXISTS `attendances` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `lesson_id` BIGINT NOT NULL,
    `student_id` BIGINT NOT NULL,
    `is_present` BOOLEAN DEFAULT FALSE,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE `students`
    ADD CONSTRAINT `fk_student_group`
    FOREIGN KEY (`student_group_id`) REFERENCES `groups`(`id`) ON DELETE RESTRICT;

ALTER TABLE `lessons`
    ADD CONSTRAINT `fk_lesson_subject`
    FOREIGN KEY (`subject_id`) REFERENCES `disciplines`(`id`) ON DELETE RESTRICT,
    ADD CONSTRAINT `fk_lesson_teacher`
    FOREIGN KEY (`teacher_id`) REFERENCES `teachers`(`id`) ON DELETE RESTRICT,
    ADD CONSTRAINT `fk_lesson_group`
    FOREIGN KEY (`student_group_id`) REFERENCES `groups`(`id`) ON DELETE RESTRICT;

ALTER TABLE `attendance`
    ADD CONSTRAINT `fk_attendance_lesson`
    FOREIGN KEY (`lesson_id`) REFERENCES `lessons`(`id`) ON DELETE CASCADE,
    ADD CONSTRAINT `fk_attendance_student`
    FOREIGN KEY (`student_id`) REFERENCES `students`(`id`) ON DELETE CASCADE;

-- 8. Уникальность посещаемости
ALTER TABLE `attendance`
    ADD CONSTRAINT `unique_lesson_student`
    UNIQUE (`lesson_id`, `student_id`);


CREATE INDEX `idx_lesson_date` ON `lessons`(`date`);
CREATE INDEX `idx_lesson_teacher_date` ON `lessons`(`teacher_id`, `date`);
CREATE INDEX `idx_lesson_group_date` ON `lessons`(`group_id`, `date`);
CREATE INDEX `idx_student_group` ON `students`(`group_id`);
CREATE INDEX `idx_attendance_lesson` ON `attendance`(`lesson_id`);