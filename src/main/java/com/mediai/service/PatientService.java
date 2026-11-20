package com.mediai.service;

import com.mediai.entity.Patient;
import com.mediai.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public Optional<Patient> getPatientById(Long id) {
        return patientRepository.findById(id);
    }

    public Optional<Patient> getPatientByUserId(Long userId) {
        return patientRepository.findByUserId(userId);
    }

    public List<Patient> getPatientsByDoctor(Long doctorId) {
        return patientRepository.findByAssignedDoctorId(doctorId);
    }

    public Patient savePatient(Patient patient) {
        return patientRepository.save(patient);
    }
}
