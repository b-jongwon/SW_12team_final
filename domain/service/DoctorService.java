package domain.service;

import data.repository.AssignmentRepository;
import data.repository.MedicalRepository;
import data.repository.UserRepository;
import domain.assignment.PatientAssignment;
import domain.medical.DoctorNote;
import domain.medical.RiskAssessment;
import domain.medical.ScheduledExam;
import domain.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DoctorService {

    // 필요한 리포지토리들 (나중에 DI로 주입받아야 함)
    private final MedicalRepository medicalRepo = new MedicalRepository();
    private final AssignmentRepository assignmentRepo = new AssignmentRepository();
    private final UserRepository userRepo = new UserRepository();

    // =========================================================
    // [핵심] 담당 환자 목록 조회 (이름 + 위험도 포함)
    // =========================================================
    public List<PatientSummary> getMyPatients(Long doctorId) {
        List<PatientSummary> result = new ArrayList<>();

        // 1. 나에게 배정된 환자 목록(ID) 가져오기
        List<PatientAssignment> assignments = assignmentRepo.findAssignmentsByDoctor(doctorId);

        for (PatientAssignment assign : assignments) {
            Long patientId = assign.getPatientId();

            // 2. 환자 이름 찾기 (User Repo)
            String patientName = "알수없음";
            Optional<User> patientOpt = userRepo.findAll().stream()
                    .filter(u -> u.getId().equals(patientId))
                    .findFirst();
            if (patientOpt.isPresent()) {
                patientName = patientOpt.get().getName();
            }

            // 3. 환자의 최근 위험도 찾기 (Medical Repo)
            String currentStatus = "데이터 없음";
            List<RiskAssessment> risks = medicalRepo.findRiskByPatient(patientId);
            if (!risks.isEmpty()) {
                // 가장 최근 기록의 위험 레벨 가져오기
                currentStatus = risks.get(risks.size() - 1).getRiskLevel();
            }

            // 4. 요약 정보(DTO) 생성 및 추가
            // loginId를 화면에 보여주기 위해 User 객체에서 loginId도 가져오면 좋음
            String patientLoginId = patientOpt.map(User::getLoginId).orElse("unknown");

            result.add(new PatientSummary(patientLoginId, patientName, currentStatus, patientId));
        }

        return result;
    }

    // --- 기존 메서드들 (소견, 예약) ---
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

    // =========================================================
    // [DTO] UI에 보여줄 환자 요약 정보 내부 클래스
    // =========================================================
    public static class PatientSummary {
        private String loginId;
        private String name;
        private String status;
        private Long realId; // 실제 DB ID (숨겨진 값)

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

        // JTable에 객체 자체를 넣을 때 문자열 표현 (필요시)
        @Override public String toString() { return name; }
    }
}