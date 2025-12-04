package domain.service;

import data.repository.AssignmentRepository;
import data.repository.MedicalRepository;
import data.repository.UserRepository;
import domain.patient.PatientAssignment;
import domain.patient.RiskAssessment;
import domain.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CaregiverService {

    private final AssignmentRepository assignmentRepo = new AssignmentRepository();
    private final UserRepository userRepo = new UserRepository();
    private final MedicalRepository medicalRepo = new MedicalRepository();

    // 1. 내 가족 목록 (수락된 사람만)
    public List<FamilySummary> getMyFamily(Long caregiverId) {
        return getListByStatus(caregiverId, "ACCEPTED");
    }

    // [NEW] 2. 연결 요청 목록 (대기 중인 사람만)
    public List<FamilySummary> getPendingRequests(Long caregiverId) {
        return getListByStatus(caregiverId, "PENDING");
    }

    // [NEW] 3. 수락/거절 처리
    public void replyToRequest(Long assignmentId, boolean isAccept) {
        Optional<PatientAssignment> target = assignmentRepo.findAll().stream()
                .filter(a -> a.getId().equals(assignmentId))
                .findFirst();

        if (target.isPresent()) {
            PatientAssignment assignment = target.get();
            if (isAccept) assignment.accept();
            else assignment.reject();

            assignmentRepo.updateAssignment(assignment); // 업데이트
        }
    }
    // [NEW] 가족(환자)의 건강 기록 상세 조회
    public List<domain.patient.HealthRecord> getPatientHealthRecords(Long patientId) {
        return medicalRepo.findRecordsByPatient(patientId);
    }

    // [NEW] 가족(환자)에게 남겨진 의사 소견 조회
    public List<domain.medical.DoctorNote> getPatientDoctorNotes(Long patientId) {
        return medicalRepo.findNotesByPatient(patientId);
    }

    // 헬퍼 메서드
    private List<FamilySummary> getListByStatus(Long caregiverId, String status) {
        List<FamilySummary> result = new ArrayList<>();
        List<PatientAssignment> list = assignmentRepo.findAssignmentsByCaregiver(caregiverId);

        for (PatientAssignment assign : list) {
            if (!status.equals(assign.getStatus())) continue; // 상태 필터링

            Long patientId = assign.getPatientId();
            String name = "알수없음";
            String loginId = "unknown";

            Optional<User> p = userRepo.findAll().stream().filter(u -> u.getId().equals(patientId)).findFirst();
            if (p.isPresent()) {
                name = p.get().getName();
                loginId = p.get().getLoginId();
            }

            String risk = "-";
            String desc = "-";
            List<RiskAssessment> risks = medicalRepo.findRiskByPatient(patientId);
            if (!risks.isEmpty()) {
                risk = risks.get(risks.size() - 1).getRiskLevel();
                desc = risks.get(risks.size() - 1).getRecommendationSummary();
            }

            // DTO에 assignmentId 포함
            result.add(new FamilySummary(patientId, name, loginId, risk, desc, assign.getId()));
        }
        return result;
    }

    // [DTO 수정] assignmentId, loginId 추가
    public static class FamilySummary {
        private Long patientId;
        private String name;
        private String loginId;
        private String riskLevel;
        private String description;
        private Long assignmentId;

        public FamilySummary(Long patientId, String name, String loginId, String riskLevel, String description, Long assignmentId) {
            this.patientId = patientId;
            this.name = name;
            this.loginId = loginId;
            this.riskLevel = riskLevel;
            this.description = description;
            this.assignmentId = assignmentId;
        }
        // Getters...
        public String getName() { return name; }
        public String getLoginId() { return loginId; }
        public String getRiskLevel() { return riskLevel; }
        public String getDescription() { return description; }
        public Long getAssignmentId() { return assignmentId; }
        public Long getPatientId() {
            return patientId;
        }
    }
}