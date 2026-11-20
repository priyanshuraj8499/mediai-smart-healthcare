package com.mediai.service;

import com.mediai.dto.RegisterRequest;
import com.mediai.entity.Doctor;
import com.mediai.entity.Patient;
import com.mediai.entity.Role;
import com.mediai.entity.User;
import com.mediai.repository.DoctorRepository;
import com.mediai.repository.PatientRepository;
import com.mediai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(RegisterRequest request) {
        // Enforce specification: Public registration must never allow creation of Admin role
        if ("ROLE_ADMIN".equalsIgnoreCase(request.getRole())) {
            throw new IllegalArgumentException("Security violation: Hospital Administrator accounts cannot be created via public registration.");
        }

        String normalizedEmail = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException("An account with email " + normalizedEmail + " already exists.");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match.");
        }

        Role userRole = "ROLE_DOCTOR".equalsIgnoreCase(request.getRole()) ? Role.ROLE_DOCTOR : Role.ROLE_PATIENT;

        User user = User.builder()
                .email(normalizedEmail)
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName().trim())
                .phone(request.getPhone())
                .role(userRole)
                .avatarUrl("https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&q=80&w=256")
                .enabled(true)
                .build();

        User savedUser = userRepository.save(user);

        if (userRole == Role.ROLE_PATIENT) {
            Patient patient = Patient.builder()
                    .user(savedUser)
                    .emergencyContact("Primary Kin: " + (request.getPhone() != null ? request.getPhone() : "N/A"))
                    .bloodGroup("O+")
                    .gender("Unspecified")
                    .allergies("None reported")
                    .chronicConditions("None")
                    .medicalHistorySummary("Newly registered patient.")
                    .build();
            patientRepository.save(patient);
        } else {
            Doctor doctor = Doctor.builder()
                    .user(savedUser)
                    .specialization("General Clinical Medicine")
                    .qualification("MD / MBBS")
                    .experienceYears(5)
                    .bio("Board-certified clinician at MediAI Medical Center.")
                    .availableDays("Monday, Tuesday, Wednesday, Thursday, Friday")
                    .availableTimeStart("09:00")
                    .availableTimeEnd("17:00")
                    .roomNumber("Main Wing - Suite 101")
                    .isAvailable(true)
                    .build();
            doctorRepository.save(doctor);
        }

        return savedUser;
    }
}
