package domain.service;

import data.repository.AssignmentRepository;
import data.repository.MedicalRepository;
import data.repository.UserRepository;
import domain.medical.DoctorNote;
import domain.medical.ScheduledExam;
import domain.patient.HealthRecord;
import domain.patient.PatientAssignment;
import domain.patient.RiskAssessment;
import data.repository.UserRepository;
import domain.patient.RiskConfiguration;
import domain.user.Patient;
import domain.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Optional;

public class DoctorService {

    private final MedicalRepository medicalRepo = new MedicalRepository();
    private final AssignmentRepository assignmentRepo = new AssignmentRepository();
    private final UserRepository userRepo = new UserRepository();

    // [중요] 이 줄이 없어서 에러가 났던 겁니다. 추가해주세요!
    private final MessagingService messagingService = new MessagingService();

    // 담당 환자 목록 조회 (수락된 환자만!)
    public List<PatientSummary> getMyPatients(Long doctorId) {
        List<PatientAssignment> all = assignmentRepo.findAssignmentsByDoctor(doctorId);

        List<PatientAssignment> accepted = all.stream()
                .filter(a -> "ACCEPTED".equals(a.getStatus()))
                .collect(Collectors.toList());

        return convertToSummaries(accepted);
    }

    public Patient getPatientById(Long patientId) {
        UserRepository userRepository = new UserRepository(); // 지금 구조가 new 방식이라면 이게 제일 단순
        Optional<User> found = userRepository.findById(patientId);

        if (found.isEmpty()) return null;

        User u = found.get();
        if (u instanceof Patient) return (Patient) u;

        return null; // Patient가 아닌 User면 null
    }

    // 연결 요청 대기 목록 조회 (PENDING 상태만)
    public List<PatientSummary> getPendingRequests(Long doctorId) {
        List<PatientAssignment> all = assignmentRepo.findAssignmentsByDoctor(doctorId);

        List<PatientAssignment> pending = all.stream()
                .filter(a -> "PENDING".equals(a.getStatus()))
                .collect(Collectors.toList());

        return convertToSummaries(pending);
    }

    // [수정완료] 주석 제거하고 로직 복원했습니다.
    public void replyToRequest(Long assignmentId, boolean isAccept) {
        // 1. ID로 찾기 (이 부분이 주석되어 있어서 target을 못 찾았던 것임)
        Optional<PatientAssignment> target = assignmentRepo.findAll().stream()
                .filter(a -> a.getId().equals(assignmentId))
                .findFirst();

        if (target.isPresent()) {
            PatientAssignment assignment = target.get();
            if (isAccept) {
                assignment.accept();
                assignmentRepo.updateAssignment(assignment);

                // [NEW] 채팅방에 '의사'로서 입장!
                messagingService.joinRoom(assignment.getPatientId(), assignment.getDoctorId(), null);

            } else {
                assignment.reject();
                assignmentRepo.updateAssignment(assignment);
            }
        }
    }

    // [Helper] 배정 리스트 -> 요약 리스트 변환
    private List<PatientSummary> convertToSummaries(List<PatientAssignment> assignments) {
        List<PatientSummary> result = new ArrayList<>();
        for (PatientAssignment assign : assignments) {
            Long patientId = assign.getPatientId();
            String patientName = "알수없음";
            String loginId = "unknown";

            Optional<User> pOpt = userRepo.findAll().stream()
                    .filter(u -> u.getId().equals(patientId)).findFirst();
            if (pOpt.isPresent()) {
                patientName = pOpt.get().getName();
                loginId = pOpt.get().getLoginId();
            }

            String currentStatus = calculateCurrentRiskLevel(getLatestRecord(patientId));

            result.add(new PatientSummary(loginId, patientName, currentStatus, patientId, assign.getId()));
        }
        return result;
    }

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

    public List<domain.patient.HealthRecord> getPatientHealthRecords(Long patientId) {
        return medicalRepo.findRecordsByPatient(patientId);
    }
    private HealthRecord getLatestRecord(Long patientId) {
        List<HealthRecord> records = medicalRepo.findRecordsByPatient(patientId);
        if (records.isEmpty()) return null;

        return records.stream()
                .filter(r -> r.getMeasuredAt() != null)
                .max(java.util.Comparator.comparing(HealthRecord::getMeasuredAt))
                .orElse(records.get(records.size() - 1));
    }

    private String calculateCurrentRiskLevel(HealthRecord r) {
        if (r == null) return "데이터 없음";

        double score = 0.0;

        RiskConfiguration.PersonalCriteria c =
                RiskConfiguration.getPersonalizedCriteria(r.getAge(), r.getGender());

        if (r.getSystolicBp() >= c.maxSys || r.getDiastolicBp() >= c.maxDia) score += 40.0;
        else if (r.getSystolicBp() >= (c.maxSys - 20)) score += 15.0;

        if (r.getBloodSugar() >= c.maxSugar) score += 30.0;
        if (r.getBmi() >= c.maxBmi) score += 15.0;
        if ("Yes".equalsIgnoreCase(r.getSmoking())) score += 15.0;

        if (score >= 60) return "고위험";
        if (score >= 30) return "주의";
        return "정상";
    }
    public List<DoctorNote> getPatientDoctorNotes(Long patientId) {
        return medicalRepo.findNotesByPatient(patientId);
    }

    public static class PatientSummary {
        private String loginId;
        private String name;
        private String status;
        private Long realId;
        private Long assignmentId;

        public PatientSummary(String loginId, String name, String status, Long realId, Long assignmentId) {
            this.loginId = loginId;
            this.name = name;
            this.status = status;
            this.realId = realId;
            this.assignmentId = assignmentId;
        }

        public String getLoginId() { return loginId; }
        public String getName() { return name; }
        public String getStatus() { return status; }
        public Long getRealId() { return realId; }
        public Long getAssignmentId() { return assignmentId; }
    }
}