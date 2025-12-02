package domain.service;

import data.repository.AssignmentRepository;
import data.repository.MedicalRepository;
import data.repository.UserRepository;
import domain.patient.RiskAssessment; // (패키지 이동됨!)
import domain.patient.PatientAssignment; // (패키지 이동됨!)
import domain.medical.DoctorNote;
import domain.medical.ScheduledExam;
import domain.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DoctorService {

    private final MedicalRepository medicalRepo = new MedicalRepository();
    private final AssignmentRepository assignmentRepo = new AssignmentRepository();
    private final UserRepository userRepo = new UserRepository();

    // =========================================================
    // [NEW] 담당 환자 목록 조회 (이름 + 위험도 포함)
    // =========================================================
    public List<PatientSummary> getMyPatients(Long doctorId) {
        List<PatientSummary> result = new ArrayList<>();

        // 1. 나(의사)에게 배정된 환자 ID 목록 찾기 (AssignmentRepo 기능 필요)
        // (AssignmentRepository에 findAssignmentsByDoctor 메서드가 없으면 오류가 날 수 있음 -> Step 1.5에서 추가)
        List<PatientAssignment> assignments = assignmentRepo.findAssignmentsByDoctor(doctorId);

        for (PatientAssignment assign : assignments) {
            Long patientId = assign.getPatientId();

            // 2. 환자 이름 찾기 (User Repo)
            String patientName = "알수없음";
            Optional<User> patientOpt = userRepo.findAll().stream()
                    .filter(u -> u.getId().equals(patientId))
                    .findFirst();

            String loginId = "unknown";
            if (patientOpt.isPresent()) {
                patientName = patientOpt.get().getName();
                loginId = patientOpt.get().getLoginId();
            }

            // 3. 환자의 최근 위험도 찾기 (Medical Repo)
            String currentStatus = "데이터 없음";
            List<RiskAssessment> risks = medicalRepo.findRiskByPatient(patientId);
            if (!risks.isEmpty()) {
                // 가장 최근 기록의 위험 레벨
                currentStatus = risks.get(risks.size() - 1).getRiskLevel();
            }

            // 4. 요약 정보(DTO) 생성
            result.add(new PatientSummary(loginId, patientName, currentStatus, patientId));
        }

        return result;
    }

    // --- 기존 메서드들 ---
    public DoctorNote writeNote(Long doctorId, Long patientId, String content) {
        DoctorNote note = new DoctorNote();
        note.write(doctorId, patientId, content);
        return medicalRepo.saveNote(note);
    }

    public ScheduledExam scheduleExam(Long doctorId, Long patientId, LocalDateTime date, String description) {
        ScheduledExam exam = new ScheduledExam();
        exam.schedule(doctorId, patientId, date, description);
        return medicalRepo.saveExam(exam);
    }

    // [DTO] UI 전달용 데이터 클래스
    public static class PatientSummary {
        private String loginId;
        private String name;
        private String status;
        private Long realId;

        public PatientSummary(String loginId, String name, String status, Long realId) {
            this.loginId = loginId;
            this.name = name;
            this.status = status;
            this.realId = realId;
        }

        public String getLoginId() { return loginId; }
        public String getName() { return name; }
        public String getStatus() { return status; }
        public Long getRealId() { return realId; }
    }
}