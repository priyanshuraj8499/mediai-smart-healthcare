# MediAI – AI-Powered Smart Healthcare Management System

[![Java](https://img.shields.io/badge/Java-17%20LTS-007396?logo=openjdk&logoColor=white)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Security](https://img.shields.io/badge/Spring%20Security-6.x-6DB33F?logo=springsecurity&logoColor=white)](https://spring.io/projects/spring-security)
[![Database](https://img.shields.io/badge/MySQL-8.0-4479A1?logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Frontend](https://img.shields.io/badge/Thymeleaf%20%7C%20Bootstrap-5.3-2563EB?logo=bootstrap&logoColor=white)](https://getbootstrap.com/)
[![Live Demo](https://img.shields.io/badge/Live%20Demo-Online-22C55E?logo=cloudflare)](https://too-yrs-carlos-corps.trycloudflare.com)

A complete, production-grade **Full-Stack Java Enterprise Web Application** designed for hospital administration, physician workflows, continuous patient telemetry, and AI-assisted preliminary clinical diagnosis. Built exclusively with **Java 17, Spring Boot 3, Spring Data JPA, Hibernate, Spring Security, MySQL 8.0, Thymeleaf, and Bootstrap 5**.

🌐 **GitHub Repository:** [https://github.com/priyanshuraj8499/mediai-smart-healthcare](https://github.com/priyanshuraj8499/mediai-smart-healthcare)  
🚀 **Live Deployed URL:** [https://too-yrs-carlos-corps.trycloudflare.com](https://too-yrs-carlos-corps.trycloudflare.com)

---

## 🏥 Design Theme & Visual Identity

The user interface follows a modern healthcare SaaS aesthetic:
- **Primary Color:** Medical Blue (`#2563EB`)
- **Secondary Color:** Teal (`#14B8A6`)
- **Background:** Crisp White (`#FFFFFF`)
- **Light Accent Canvas:** Soft Blue (`#F0F7FF`)
- **Positive / Health State:** Soft Green (`#22C55E`)
- **Deep Typography:** Dark Navy (`#172554`)
- **Subtle Borders & Cards:** Slate Gray (`#64748B`) & Subtle Border (`#E2E8F0`)

---

## 🚀 Key Functional Modules

### 1. Hospital Executive Administration (`/admin/*`)
- **Census & Resource Analytics:** Live KPI cards for admitted census, bed occupancy percentages, ICU units, and ambulance status.
- **Department Oversight:** 6 clinical divisions (Cardiology, Neurology, Pediatrics, Orthopedics, General Medicine, Oncology) with division heads and bed allocations.
- **Equipment & Bed Management (`/admin/resources`):** Interactive asset allocation console to modify available bed/ventilator counts in real-time.
- **Master Appointment Ledger (`/admin/appointments`):** Hospital-wide consultation schedule with multi-status filtering (Confirmed, Pending, Completed, Cancelled).
- **Doctors & Patients Directories:** Directory of credentialed physicians, department heads, and patient electronic charts.

### 2. Physician Clinical Portal (`/doctor/*`)
- **Doctor Overview Dashboard:** Real-time consultations queue, assigned patient list, pending review counts, and weekly practice hours.
- **Appointment Approval & Triage (`/doctor/appointments`):** Interactive confirm, complete, or decline actions with doctor clinical notes.
- **AI Diagnostic Report Review (`/doctor/records`):** Review preliminary machine learning triage results, differential diagnoses, and provide electronic clinical sign-off.
- **Care Plan Authorization (`/doctor/treatment-recommendations`):** Authorize evidence-based lifestyle modifications and educational medication classes.
- **Physician Profile (`/doctor/profile`):** Specialization, consultation fees, clinic room number, credentials, and weekly schedules.

### 3. Patient Healthcare Portal (`/patient/*`)
- **Personal Health Overview:** Current assigned cardiologist, next consultation countdown, and health status indicators.
- **Smart Appointment Booking (`/patient/book-appointment`):** Real-time booking with **strict database-level and service-level double-booking collision prevention** across physician slots.
- **Continuous Telemetry & IoT Simulator (`/patient/monitoring`):**
  - Live biometrics: **Heart Rate (BPM)**, **Blood Pressure (mmHg)**, **SpO2 Oxygen (%)**, and **Body Temperature (°C)**.
  - Interactive multi-axis **Chart.js** historical trendline graph.
  - Interactive **Simulate Telemetry Tick** and **Trigger Critical Anomaly Alert** buttons.
- **AI Symptom Assessment Engine (`/patient/diagnosis`):**
  - Structured intake for age, gender, duration, chronic conditions, and symptom description.
  - One-click demo presets: *Chest Tightness / Angina*, *Unilateral Migraine & Aura*, and *Productive Cough & Wheeze*.
  - Dual-tier analysis: Configurable Gemini 1.5 Pro AI integration with deterministic clinical rule-based triage fallback.
  - Displays primary condition, differential possibilities, urgency level (Routine, Urgent, Emergency), and recommended next steps.
- **EHR & Medical History (`/patient/medical-history`):** Complete longitudinal chart, documented allergies, chronic conditions, and printable summary.

---

## 👥 Default Login Credentials

All demo accounts share the password: **`password123`**

| Role | Email | Password | Access Level |
| :--- | :--- | :--- | :--- |
| **Admin** | `admin@mediai.health` | `password123` | Full Hospital Administration (`/admin/dashboard`) |
| **Doctor** | `dr.marcus@mediai.health` | `password123` | Cardiology Attending Physician (`/doctor/dashboard`) |
| **Doctor** | `dr.elena@mediai.health` | `password123` | Neurology Specialist (`/doctor/dashboard`) |
| **Doctor** | `dr.chloe@mediai.health` | `password123` | Pediatrics Specialist (`/doctor/dashboard`) |
| **Doctor** | `dr.rajesh@mediai.health` | `password123` | Orthopedics Specialist (`/doctor/dashboard`) |
| **Patient** | `patient@mediai.health` | `password123` | Alexander Hayes (`/patient/dashboard`) |
| **Patient** | `patient2@mediai.health` | `password123` | Sophia Martinez (`/patient/dashboard`) |
| **Patient** | `patient3@mediai.health` | `password123` | Liam Chen (`/patient/dashboard`) |

> **Note on Registration:** Public registration automatically provisions users with `ROLE_PATIENT`. Self-assignment of `ROLE_ADMIN` is strictly forbidden and protected at the service layer.

---

## 🛠️ Technology Stack & Dependencies

- **Programming Language:** Java 17 LTS
- **Framework:** Spring Boot 3.3.5
- **Web Layer:** Spring MVC
- **Security:** Spring Security 6 (BCrypt hashing, custom role-based login handlers, CSRF protection)
- **Persistence:** Spring Data JPA, Hibernate ORM
- **Database:** MySQL 8.0 (with H2 in-memory profile available for instant zero-config testing)
- **Templates:** Thymeleaf 3 + Thymeleaf Extras Spring Security 6
- **Frontend Design:** Bootstrap 5.3, Bootstrap Icons 1.11, Custom CSS
- **Visualization:** Chart.js 4.4
- **Build Tool:** Apache Maven 3.9+

---

## 📂 Project Architecture

```
ONLINE HEALTH MANAGEMENT SYSTEM BASED ON AI/
│
├── pom.xml                               # Maven build configuration
├── database-schema.sql                   # MySQL 8.0 DDL & seed data script
├── README.md                             # Documentation & setup guide
│
└── src/main/
    ├── java/com/mediai/
    │   ├── MediAiApplication.java       # Spring Boot main entry point
    │   ├── config/
    │   │   ├── SecurityConfig.java       # Spring Security 6 configuration
    │   │   ├── WebConfig.java            # Web MVC resource handlers
    │   │   └── DataInitializer.java     # Automated database seeder
    │   ├── controller/
    │   │   ├── HomeController.java       # Public pages controller
    │   │   ├── AuthController.java       # Login & registration controller
    │   │   ├── AdminController.java      # Admin dashboard & resource manager
    │   │   ├── DoctorController.java     # Physician dashboard & review handlers
    │   │   ├── PatientController.java    # Patient dashboard, bookings, telemetry & AI triage
    │   │   ├── AppointmentController.java# Role-based appointment router
    │   │   ├── DiagnosisController.java  # AI triage router
    │   │   └── MonitoringController.java # Telemetry router
    │   ├── dto/
    │   │   ├── RegisterRequest.java      # User registration form DTO
    │   │   ├── AppointmentRequest.java   # Booking form DTO
    │   │   ├── DiagnosisRequest.java     # AI triage intake DTO
    │   │   └── HospitalStatsDto.java     # Executive statistics DTO
    │   ├── entity/
    │   │   ├── User.java, Role.java      # Auth & RBAC entities
    │   │   ├── Doctor.java, Patient.java # Medical domain entities
    │   │   ├── Department.java           # Hospital divisions
    │   │   ├── Appointment.java          # Booking entity with unique slot constraint
    │   │   ├── VitalReading.java         # Real-time telemetry biometrics
    │   │   ├── AIAssessment.java         # AI clinical triage assessment
    │   │   ├── TreatmentRecommendation.java # Doctor-signed care protocols
    │   │   ├── HospitalResource.java     # Beds, ICU, ventilators, ambulances
    │   │   └── Notification.java         # System alerts
    │   ├── repository/                   # Spring Data JPA repositories
    │   ├── security/                     # UserDetailsService & custom success router
    │   └── service/                      # Core business logic services
    │
    └── resources/
        ├── application.properties        # MySQL configuration
        ├── application-h2.properties     # Zero-setup H2 fallback profile
        ├── static/
        │   ├── css/style.css             # Theme stylesheet
        │   └── js/main.js                # Frontend scripts
        └── templates/
            ├── index.html                # Public homepage with AI illustrations & stats
            ├── about.html                # About Us page
            ├── features.html             # Platform features page
            ├── doctors.html              # Public doctors directory
            ├── contact.html              # Contact Us page
            ├── login.html, register.html # Auth pages
            ├── admin/                    # Admin views (dashboard, doctors, patients, appointments, etc.)
            ├── doctor/                   # Doctor views (dashboard, schedule, patients, AI review, etc.)
            ├── patient/                  # Patient views (dashboard, book, vitals, AI diagnosis, EHR)
            └── fragments/                # Reusable navbar, sidebar, footer & alerts
```

---

## ⚡ How to Run the Application

### Option 1: Running with MySQL 8.0 (Recommended for Production)

1. **Create the Database:**
   ```bash
   mysql -u root -p < database-schema.sql
   ```
2. **Configure Database Credentials:**
   Edit `src/main/resources/application.properties` or set environment variables:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/mediai_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
   spring.datasource.username=root
   spring.datasource.password=YOUR_MYSQL_PASSWORD
   ```
3. **Run via Maven:**
   ```bash
   mvn clean spring-boot:run
   ```
4. Access the application in your browser:
   👉 **`http://localhost:8081/`**

---

### Option 2: Running with In-Memory H2 Profile (Instant Zero-Setup Mode)

If MySQL is not installed or configured on your machine, launch the application with the pre-configured `h2` profile. The database and seed data will initialize automatically in memory:

```bash
mvn clean spring-boot:run -Dspring-boot.run.profiles=h2
```
or run the pre-built JAR:
```bash
java -jar target/mediai-healthcare-1.0.0.jar --spring.profiles.active=h2
```

Access the application in your browser:
👉 **`http://localhost:8081/`**  
*(H2 Web Console is available at `http://localhost:8081/h2-console` with JDBC URL `jdbc:h2:mem:mediaidb`)*

---

### Option 3: Opening and Running in IntelliJ IDEA

1. Open **IntelliJ IDEA**.
2. Select **File ➔ Open...** and choose `ONLINE HEALTH MANAGEMENT SYSTEM BASED ON AI`.
3. IntelliJ will automatically detect `pom.xml` and download the dependencies.
4. Ensure your Project SDK is set to **Java 17** (*File ➔ Project Structure ➔ SDK: 17*).
5. Locate `src/main/java/com/mediai/MediAiApplication.java`, right-click, and select **Run 'MediAiApplication'**.
6. Open your browser and navigate to `http://localhost:8081/`.

---

## 🩺 Clinical and Regulatory Disclaimer

> **IMPORTANT:** MediAI is an academic demonstration system. Preliminary AI diagnostic assessments, differential possibilities, and clinical recommendations generated by the platform do **not** constitute a formal clinical diagnosis, certified medical prescription, or emergency triage service. Medical decisions must always be made by a licensed healthcare professional. In the event of a medical emergency, immediately dial **911** or **112**.
