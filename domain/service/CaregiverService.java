package domain.service;

import data.repository.AssignmentRepository;
import data.repository.MedicalRepository;
import data.repository.UserRepository;
import domain.medical.DoctorNote;
import domain.patient.HealthRecord;
import domain.patient.PatientAssignment;
import domain.patient.RiskAssessment;
import domain.patient.RiskConfiguration;
import domain.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CaregiverService {

    private final AssignmentRepository assignmentRepo = new AssignmentRepository();
    private final UserRepository userRepo = new UserRepository();
    private final MedicalRepository medicalRepo = new MedicalRepository();

    // 채팅방 입장용
    private final MessagingService messagingService = new MessagingService();

    // 1. 내 가족 목록
    public List<FamilySummary> getMyFamily(Long caregiverId) {
        return getListByStatus(caregiverId, "ACCEPTED");
    }

    // 2. 연결 요청 목록
    public List<FamilySummary> getPendingRequests(Long caregiverId) {
        return getListByStatus(caregiverId, "PENDING");
    }

    // 3. 수락/거절 처리
    public void replyToRequest(Long assignmentId, boolean isAccept) {
        Optional<PatientAssignment> target = assignmentRepo.findAll().stream()
                .filter(a -> a.getId().equals(assignmentId))
                .findFirst();

        if (target.isPresent()) {
            PatientAssignment assignment = target.get();
            if (isAccept) {
                assignment.accept();
                assignmentRepo.updateAssignment(assignment);

                // 채팅방 입장
                messagingService.joinRoom(assignment.getPatientId(), null, assignment.getCaregiverId());
            } else {
                assignment.reject();
                assignmentRepo.updateAssignment(assignment);
            }
        }
    }

    // 가족(환자)의 건강 기록 상세 조회
    public List<HealthRecord> getPatientHealthRecords(Long patientId) {
        return medicalRepo.findRecordsByPatient(patientId);
    }

    // 가족(환자)에게 남겨진 의사 소견 조회
    public List<DoctorNote> getPatientDoctorNotes(Long patientId) {
        return medicalRepo.findNotesByPatient(patientId);
    }


    private HealthRecord getLatestRecord(Long patientId) {
        List<HealthRecord> records = medicalRepo.findRecordsByPatient(patientId);
        if (records.isEmpty()) return null;

        return records.stream()
                .filter(r -> r.getMeasuredAt() != null)
                .max(java.util.Comparator.comparing(HealthRecord::getMeasuredAt))
                .orElse(records.get(records.size() - 1));
    }

    private RiskAssessment calculateCurrentRisk(HealthRecord r) {
        if (r == null) return null;

        double score = 0.0;
        StringBuilder reason = new StringBuilder();

        // 개인 맞춤 기준
        RiskConfiguration.PersonalCriteria c =
                RiskConfiguration.getPersonalizedCriteria(r.getAge(), r.getGender());

        // 혈압
        if (r.getSystolicBp() >= c.maxSys || r.getDiastolicBp() >= c.maxDia) {
            score += 40.0; reason.append("고혈압/ ");
        } else if (r.getSystolicBp() >= (c.maxSys - 20)) {
            score += 15.0; reason.append("혈압주의/ ");
        }

        // 혈당
        if (r.getBloodSugar() >= c.maxSugar) {
            score += 30.0; reason.append("당뇨/ ");
        }

        // BMI
        if (r.getBmi() >= c.maxBmi) {
            score += 15.0; reason.append("비만/ ");
        }

        // 흡연
        if ("Yes".equalsIgnoreCase(r.getSmoking())) {
            score += 15.0; reason.append("흡연/ ");
        }

        if (reason.length() == 0) reason.append("정상 범위");

        String level = "정상";
        if (score >= 60) level = "고위험";
        else if (score >= 30) level = "주의";

        RiskAssessment risk = new RiskAssessment();
        risk.setPatientId(r.getPatientId());
        risk.assess(score, score, level, reason.toString());
        risk.setAssessedAt(r.getMeasuredAt());
        return risk;
    }

    private List<FamilySummary> getListByStatus(Long caregiverId, String status) {
        List<FamilySummary> result = new ArrayList<>();
        List<PatientAssignment> list = assignmentRepo.findAssignmentsByCaregiver(caregiverId);

        for (PatientAssignment assign : list) {
            if (!status.equals(assign.getStatus())) continue;

            Long patientId = assign.getPatientId();
            String name = "알수없음";
            String loginId = "unknown";

            Optional<User> p = userRepo.findAll().stream()
                    .filter(u -> u.getId().equals(patientId))
                    .findFirst();

            if (p.isPresent()) {
                name = p.get().getName();
                loginId = p.get().getLoginId();
            }


            HealthRecord latest = getLatestRecord(patientId);
            RiskAssessment current = calculateCurrentRisk(latest);

            String riskLevel = "데이터 없음";
            String desc = "-";

            if (current != null) {
                riskLevel = current.getRiskLevel();
                desc = current.getRecommendationSummary(); // reason이 들어감
            }

            result.add(new FamilySummary(patientId, name, loginId, riskLevel, desc, assign.getId()));
        }
        return result;
    }


    public static class FamilySummary {
        private Long patientId;
        private String name;
        private String loginId;
        private String riskLevel;
        private String description;
        private Long assignmentId;

        public FamilySummary(Long patientId, String name, String loginId,
                             String riskLevel, String description, Long assignmentId) {
            this.patientId = patientId;
            this.name = name;
            this.loginId = loginId;
            this.riskLevel = riskLevel;
            this.description = description;
            this.assignmentId = assignmentId;
        }

        public Long getPatientId() { return patientId; }
        public String getName() { return name; }
        public String getLoginId() { return loginId; }
        public String getRiskLevel() { return riskLevel; }
        public String getDescription() { return description; }
        public Long getAssignmentId() { return assignmentId; }
    }
}
