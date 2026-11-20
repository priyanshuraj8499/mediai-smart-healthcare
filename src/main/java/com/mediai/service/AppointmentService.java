package com.mediai.service;

import com.mediai.dto.AppointmentRequest;
import com.mediai.entity.Appointment;
import com.mediai.entity.Doctor;
import com.mediai.entity.Notification;
import com.mediai.entity.Patient;
import com.mediai.repository.AppointmentRepository;
import com.mediai.repository.DoctorRepository;
import com.mediai.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final NotificationService notificationService;

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAllByOrderByAppointmentDateDescAppointmentTimeDesc();
    }

    public List<Appointment> getAppointmentsForPatient(Long patientId) {
        return appointmentRepository.findByPatientIdOrderByAppointmentDateDescAppointmentTimeDesc(patientId);
    }

    public List<Appointment> getAppointmentsForDoctor(Long doctorId) {
        return appointmentRepository.findByDoctorIdOrderByAppointmentDateDescAppointmentTimeDesc(doctorId);
    }

    public Optional<Appointment> getAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }

    /**
     * Prevents double-booking using isolation-level transaction & conflict checks.
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Appointment bookAppointment(Long patientId, AppointmentRequest request) {
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new IllegalArgumentException("Selected physician could not be located."));

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient profile not found."));

        // Backend double booking validation check
        Optional<Appointment> conflict = appointmentRepository.findConflictingSlot(
                doctor.getId(),
                request.getAppointmentDate(),
                request.getAppointmentTime()
        );

        if (conflict.isPresent()) {
            throw new IllegalStateException("Slot conflict: Dr. " + doctor.getFullName() +
                    " already has a scheduled appointment on " + request.getAppointmentDate() +
                    " at " + request.getAppointmentTime() + ". Please select another slot.");
        }

        Appointment.MeetingType meetingType = "telehealth".equalsIgnoreCase(request.getMeetingType())
                ? Appointment.MeetingType.TELEHEALTH
                : Appointment.MeetingType.IN_PERSON;

        Appointment appointment = Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .department(doctor.getDepartment())
                .appointmentDate(request.getAppointmentDate())
                .appointmentTime(request.getAppointmentTime())
                .meetingType(meetingType)
                .status(Appointment.Status.PENDING)
                .symptoms(request.getSymptoms().trim())
                .build();

        Appointment saved = appointmentRepository.save(appointment);

        // Notify Doctor
        if (doctor.getUser() != null) {
            notificationService.createNotification(
                    doctor.getUser(),
                    "New Appointment Request",
                    patient.getFullName() + " booked an appointment on " + saved.getAppointmentDate() + " at " + saved.getAppointmentTime(),
                    Notification.Type.INFO,
                    "/doctor/dashboard"
            );
        }

        return saved;
    }

    @Transactional
    public boolean updateStatus(Long appointmentId, Appointment.Status newStatus, String doctorNotes) {
        Optional<Appointment> opt = appointmentRepository.findById(appointmentId);
        if (opt.isPresent()) {
            Appointment apt = opt.get();
            apt.setStatus(newStatus);
            if (doctorNotes != null && !doctorNotes.trim().isEmpty()) {
                apt.setDoctorNotes(doctorNotes.trim());
            }
            appointmentRepository.save(apt);

            // Notify patient
            if (apt.getPatient() != null && apt.getPatient().getUser() != null) {
                notificationService.createNotification(
                        apt.getPatient().getUser(),
                        "Appointment Status: " + newStatus,
                        "Your appointment with " + apt.getDoctor().getFullName() + " on " + apt.getAppointmentDate() + " is now " + newStatus,
                        Notification.Type.SUCCESS,
                        "/patient/dashboard"
                );
            }
            return true;
        }
        return false;
    }

    @Transactional
    public boolean cancelAppointment(Long appointmentId, Long userId) {
        Optional<Appointment> opt = appointmentRepository.findById(appointmentId);
        if (opt.isPresent()) {
            Appointment apt = opt.get();
            apt.setStatus(Appointment.Status.CANCELLED);
            appointmentRepository.save(apt);
            return true;
        }
        return false;
    }
}
