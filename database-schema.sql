-- =====================================================================
-- MediAI – AI-Powered Smart Healthcare Management System
-- Database Schema Definition & Initial Seed Data for MySQL 8.0
-- =====================================================================

CREATE DATABASE IF NOT EXISTS `mediai_db` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `mediai_db`;

-- Drop existing tables in reverse dependency order if resetting
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS `notifications`;
DROP TABLE IF EXISTS `hospital_resources`;
DROP TABLE IF EXISTS `treatment_recommendations`;
DROP TABLE IF EXISTS `ai_assessments`;
DROP TABLE IF EXISTS `vital_readings`;
DROP TABLE IF EXISTS `medical_records`;
DROP TABLE IF EXISTS `doctor_availability`;
DROP TABLE IF EXISTS `appointments`;
DROP TABLE IF EXISTS `patients`;
DROP TABLE IF EXISTS `doctors`;
DROP TABLE IF EXISTS `departments`;
DROP TABLE IF EXISTS `user_roles`;
DROP TABLE IF EXISTS `users`;
DROP TABLE IF EXISTS `roles`;
SET FOREIGN_KEY_CHECKS = 1;

-- ---------------------------------------------------------------------
-- 1. ROLES TABLE
-- ---------------------------------------------------------------------
CREATE TABLE `roles` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(50) NOT NULL UNIQUE,
    `description` VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `roles` (`name`, `description`) VALUES
('ROLE_ADMIN', 'Hospital Executive Administrator with full system oversight'),
('ROLE_DOCTOR', 'Licensed Physician / Medical Specialist with clinical authority'),
('ROLE_PATIENT', 'Registered Patient with personal health record access');

-- ---------------------------------------------------------------------
-- 2. USERS TABLE
-- ---------------------------------------------------------------------
CREATE TABLE `users` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `email` VARCHAR(150) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `first_name` VARCHAR(100) NOT NULL,
    `last_name` VARCHAR(100) NOT NULL,
    `phone` VARCHAR(30),
    `enabled` BOOLEAN NOT NULL DEFAULT TRUE,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- 3. USER_ROLES JOIN TABLE
