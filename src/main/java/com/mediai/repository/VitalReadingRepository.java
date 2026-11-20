package com.mediai.repository;

import com.mediai.entity.VitalReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VitalReadingRepository extends JpaRepository<VitalReading, Long> {
    List<VitalReading> findByPatientIdOrderByRecordedAtDesc(Long patientId);
    List<VitalReading> findTop20ByPatientIdOrderByRecordedAtAsc(Long patientId);
    List<VitalReading> findTop5ByPatientIdOrderByRecordedAtDesc(Long patientId);
}
