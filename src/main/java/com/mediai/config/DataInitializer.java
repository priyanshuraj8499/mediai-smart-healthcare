package com.mediai.config;

import com.mediai.entity.*;
import com.mediai.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final VitalReadingRepository vitalReadingRepository;
    private final AIAssessmentRepository assessmentRepository;
    private final TreatmentRecommendationRepository recommendationRepository;
    private final HospitalResourceRepository resourceRepository;
    private final NotificationRepository notificationRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("MediAI database already initialized. Skipping seed.");
            return;
        }

        log.info("Seeding MediAI healthcare platform with clinically rich sample data...");

        // 1. DEPARTMENTS
        Department cardio = departmentRepository.save(Department.builder()
                .name("Cardiology")
                .code("CARD")
                .description("Specialized diagnosis and treatment of congenital heart defects, coronary artery disease, heart failure and electrophysiology.")
                .icon("bi-heart-pulse")
                .headDoctorName("Dr. Marcus Vance, MD")
                .bedCapacity(45)
                .occupiedBeds(38)
                .build());

        Department neuro = departmentRepository.save(Department.builder()
                .name("Neurology")
                .code("NEUR")
                .description("Advanced neurological disorders care, stroke management, neuro-oncology, epilepsy, and cognitive rehabilitation.")
                .icon("bi-activity")
                .headDoctorName("Dr. Elena Rostova, PhD")
                .bedCapacity(35)
                .occupiedBeds(29)
                .build());

        Department emergency = departmentRepository.save(Department.builder()
                .name("Emergency & Trauma")
                .code("EMRG")
                .description("24/7 Level-1 trauma response, acute critical stabilization, resuscitation suites, and AI triage prioritization.")
                .icon("bi-lightning-charge")
                .headDoctorName("Dr. Rajesh Patel, MBBS, MD")
                .bedCapacity(60)
                .occupiedBeds(51)
                .build());

        Department peds = departmentRepository.save(Department.builder()
                .name("Pediatrics")
                .code("PED")
                .description("Compassionate pediatric healthcare from neonatal care to adolescent wellness with dedicated pediatric ICU.")
                .icon("bi-person-heart")
                .headDoctorName("Dr. Chloe Dubois, MD")
                .bedCapacity(30)
                .occupiedBeds(18)
                .build());

        Department ortho = departmentRepository.save(Department.builder()
                .name("Orthopedics & Spine")
                .code("ORTH")
                .description("Joint replacement, robotic-assisted spine surgery, sports injury rehabilitation, and trauma reconstruction.")
                .icon("bi-hospital")
                .headDoctorName("Dr. Nathan Reed, MD")
                .bedCapacity(40)
                .occupiedBeds(32)
                .build());

        Department general = departmentRepository.save(Department.builder()
                .name("Internal & General Medicine")
                .code("GENM")
                .description("Comprehensive chronic disease management, preventative screenings, diagnostic checkups, and outpatient care.")
                .icon("bi-bandaid")
                .headDoctorName("Dr. Sarah Jenkins, MD")
                .bedCapacity(50)
                .occupiedBeds(41)
                .build());

        // 2. USERS & ROLES
        String encodedPass = passwordEncoder.encode("password123");

        // Admin User
        User adminUser = userRepository.save(User.builder()
                .email("admin@mediai.health")
                .password(encodedPass)
                .fullName("Dr. Sarah Jenkins")
                .phone("+1 (555) 019-2831")
                .role(Role.ROLE_ADMIN)
                .avatarUrl("https://images.unsplash.com/photo-1559839734-2b71ea197ec2?auto=format&fit=crop&q=80&w=256")
                .enabled(true)
                .build());

        // Doctors
        User docUser1 = userRepository.save(User.builder()
                .email("dr.marcus@mediai.health")
                .password(encodedPass)
                .fullName("Dr. Marcus Vance, MD")
                .phone("+1 (555) 014-9922")
                .role(Role.ROLE_DOCTOR)
                .avatarUrl("https://images.unsplash.com/photo-1622253692010-333f2da6031d?auto=format&fit=crop&q=80&w=256")
                .enabled(true)
                .build());

        Doctor doc1 = doctorRepository.save(Doctor.builder()
                .user(docUser1)
                .department(cardio)
                .specialization("Interventional Cardiology & Electrophysiology")
                .qualification("MD, FACC, Harvard Medical School")
                .experienceYears(15)
                .consultationFee(BigDecimal.valueOf(150.00))
                .rating(4.9)
                .reviewCount(142)
                .bio("Lead Cardiologist specializing in minimally invasive cardiac catheterization, arrhythmia therapies, and preventive cardiovascular AI risk assessment.")
                .availableDays("Monday, Tuesday, Wednesday, Thursday, Friday")
                .availableTimeStart("09:00")
                .availableTimeEnd("16:00")
                .roomNumber("Wing B - Suite 304")
                .isAvailable(true)
                .build());

        User docUser2 = userRepository.save(User.builder()
                .email("dr.elena@mediai.health")
                .password(encodedPass)
                .fullName("Dr. Elena Rostova, PhD")
                .phone("+1 (555) 017-3381")
                .role(Role.ROLE_DOCTOR)
                .avatarUrl("https://images.unsplash.com/photo-1594824813588-e9f0865c1979?auto=format&fit=crop&q=80&w=256")
                .enabled(true)
                .build());

        Doctor doc2 = doctorRepository.save(Doctor.builder()
                .user(docUser2)
                .department(neuro)
                .specialization("Cognitive Neurology & Stroke Recovery")
                .qualification("MD, PhD in Neurobiology, Johns Hopkins")
                .experienceYears(12)
                .consultationFee(BigDecimal.valueOf(175.00))
                .rating(4.95)
                .reviewCount(98)
                .bio("Renowned neuroscientist and clinician focusing on early neurodegenerative detection, migraine management, and AI brain mapping.")
                .availableDays("Monday, Wednesday, Thursday")
                .availableTimeStart("10:00")
                .availableTimeEnd("17:00")
                .roomNumber("Wing C - Suite 210")
                .isAvailable(true)
                .build());

        User docUser3 = userRepository.save(User.builder()
                .email("dr.rajesh@mediai.health")
                .password(encodedPass)
                .fullName("Dr. Rajesh Patel, MBBS, MD")
                .phone("+1 (555) 012-7744")
                .role(Role.ROLE_DOCTOR)
                .avatarUrl("https://images.unsplash.com/photo-1612349317150-e413f6a5b16d?auto=format&fit=crop&q=80&w=256")
                .enabled(true)
                .build());

        Doctor doc3 = doctorRepository.save(Doctor.builder()
                .user(docUser3)
                .department(emergency)
                .specialization("Emergency Critical Care & Acute Resuscitation")
                .qualification("MBBS, MD (Emergency Medicine), Stanford Health")
                .experienceYears(11)
                .consultationFee(BigDecimal.valueOf(120.00))
                .rating(4.85)
                .reviewCount(215)
                .bio("Emergency medicine specialist with extensive background in rapid triage, acute pulmonary distress, and remote telemetry oversight.")
                .availableDays("Tuesday, Thursday, Friday, Saturday")
                .availableTimeStart("08:00")
                .availableTimeEnd("15:00")
                .roomNumber("Emergency Wing - Bay 1")
                .isAvailable(true)
                .build());

        User docUser4 = userRepository.save(User.builder()
                .email("dr.chloe@mediai.health")
                .password(encodedPass)
                .fullName("Dr. Chloe Dubois, MD")
                .phone("+1 (555) 018-4455")
                .role(Role.ROLE_DOCTOR)
                .avatarUrl("https://images.unsplash.com/photo-1559839734-2b71ea197ec2?auto=format&fit=crop&q=80&w=256")
                .enabled(true)
                .build());

        doctorRepository.save(Doctor.builder()
                .user(docUser4)
                .department(peds)
                .specialization("General Pediatrics & Neonatal Wellness")
                .qualification("MD, FAAP, Columbia University")
                .experienceYears(9)
                .consultationFee(BigDecimal.valueOf(110.00))
                .rating(4.92)
                .reviewCount(88)
                .bio("Dedicated pediatrician passionate about proactive childhood developmental monitoring, immunotherapy, and adolescent medicine.")
                .availableDays("Monday, Tuesday, Wednesday, Friday")
                .availableTimeStart("09:00")
                .availableTimeEnd("14:30")
                .roomNumber("Children Pavilion - Suite 102")
                .isAvailable(true)
                .build());

        // Patients
        User patUser1 = userRepository.save(User.builder()
                .email("patient@mediai.health")
                .password(encodedPass)
                .fullName("Alexander Hayes")
                .phone("+1 (555) 234-5678")
                .role(Role.ROLE_PATIENT)
                .avatarUrl("https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&q=80&w=256")
                .enabled(true)
                .build());

        Patient pat1 = patientRepository.save(Patient.builder()
                .user(patUser1)
                .dateOfBirth(LocalDate.of(1989, 5, 14))
                .gender("Male")
                .bloodGroup("A+")
                .emergencyContact("Claire Hayes (Spouse): +1 (555) 234-9988")
                .address("742 Evergreen Terrace, Seattle, WA")
                .allergies("Penicillin, Sulfa drugs")
                .chronicConditions("Mild Hypertension, Seasonal Asthma")
                .medicalHistorySummary("Diagnosed with stage-1 hypertension in 2022; managed with Lisinopril 10mg daily. Occasional asthmatic flare-ups triggered by cold air.")
                .assignedDoctor(doc1)
                .build());

        User patUser2 = userRepository.save(User.builder()
                .email("sophia.chen@example.com")
                .password(encodedPass)
                .fullName("Sophia Chen")
                .phone("+1 (555) 345-6789")
                .role(Role.ROLE_PATIENT)
                .avatarUrl("https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&q=80&w=256")
                .enabled(true)
                .build());

        Patient pat2 = patientRepository.save(Patient.builder()
                .user(patUser2)
                .dateOfBirth(LocalDate.of(1995, 11, 28))
                .gender("Female")
                .bloodGroup("O+")
                .emergencyContact("David Chen (Brother): +1 (555) 345-1122")
                .address("1204 Pine Street, Seattle, WA")
                .allergies("Latex")
                .chronicConditions("Migraine with aura")
                .medicalHistorySummary("History of chronic tension migraines since age 22. MRI in 2023 showed normal intracranial structures.")
                .assignedDoctor(doc2)
                .build());

        User patUser3 = userRepository.save(User.builder()
                .email("david.miller@example.com")
                .password(encodedPass)
                .fullName("David Miller")
                .phone("+1 (555) 456-7890")
                .role(Role.ROLE_PATIENT)
                .avatarUrl("https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?auto=format&fit=crop&q=80&w=256")
                .enabled(true)
                .build());

        Patient pat3 = patientRepository.save(Patient.builder()
                .user(patUser3)
                .dateOfBirth(LocalDate.of(1972, 3, 8))
                .gender("Male")
                .bloodGroup("B+")
                .emergencyContact("Rachel Miller (Wife): +1 (555) 456-2233")
                .address("883 Harbor View Rd, Bellevue, WA")
                .allergies("Aspirin, NSAIDs")
                .chronicConditions("Type 2 Diabetes, Coronary Artery Disease")
                .medicalHistorySummary("Coronary stent placed in 2021 (LAD). Glycemic control maintained on Metformin 500mg BID.")
                .assignedDoctor(doc1)
                .build());

        // 3. APPOINTMENTS
        appointmentRepository.save(Appointment.builder()
                .patient(pat1)
                .doctor(doc1)
                .department(cardio)
                .appointmentDate(LocalDate.now().plusDays(2))
                .appointmentTime("10:30")
                .status(Appointment.Status.APPROVED)
                .meetingType(Appointment.MeetingType.IN_PERSON)
                .symptoms("Mild chest tightness upon moderate exertion, accompanied by occasional palpitations.")
                .doctorNotes("Patient showed normal sinus rhythm. Continue current ACE inhibitor regimen.")
                .build());

        appointmentRepository.save(Appointment.builder()
                .patient(pat2)
                .doctor(doc2)
                .department(neuro)
                .appointmentDate(LocalDate.now().plusDays(3))
                .appointmentTime("14:00")
                .status(Appointment.Status.PENDING)
                .meetingType(Appointment.MeetingType.TELEHEALTH)
                .symptoms("Recurring unilateral throbbing headache, photophobia, nausea over past 4 days.")
                .build());

        appointmentRepository.save(Appointment.builder()
                .patient(pat3)
                .doctor(doc1)
                .department(cardio)
                .appointmentDate(LocalDate.now().plusDays(4))
                .appointmentTime("11:00")
                .status(Appointment.Status.APPROVED)
                .meetingType(Appointment.MeetingType.IN_PERSON)
                .symptoms("Routine 6-month post-stent cardiac evaluation.")
                .build());

        // 4. VITALS
        vitalReadingRepository.save(VitalReading.builder()
                .patient(pat1)
                .heartRate(74)
                .systolicBp(122)
                .diastolicBp(78)
                .bodyTemperature(36.8)
                .oxygenSaturation(98)
                .respiratoryRate(16)
                .status(VitalReading.Status.NORMAL)
                .notes("Baseline morning resting telemetry.")
                .isSimulated(true)
                .recordedAt(LocalDateTime.now().minusHours(3))
                .build());

        vitalReadingRepository.save(VitalReading.builder()
                .patient(pat1)
                .heartRate(78)
                .systolicBp(126)
                .diastolicBp(80)
                .bodyTemperature(36.9)
                .oxygenSaturation(98)
                .respiratoryRate(17)
                .status(VitalReading.Status.NORMAL)
                .notes("Post-light breakfast ambulatory telemetry.")
                .isSimulated(true)
                .recordedAt(LocalDateTime.now().minusHours(1))
                .build());

        vitalReadingRepository.save(VitalReading.builder()
                .patient(pat3)
                .heartRate(96)
                .systolicBp(142)
                .diastolicBp(92)
                .bodyTemperature(37.4)
                .oxygenSaturation(94)
                .respiratoryRate(21)
                .status(VitalReading.Status.WARNING)
                .notes("Borderline elevated BP and slight desaturation; tele-nurse alert triggered.")
                .isSimulated(true)
                .recordedAt(LocalDateTime.now().minusMinutes(20))
                .build());

        // 5. AI ASSESSMENTS
        AIAssessment asst1 = assessmentRepository.save(AIAssessment.builder()
                .patient(pat1)
                .patientName(pat1.getFullName())
                .age(35)
                .gender("Male")
                .symptoms("Mild dull retrosternal chest tightness radiating towards left scapula upon climbing stairs, lasting 3-5 minutes.")
                .symptomDuration("3 days, intermittent")
                .medicalHistory(pat1.getMedicalHistorySummary())
                .existingConditions(pat1.getChronicConditions())
                .additionalNotes("Father had myocardial infarction at age 52.")
                .possibleConditionsJson("[{\"condition\":\"Stable Exertional Angina Pectoris\",\"probability\":\"~68% Probability\",\"urgency\":\"high\",\"description\":\"Myocardial ischemia during exertion in the context of vascular risk profile.\"}," +
                                       "{\"condition\":\"Musculoskeletal Costochondritis\",\"probability\":\"~22% Probability\",\"urgency\":\"low\",\"description\":\"Benign chest wall cartilage inflammation.\"}," +
                                       "{\"condition\":\"Gastroesophageal Reflux (GERD)\",\"probability\":\"~10% Probability\",\"urgency\":\"low\",\"description\":\"Gastric acid irritation simulating retrosternal discomfort.\"}]")
                .confidenceLevel("High")
                .suggestedNextStepsJson("[\"Consult an attending Cardiologist immediately for 12-lead ECG and Cardiac Troponins.\",\"Avoid strenuous exertion or heavy lifting until cleared by your physician.\",\"Log resting heart rate and blood pressure twice daily.\"]")
                .generalGuidanceJson("[\"Adopt a heart-healthy Mediterranean diet low in sodium.\",\"Avoid caffeine, tobacco, and energy stimulants.\",\"Maintain continuous ambulatory telemetry.\"]")
                .redFlagsJson("[\"Crushing chest pain persisting longer than 10 minutes.\",\"Pain radiating to left arm, neck, or jaw accompanied by cold sweats.\",\"Acute shortness of breath or dizziness.\"]")
                .isDisclaimerAcknowledged(true)
                .isDoctorReviewed(true)
                .reviewingDoctor(doc1)
                .doctorFeedback("Reviewed. ECG ordered for upcoming appointment. Patient advised on emergency triage criteria.")
                .build());

        // 6. TREATMENT RECOMMENDATIONS
        recommendationRepository.save(TreatmentRecommendation.builder()
                .assessment(asst1)
                .patient(pat1)
                .doctor(doc1)
                .patientSummary("35 y/o male with history of stage-1 HTN presenting with reproducible exertional chest discomfort.")
                .preliminaryAssessment("Possible exertional angina. Low-to-intermediate Framingham risk profile.")
                .suggestedCarePlanJson("[\"12-lead Electrocardiogram (ECG) and high-sensitivity Troponin I baseline.\",\"Treadmill Exercise Stress Test with continuous 12-lead monitoring.\",\"Comprehensive lipid sub-fraction analysis (ApoB, Lp(a), LDL-C, Triglycerides).\"]")
                .precautionsJson("[\"Cease all strenuous anaerobic lifting and heavy exertion immediately.\",\"If pain persists > 10 min without relief, call Emergency Services (911/112) immediately.\"]")
                .suggestedFollowUp("Follow-up in cardiology clinic within 48 hours with laboratory results.")
                .doctorNotes("Care plan approved and verified. Requisition dispatched.")
                .status(TreatmentRecommendation.Status.APPROVED)
                .approvedAt(LocalDateTime.now().minusDays(1))
                .build());

        // 7. HOSPITAL RESOURCES
        resourceRepository.save(HospitalResource.builder()
                .resourceName("ICU Critical Care Beds")
                .category(HospitalResource.Category.ICU)
                .totalCount(50)
                .availableCount(14)
                .occupiedCount(36)
                .department(emergency)
                .departmentName("Intensive Care Unit")
                .build());

        resourceRepository.save(HospitalResource.builder()
                .resourceName("Inpatient General Ward Beds")
                .category(HospitalResource.Category.BEDS)
                .totalCount(220)
                .availableCount(58)
                .occupiedCount(162)
                .department(general)
                .departmentName("Inpatient Services")
                .build());

        resourceRepository.save(HospitalResource.builder()
                .resourceName("Advanced Invasive Ventilators")
                .category(HospitalResource.Category.VENTILATORS)
                .totalCount(32)
                .availableCount(11)
                .occupiedCount(21)
                .department(emergency)
                .departmentName("Pulmonary & Critical Care")
                .build());

        resourceRepository.save(HospitalResource.builder()
                .resourceName("Emergency ALS Ambulances")
                .category(HospitalResource.Category.AMBULANCES)
                .totalCount(12)
                .availableCount(4)
                .occupiedCount(8)
                .department(emergency)
                .departmentName("Emergency Logistics")
                .build());

        resourceRepository.save(HospitalResource.builder()
                .resourceName("Smart Operating Suites")
                .category(HospitalResource.Category.OPERATING_ROOMS)
                .totalCount(14)
                .availableCount(5)
                .occupiedCount(9)
                .department(ortho)
                .departmentName("Surgical Services")
                .build());

        // 8. NOTIFICATIONS
        notificationRepository.save(Notification.builder()
                .user(adminUser)
                .title("Hospital Capacity Notice")
                .message("Cardiology wing bed occupancy reached 84%. Reallocation protocol active.")
                .type(Notification.Type.WARNING)
                .link("/admin/dashboard")
                .isRead(false)
                .build());

        notificationRepository.save(Notification.builder()
                .user(docUser1)
                .title("New Appointment Booking")
                .message("Alexander Hayes confirmed an appointment for " + LocalDate.now().plusDays(2) + " at 10:30 AM.")
                .type(Notification.Type.INFO)
                .link("/doctor/dashboard")
                .isRead(false)
                .build());

        notificationRepository.save(Notification.builder()
                .user(patUser1)
                .title("AI Assessment Reviewed")
                .message("Dr. Marcus Vance reviewed your AI preliminary symptom report.")
                .type(Notification.Type.SUCCESS)
                .link("/patient/dashboard")
                .isRead(true)
                .build());

        log.info("MediAI sample healthcare data seeded successfully!");
    }
}
