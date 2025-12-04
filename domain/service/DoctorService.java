package domain.service;

import data.repository.AssignmentRepository;
import data.repository.MedicalRepository;
import data.repository.UserRepository;
import domain.patient.PatientAssignment;
import domain.patient.RiskAssessment;
import domain.medical.DoctorNote;
import domain.medical.ScheduledExam;
import domain.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DoctorService {

    private final MedicalRepository medicalRepo = new MedicalRepository();
    private final AssignmentRepository assignmentRepo = new AssignmentRepository();
    private final UserRepository userRepo = new UserRepository();

    // =========================================================
    // [수정] 담당 환자 목록 조회 (수락된 환자만!)
    // =========================================================
    public List<PatientSummary> getMyPatients(Long doctorId) {
        List<PatientAssignment> all = assignmentRepo.findAssignmentsByDoctor(doctorId);

        // ACCEPTED 상태만 필터링
        List<PatientAssignment> accepted = all.stream()
                .filter(a -> "ACCEPTED".equals(a.getStatus()))
                .collect(Collectors.toList());

        return convertToSummaries(accepted);
    }

    // =========================================================
    // [NEW] 연결 요청 대기 목록 조회 (PENDING 상태만)
    // =========================================================
    public List<PatientSummary> getPendingRequests(Long doctorId) {
        List<PatientAssignment> all = assignmentRepo.findAssignmentsByDoctor(doctorId);

        // PENDING 상태만 필터링
        List<PatientAssignment> pending = all.stream()
                .filter(a -> "PENDING".equals(a.getStatus()))
                .collect(Collectors.toList());

        return convertToSummaries(pending);
    }

    public void replyToRequest(Long assignmentId, boolean isAccept) {
        // ID로 찾기
        Optional<PatientAssignment> target = assignmentRepo.findAll().stream()
                .filter(a -> a.getId().equals(assignmentId))
                .findFirst();

        if (target.isPresent()) {
            PatientAssignment assignment = target.get();

            if (isAccept) {
                assignment.accept(); // 상태를 ACCEPTED로 변경
            } else {
                assignment.reject(); // 상태를 REJECTED로 변경
            }

            // [수정] ★ save(추가) 하지 말고 update(교체) 해야 함!
            // assignmentRepo.saveAssignment(assignment); // <--- 이거 삭제!!
            assignmentRepo.updateAssignment(assignment);  // <--- 이걸로 교체!!
        }
    }

    // [Helper] 배정 리스트 -> 요약 리스트 변환 (코드 중복 제거)
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

            String currentStatus = "데이터 없음";
            List<RiskAssessment> risks = medicalRepo.findRiskByPatient(patientId);
            if (!risks.isEmpty()) currentStatus = risks.get(risks.size() - 1).getRiskLevel();

            // DTO에 assignmentId도 포함 (수락/거절 처리를 위해)
            result.add(new PatientSummary(loginId, patientName, currentStatus, patientId, assign.getId()));
        }
        return result;
    }

    // ... (writeNote, scheduleExam 기존 메서드 유지) ...
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
    // [NEW] 특정 환자의 건강 기록 조회
    public List<domain.patient.HealthRecord> getPatientHealthRecords(Long patientId) {
        return medicalRepo.findRecordsByPatient(patientId);
    }

    // [NEW] 특정 환자의 의사 소견 조회 (내가 쓴 것 뿐만 아니라 히스토리 전체)
    public List<DoctorNote> getPatientDoctorNotes(Long patientId) {
        return medicalRepo.findNotesByPatient(patientId);
    }

    // [DTO 수정] assignmentId 필드 추가
    public static class PatientSummary {
        private String loginId;
        private String name;
        private String status;
        private Long realId;
        private Long assignmentId; // [NEW] 수락/거절할 때 필요

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