-- ---------------------------------------------------------------------
CREATE TABLE `user_roles` (
    `user_id` BIGINT NOT NULL,
    `role_id` BIGINT NOT NULL,
    PRIMARY KEY (`user_id`, `role_id`),
    CONSTRAINT `fk_user_roles_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_user_roles_role` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- 4. DEPARTMENTS TABLE
-- ---------------------------------------------------------------------
CREATE TABLE `departments` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(120) NOT NULL UNIQUE,
    `code` VARCHAR(20) NOT NULL UNIQUE,
    `description` TEXT,
    `head_doctor_name` VARCHAR(150),
    `bed_capacity` INT DEFAULT 30,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `departments` (`name`, `code`, `description`, `head_doctor_name`, `bed_capacity`) VALUES
('Cardiology', 'CARD', 'Comprehensive coronary diagnosis, telemetry monitoring, angiography, and heart disease intervention.', 'Dr. Marcus Vance', 50),
('Neurology', 'NEUR', 'Advanced neuroimaging, cerebrovascular stroke intervention, migraine triage, and epilepsy treatment.', 'Dr. Elena Rostova', 40),
('Pediatrics', 'PEDI', 'Neonatal intensive care, pediatric cardiology, developmental wellness, and acute care.', 'Dr. Sarah Jenkins', 45),
('Orthopedics', 'ORTH', 'Arthroplasty, joint reconstruction, spinal surgery, sports medicine, and trauma stabilization.', 'Dr. Arthur Pendelton', 55),
('General Medicine', 'GENM', 'Acute adult internal medicine, multi-system diagnosis, preventive wellness, and chronic illness care.', 'Dr. Robert Lang', 60),
('Oncology', 'ONCO', 'Comprehensive cancer care, targeted immunotherapy, precision chemotherapy, and survivorship.', 'Dr. Maria Santos', 35);

-- ---------------------------------------------------------------------
-- 5. DOCTORS TABLE
-- ---------------------------------------------------------------------
CREATE TABLE `doctors` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL UNIQUE,
    `department_id` BIGINT,
    `specialization` VARCHAR(150) NOT NULL,
    `qualification` VARCHAR(200) NOT NULL,
    `experience_years` INT NOT NULL DEFAULT 1,
    `consultation_fee` DECIMAL(10, 2) NOT NULL DEFAULT 100.00,
    `room_number` VARCHAR(50),
    `bio` TEXT,
    `rating` DOUBLE NOT NULL DEFAULT 5.0,
    `available_days` VARCHAR(150) DEFAULT 'Mon - Fri',
    `profile_image_url` VARCHAR(500),
    CONSTRAINT `fk_doctors_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_doctors_department` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- 6. PATIENTS TABLE
-- ---------------------------------------------------------------------
CREATE TABLE `patients` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL UNIQUE,
    `assigned_doctor_id` BIGINT,
    `date_of_birth` DATE,
    `gender` VARCHAR(20),
    `blood_group` VARCHAR(10),
    `emergency_contact_name` VARCHAR(120),
    `emergency_contact_phone` VARCHAR(30),
    `address` VARCHAR(255),
    `medical_history_summary` TEXT,
    `allergies` TEXT,
    `chronic_conditions` TEXT,
    CONSTRAINT `fk_patients_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_patients_doctor` FOREIGN KEY (`assigned_doctor_id`) REFERENCES `doctors` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- 7. APPOINTMENTS TABLE (WITH DOUBLE-BOOKING PREVENTION UNIQUE CONSTRAINT)
-- ---------------------------------------------------------------------
CREATE TABLE `appointments` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `patient_id` BIGINT NOT NULL,
    `doctor_id` BIGINT NOT NULL,
    `appointment_date` DATE NOT NULL,
    `appointment_time` VARCHAR(20) NOT NULL,
    `meeting_type` VARCHAR(30) NOT NULL DEFAULT 'in-person',
    `status` VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    `symptoms` TEXT,
    `doctor_notes` TEXT,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_apt_patient` FOREIGN KEY (`patient_id`) REFERENCES `patients` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_apt_doctor` FOREIGN KEY (`doctor_id`) REFERENCES `doctors` (`id`) ON DELETE CASCADE,
    CONSTRAINT `uk_doctor_slot_conflict` UNIQUE (`doctor_id`, `appointment_date`, `appointment_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- 8. DOCTOR_AVAILABILITY TABLE
-- ---------------------------------------------------------------------
CREATE TABLE `doctor_availability` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `doctor_id` BIGINT NOT NULL,
    `day_of_week` VARCHAR(20) NOT NULL,
    `start_time` VARCHAR(10) NOT NULL,
    `end_time` VARCHAR(10) NOT NULL,
    `is_available` BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT `fk_avail_doctor` FOREIGN KEY (`doctor_id`) REFERENCES `doctors` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- 9. MEDICAL_RECORDS TABLE
-- ---------------------------------------------------------------------
CREATE TABLE `medical_records` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `patient_id` BIGINT NOT NULL,
    `doctor_id` BIGINT,
    `record_type` VARCHAR(80) NOT NULL,
    `diagnosis` VARCHAR(255) NOT NULL,
    `treatment_plan` TEXT,
    `prescriptions` TEXT,
    `lab_results` TEXT,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_medrec_patient` FOREIGN KEY (`patient_id`) REFERENCES `patients` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_medrec_doctor` FOREIGN KEY (`doctor_id`) REFERENCES `doctors` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- 10. VITAL_READINGS TABLE (TELEMETRY)
-- ---------------------------------------------------------------------
CREATE TABLE `vital_readings` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `patient_id` BIGINT NOT NULL,
    `heart_rate` INT NOT NULL,
    `blood_pressure_systolic` INT NOT NULL,
    `blood_pressure_diastolic` INT NOT NULL,
    `body_temperature` DOUBLE NOT NULL,
    `oxygen_saturation` INT NOT NULL,
    `status` VARCHAR(30) NOT NULL DEFAULT 'NORMAL',
    `reading_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_vitals_patient` FOREIGN KEY (`patient_id`) REFERENCES `patients` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- 11. AI_ASSESSMENTS TABLE
-- ---------------------------------------------------------------------
CREATE TABLE `ai_assessments` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `patient_id` BIGINT NOT NULL,
    `reported_symptoms` TEXT NOT NULL,
    `primary_condition` VARCHAR(200) NOT NULL,
    `differential_diagnoses` TEXT,
    `confidence_score` DOUBLE NOT NULL DEFAULT 0.85,
    `urgency_level` VARCHAR(40) NOT NULL DEFAULT 'ROUTINE',
    `reasoning` TEXT,
    `suggested_actions` TEXT,
    `reviewed_by_doctor` BOOLEAN NOT NULL DEFAULT FALSE,
    `doctor_id` BIGINT,
    `doctor_notes` TEXT,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_ai_patient` FOREIGN KEY (`patient_id`) REFERENCES `patients` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_ai_doctor` FOREIGN KEY (`doctor_id`) REFERENCES `doctors` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- 12. TREATMENT_RECOMMENDATIONS TABLE
-- ---------------------------------------------------------------------
CREATE TABLE `treatment_recommendations` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `patient_id` BIGINT NOT NULL,
    `doctor_id` BIGINT,
    `title` VARCHAR(200) NOT NULL,
    `diagnosis_condition` VARCHAR(200) NOT NULL,
    `lifestyle_modifications` TEXT,
    `medication_classes` TEXT,
    `follow_up_schedule` VARCHAR(200),
    `approved_by_doctor` BOOLEAN NOT NULL DEFAULT FALSE,
    `doctor_notes` TEXT,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_treat_patient` FOREIGN KEY (`patient_id`) REFERENCES `patients` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_treat_doctor` FOREIGN KEY (`doctor_id`) REFERENCES `doctors` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- 13. HOSPITAL_RESOURCES TABLE
-- ---------------------------------------------------------------------
CREATE TABLE `hospital_resources` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `resource_name` VARCHAR(150) NOT NULL,
    `category` VARCHAR(80) NOT NULL,
    `total_capacity` INT NOT NULL DEFAULT 0,
    `available_count` INT NOT NULL DEFAULT 0,
    `location` VARCHAR(150),
    `status` VARCHAR(40) NOT NULL DEFAULT 'AVAILABLE',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `hospital_resources` (`resource_name`, `category`, `total_capacity`, `available_count`, `location`, `status`) VALUES
('General Inpatient Beds', 'Beds', 200, 55, 'Wings A, B, C', 'AVAILABLE'),
('Critical Care ICU Units', 'ICU', 40, 11, 'Tower 2 - Floor 4', 'LIMITED'),
('Advanced Mechanical Ventilators', 'Ventilator', 30, 6, 'ICU & Emergency', 'LIMITED'),
('Rapid ALS Ambulances', 'Emergency Transport', 12, 5, 'Fleet Bay 1-12', 'AVAILABLE'),
('High-Flow Oxygen Cylinders (40L)', 'Respiratory', 100, 38, 'Central Medical Gas Depot', 'AVAILABLE');

-- ---------------------------------------------------------------------
-- 14. NOTIFICATIONS TABLE
-- ---------------------------------------------------------------------
CREATE TABLE `notifications` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL,
    `title` VARCHAR(200) NOT NULL,
    `message` TEXT NOT NULL,
    `notification_type` VARCHAR(50) DEFAULT 'INFO',
    `is_read` BOOLEAN NOT NULL DEFAULT FALSE,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_notif_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================================
-- SEED USERS & CREDENTIALS
-- Passwords below are pre-hashed for 'password123' via BCrypt:
-- $2a$10$wN1i72hA8zH1QZ4nJ0l1geR9l6lZpQeR0pM.1oY5D2qZ3K8GvCkW.
-- =====================================================================

INSERT INTO `users` (`id`, `email`, `password`, `first_name`, `last_name`, `phone`, `enabled`) VALUES
(1, 'admin@mediai.health', '$2a$10$wN1i72hA8zH1QZ4nJ0l1geR9l6lZpQeR0pM.1oY5D2qZ3K8GvCkW.', 'Sarah', 'Jenkins', '+1 (555) 010-0001', 1),
(2, 'dr.marcus@mediai.health', '$2a$10$wN1i72hA8zH1QZ4nJ0l1geR9l6lZpQeR0pM.1oY5D2qZ3K8GvCkW.', 'Marcus', 'Vance', '+1 (555) 010-0002', 1),
(3, 'dr.elena@mediai.health', '$2a$10$wN1i72hA8zH1QZ4nJ0l1geR9l6lZpQeR0pM.1oY5D2qZ3K8GvCkW.', 'Elena', 'Rostova', '+1 (555) 010-0003', 1),
(4, 'dr.chloe@mediai.health', '$2a$10$wN1i72hA8zH1QZ4nJ0l1geR9l6lZpQeR0pM.1oY5D2qZ3K8GvCkW.', 'Chloe', 'Dubois', '+1 (555) 010-0004', 1),
(5, 'dr.rajesh@mediai.health', '$2a$10$wN1i72hA8zH1QZ4nJ0l1geR9l6lZpQeR0pM.1oY5D2qZ3K8GvCkW.', 'Rajesh', 'Patel', '+1 (555) 010-0005', 1),
(6, 'patient@mediai.health', '$2a$10$wN1i72hA8zH1QZ4nJ0l1geR9l6lZpQeR0pM.1oY5D2qZ3K8GvCkW.', 'Alexander', 'Hayes', '+1 (555) 234-5678', 1),
(7, 'patient2@mediai.health', '$2a$10$wN1i72hA8zH1QZ4nJ0l1geR9l6lZpQeR0pM.1oY5D2qZ3K8GvCkW.', 'Sophia', 'Martinez', '+1 (555) 345-6789', 1),
(8, 'patient3@mediai.health', '$2a$10$wN1i72hA8zH1QZ4nJ0l1geR9l6lZpQeR0pM.1oY5D2qZ3K8GvCkW.', 'Liam', 'Chen', '+1 (555) 456-7890', 1);

-- Map user roles
INSERT INTO `user_roles` (`user_id`, `role_id`) VALUES
(1, 1), -- Admin
(2, 2), -- Dr Vance (ROLE_DOCTOR)
(3, 2), -- Dr Rostova (ROLE_DOCTOR)
(4, 2), -- Dr Jenkins (ROLE_DOCTOR)
(5, 2), -- Dr Pendelton (ROLE_DOCTOR)
(6, 3), -- Alexander Hayes (ROLE_PATIENT)
(7, 3), -- Sophia Martinez (ROLE_PATIENT)
(8, 3); -- Liam Chen (ROLE_PATIENT)

-- Doctors details
INSERT INTO `doctors` (`id`, `user_id`, `department_id`, `specialization`, `qualification`, `experience_years`, `consultation_fee`, `room_number`, `bio`, `rating`, `available_days`, `profile_image_url`) VALUES
(1, 2, 1, 'Interventional Cardiology', 'MD, FACC, Harvard Medical School', 14, 150.00, 'Room 304', 'Senior Interventional Cardiologist specializing in acute coronary syndromes, cardiac catheterization, rhythm disorders, and AI-assisted preventive telemetry.', 4.9, 'Mon - Fri (09:00 - 17:00)', 'https://images.unsplash.com/photo-1622253692010-333f2da6031d?auto=format&fit=crop&w=400&q=80'),
(2, 3, 2, 'Neurology & Neurovascular Care', 'MD, PhD, Johns Hopkins Medicine', 12, 160.00, 'Room 412', 'Chief of Clinical Neurology with clinical expertise in cerebrovascular stroke management, intractable migraine therapies, and neuro-telemetry.', 4.9, 'Mon - Thu (10:00 - 18:00)', 'https://images.unsplash.com/photo-1594824813589-22c608f614ec?auto=format&fit=crop&w=400&q=80'),
(3, 4, 3, 'Pediatrics & Adolescent Medicine', 'MD, FAAP, Stanford Medicine', 10, 120.00, 'Room 108', 'Pediatric specialist dedicated to child health, preventive immunization, childhood asthma management, and family health education.', 4.8, 'Tue - Sat (08:30 - 16:30)', 'https://images.unsplash.com/photo-1559839734-2b71ea197ec2?auto=format&fit=crop&w=400&q=80'),
(4, 5, 4, 'Orthopedic Surgery & Sports Medicine', 'MD, FAAOS, Mayo Clinic', 16, 175.00, 'Room 215', 'Orthopedic surgeon focusing on minimally invasive arthroscopic joint repair, total knee/hip replacement, and athletic rehabilitation protocols.', 4.9, 'Mon - Fri (09:00 - 15:00)', 'https://images.unsplash.com/photo-1537368910025-700350fe46c7?auto=format&fit=crop&w=400&q=80');

-- Patients details
INSERT INTO `patients` (`id`, `user_id`, `assigned_doctor_id`, `date_of_birth`, `gender`, `blood_group`, `emergency_contact_name`, `emergency_contact_phone`, `address`, `medical_history_summary`, `allergies`, `chronic_conditions`) VALUES
(1, 6, 1, '1988-05-14', 'Male', 'O+', 'Sarah Hayes (Spouse)', '+1 (555) 234-5678', '742 Evergreen Terrace, Metro City', 'Active continuous outpatient cardiac telemetry. Normal left ventricular ejection fraction recorded on baseline echocardiogram.', 'Penicillin & Beta-lactams', 'Essential Hypertension (Grade II), Mild Hyperlipidemia'),
(2, 7, 2, '1992-11-20', 'Female', 'A+', 'Carlos Martinez (Father)', '+1 (555) 345-6789', '128 Ocean Avenue, Coastal Bay', 'Under clinical monitoring for recurring migraines with visual aura. Brain MRI unremarkable.', 'Sulfa antibiotics', 'Migraine with Aura'),
(3, 8, 3, '2012-08-05', 'Male', 'B+', 'David Chen (Parent)', '+1 (555) 456-7890', '55 Maple Drive, Oakwood Park', 'Pediatric patient with seasonal allergic rhinitis and intermittent exercise-induced wheeze.', 'Peanuts, Tree nuts', 'Mild Intermittent Asthma');

-- Appointments
INSERT INTO `appointments` (`patient_id`, `doctor_id`, `appointment_date`, `appointment_time`, `meeting_type`, `status`, `symptoms`, `doctor_notes`) VALUES
(1, 1, DATE_ADD(CURDATE(), INTERVAL 2 DAY), '10:00', 'in-person', 'APPROVED', 'Exertional chest tightness and follow-up on resting blood pressure.', 'Confirmed for clinical exam and ECG review.'),
(2, 2, DATE_ADD(CURDATE(), INTERVAL 3 DAY), '11:00', 'telehealth', 'PENDING', 'Right temporal throbbing headache with visual flashing aura.', 'Pending doctor verification of telemetry readings.'),
(3, 3, DATE_ADD(CURDATE(), INTERVAL 4 DAY), '09:00', 'in-person', 'APPROVED', 'Routine pediatric asthma checkup and pulmonary function evaluation.', 'Scheduled in Room 108.');

-- Vital Readings
INSERT INTO `vital_readings` (`patient_id`, `heart_rate`, `blood_pressure_systolic`, `blood_pressure_diastolic`, `body_temperature`, `oxygen_saturation`, `status`, `reading_time`) VALUES
(1, 72, 122, 80, 36.8, 98, 'NORMAL', DATE_SUB(NOW(), INTERVAL 4 HOUR)),
(1, 75, 124, 82, 36.7, 99, 'NORMAL', DATE_SUB(NOW(), INTERVAL 3 HOUR)),
(1, 78, 126, 84, 36.9, 98, 'NORMAL', DATE_SUB(NOW(), INTERVAL 2 HOUR)),
(1, 74, 120, 80, 36.8, 98, 'NORMAL', DATE_SUB(NOW(), INTERVAL 1 HOUR)),
(1, 73, 119, 78, 36.7, 99, 'NORMAL', NOW());

-- AI Assessments
INSERT INTO `ai_assessments` (`patient_id`, `reported_symptoms`, `primary_condition`, `differential_diagnoses`, `confidence_score`, `urgency_level`, `reasoning`, `suggested_actions`, `reviewed_by_doctor`, `doctor_id`, `doctor_notes`) VALUES
(1, 'Substernal chest tightness radiating to left arm upon physical exertion, relieved by 5 minutes of rest.', 'Angina Pectoris / Coronary Artery Disease', 'Gastroesophageal Reflux Disease (GERD), Costochondritis, Musculoskeletal Strain', 0.88, 'URGENT', 'Classic exertional ischemic pattern presenting in adult male with pre-existing hypertension. Requires cardiology workup.', 'Immediate 12-lead ECG, cardiac troponin I/T assay, fasting lipid profile, and cardiologist appointment.', 1, 1, 'Confirmed clinical presentation. Stress echocardiogram and lipid therapy ordered.');

-- Treatment Recommendations
INSERT INTO `treatment_recommendations` (`patient_id`, `doctor_id`, `title`, `diagnosis_condition`, `lifestyle_modifications`, `medication_classes`, `follow_up_schedule`, `approved_by_doctor`, `doctor_notes`) VALUES
(1, 1, 'Cardiovascular Risk Reduction & Blood Pressure Management', 'Essential Hypertension & Angina Triage', 'Adopt DASH dietary guidelines with sodium restriction < 2,000 mg/day. Minimum 30 min daily moderate aerobic activity.', 'ACE Inhibitors (e.g. Lisinopril 10mg PO daily) or Angiotensin II receptor blockers; HMG-CoA reductase inhibitors.', 'Log daily morning BP in MediAI telemetry; follow-up visit in 14 days.', 1, 'Signed and authorized for patient chart.');

-- =====================================================================
-- END OF SCHEMA & SEED DATA SCRIPT
-- =====================================================================
