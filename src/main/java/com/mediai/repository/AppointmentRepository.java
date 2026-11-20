package com.mediai.repository;

import com.mediai.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatientIdOrderByAppointmentDateDescAppointmentTimeDesc(Long patientId);

    List<Appointment> findByDoctorIdOrderByAppointmentDateDescAppointmentTimeDesc(Long doctorId);

    List<Appointment> findByDoctorIdAndStatusOrderByAppointmentDateDesc(Long doctorId, Appointment.Status status);

    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId AND a.appointmentDate = :date AND a.appointmentTime = :time AND a.status NOT IN ('CANCELLED', 'REJECTED')")
    Optional<Appointment> findConflictingSlot(
            @Param("doctorId") Long doctorId,
            @Param("date") LocalDate date,
            @Param("time") String time
    );

    long countByStatus(Appointment.Status status);

    List<Appointment> findAllByOrderByAppointmentDateDescAppointmentTimeDesc();
}
