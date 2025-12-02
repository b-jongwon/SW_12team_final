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

public class CaregiverService {

    private final AssignmentRepository assignmentRepo = new AssignmentRepository();
    private final UserRepository userRepo = new UserRepository();
    private final MedicalRepository medicalRepo = new MedicalRepository();

    // 가족(환자) 목록 및 상태 조회
    public List<FamilySummary> getMyFamily(Long caregiverId) {
        List<FamilySummary> result = new ArrayList<>();

        // 1. 배정된 환자 목록 조회
        List<PatientAssignment> assignments = assignmentRepo.findAssignmentsByCaregiver(caregiverId);

        for (PatientAssignment assign : assignments) {
            Long patientId = assign.getPatientId();

            // 2. 환자 이름 조회
            String name = "알수없음";
            Optional<User> patient = userRepo.findAll().stream()
                    .filter(u -> u.getId().equals(patientId))
                    .findFirst();
            if (patient.isPresent()) {
                name = patient.get().getName();
            }

            // 3. 최신 위험도 조회
            String riskLevel = "데이터 없음";
            String summary = "-";

            List<RiskAssessment> risks = medicalRepo.findRiskByPatient(patientId);
            if (!risks.isEmpty()) {
                RiskAssessment latest = risks.get(risks.size() - 1);
                riskLevel = latest.getRiskLevel();
                summary = latest.getRecommendationSummary();
            }

            result.add(new FamilySummary(patientId, name, riskLevel, summary));
        }

        return result;
    }

    // [DTO] 가족 요약 정보
    public static class FamilySummary {
        private Long patientId;
        private String name;
        private String riskLevel;
        private String description;

        public FamilySummary(Long patientId, String name, String riskLevel, String description) {
            this.patientId = patientId;
            this.name = name;
            this.riskLevel = riskLevel;
            this.description = description;
        }

        public Long getPatientId() { return patientId; }
        public String getName() { return name; }
        public String getRiskLevel() { return riskLevel; }
        public String getDescription() { return description; }
    }
}