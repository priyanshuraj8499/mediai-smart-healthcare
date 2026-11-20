package com.mediai.service;

import com.mediai.entity.Notification;
import com.mediai.entity.Patient;
import com.mediai.entity.VitalReading;
import com.mediai.repository.PatientRepository;
import com.mediai.repository.VitalReadingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class MonitoringService {

    private final VitalReadingRepository vitalReadingRepository;
    private final PatientRepository patientRepository;
    private final NotificationService notificationService;
    private final Random random = new Random();

    public List<VitalReading> getVitalHistory(Long patientId) {
        return vitalReadingRepository.findTop20ByPatientIdOrderByRecordedAtAsc(patientId);
    }

    public List<VitalReading> getRecentLogs(Long patientId) {
        return vitalReadingRepository.findTop5ByPatientIdOrderByRecordedAtDesc(patientId);
    }

    public VitalReading getLatestReading(Long patientId) {
        List<VitalReading> list = vitalReadingRepository.findByPatientIdOrderByRecordedAtDesc(patientId);
        if (!list.isEmpty()) {
            return list.get(0);
        }
        // Fallback baseline reading
        Patient patient = patientRepository.findById(patientId).orElse(null);
        return VitalReading.builder()
                .patient(patient)
                .heartRate(74)
                .systolicBp(120)
                .diastolicBp(78)
                .bodyTemperature(36.8)
                .oxygenSaturation(98)
                .respiratoryRate(16)
                .bloodGlucose(95.0)
                .status(VitalReading.Status.NORMAL)
                .notes("Simulated baseline resting telemetry.")
                .isSimulated(true)
                .recordedAt(LocalDateTime.now())
                .build();
    }

    @Transactional
    public VitalReading recordReading(
            Long patientId,
            int heartRate,
            int systolic,
            int diastolic,
            double temp,
            int spo2,
            int rr,
            boolean isSimulated,
            String notes
    ) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found"));

        VitalReading.Status status = VitalReading.Status.NORMAL;
        if (spo2 < 90 || heartRate > 135 || heartRate < 45 || systolic > 175) {
            status = VitalReading.Status.CRITICAL;
        } else if (spo2 < 95 || heartRate > 105 || systolic > 135 || temp > 38.0) {
            status = VitalReading.Status.WARNING;
        }

        VitalReading reading = VitalReading.builder()
                .patient(patient)
                .heartRate(heartRate)
                .systolicBp(systolic)
                .diastolicBp(diastolic)
                .bodyTemperature(temp)
                .oxygenSaturation(spo2)
                .respiratoryRate(rr)
                .status(status)
                .notes(notes)
                .isSimulated(isSimulated)
                .recordedAt(LocalDateTime.now())
                .build();

        VitalReading saved = vitalReadingRepository.save(reading);

        // If Critical, alert assigned doctor
        if (status == VitalReading.Status.CRITICAL && patient.getAssignedDoctor() != null && patient.getAssignedDoctor().getUser() != null) {
            notificationService.createNotification(
                    patient.getAssignedDoctor().getUser(),
                    "CRITICAL TELEMETRY ALERT: " + patient.getFullName(),
                    "Patient vital reading breached critical safety threshold: HR " + heartRate + " BPM, SpO2 " + spo2 + "%",
                    Notification.Type.ALERT,
                    "/doctor/dashboard"
            );
        }

        return saved;
    }

    @Transactional
    public VitalReading simulateNextTick(Long patientId, boolean induceAnomaly) {
        int baseHr = 72 + random.nextInt(8);
        int baseSys = 118 + random.nextInt(10);
        int baseDia = 76 + random.nextInt(6);
        double baseTemp = 36.6 + (random.nextInt(5) * 0.1);
        int baseSpo2 = 98 - (random.nextInt(10) > 8 ? 1 : 0);
        int baseRr = 16 + random.nextInt(3);
        String note = "Continuous simulated ambulatory telemetry feed.";

        if (induceAnomaly) {
            int anomalyType = random.nextInt(3);
            if (anomalyType == 0) {
                baseHr = 138;
                note = "Simulated Acute Tachycardia spike event.";
            } else if (anomalyType == 1) {
                baseSpo2 = 91;
                note = "Simulated Oxygen Desaturation event (<92%).";
            } else {
                baseSys = 168;
                baseDia = 104;
                note = "Simulated Hypertensive Stage-2 surge event.";
            }
        }

        return recordReading(patientId, baseHr, baseSys, baseDia, baseTemp, baseSpo2, baseRr, true, note);
    }
}
