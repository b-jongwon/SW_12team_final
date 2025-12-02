package data.repository;

import infra.BaseJsonRepository;
import infra.IdGenerator;

import domain.medical.HealthRecord;
import domain.medical.RiskAssessment;
import domain.medical.ComplicationRisk;
// [추가된 Import]
import domain.medical.DoctorNote;
import domain.medical.ScheduledExam;

import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.stream.Collectors;

public class MedicalRepository {

    // 1. 기존 리포지토리 필드들
    private final BaseJsonRepository<HealthRecord> healthRepo =
            new BaseJsonRepository<>("data/health_records.json",
                    new TypeToken<List<HealthRecord>>() {}) {};

    private final BaseJsonRepository<RiskAssessment> riskRepo =
            new BaseJsonRepository<>("data/risk_assessments.json",
                    new TypeToken<List<RiskAssessment>>() {}) {};

    private final BaseJsonRepository<ComplicationRisk> compRepo =
            new BaseJsonRepository<>("data/complication_risks.json",
                    new TypeToken<List<ComplicationRisk>>() {}) {};

    // 2. [새로 추가] 의사 소견(Note) & 검사 일정(Exam) 저장소 필드
    private final BaseJsonRepository<DoctorNote> noteRepo =
            new BaseJsonRepository<>("data/doctor_notes.json",
                    new TypeToken<List<DoctorNote>>() {}) {};

    private final BaseJsonRepository<ScheduledExam> examRepo =
            new BaseJsonRepository<>("data/scheduled_exams.json",
                    new TypeToken<List<ScheduledExam>>() {}) {};


    // -------------------------------------------------------
    // 기존 메서드들 (HealthRecord, Risk, Complication)
    // -------------------------------------------------------

    // HealthRecord
    public HealthRecord saveNewRecord(HealthRecord record) {
        record.setId(IdGenerator.nextId("health_record"));
        healthRepo.save(record);
        return record;
    }

    public List<HealthRecord> findRecordsByPatient(Long pid) {
        return healthRepo.findAll().stream()
                .filter(r -> r.getPatientId().equals(pid))
                .collect(Collectors.toList());
    }

    // RiskAssessment
    public RiskAssessment saveRisk(RiskAssessment r) {
        r.setId(IdGenerator.nextId("risk_assess"));
        riskRepo.save(r);
        return r;
    }

    public List<RiskAssessment> findRiskByPatient(Long pid) {
        return riskRepo.findAll().stream()
                .filter(a -> a.getPatientId().equals(pid))
                .collect(Collectors.toList());
    }

    // ComplicationRisk
    public ComplicationRisk saveCompRisk(ComplicationRisk r) {
        r.setId(IdGenerator.nextId("comp_risk"));
        compRepo.save(r);
        return r;
    }

    public List<ComplicationRisk> findCompRiskByPatient(Long pid) {
        return compRepo.findAll().stream()
                .filter(a -> a.getPatientId().equals(pid))
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------
    // [새로 추가] DoctorNote & ScheduledExam 관련 메서드 5개
    // -------------------------------------------------------

    // DoctorNote (의사 소견)
    public DoctorNote saveNote(DoctorNote note) {
        note.setId(IdGenerator.nextId("doc_note"));
        noteRepo.save(note);
        return note;
    }

    public List<DoctorNote> findNotesByPatient(Long pid) {
        return noteRepo.findAll().stream()
                .filter(n -> n.getPatientId().equals(pid))
                .collect(Collectors.toList());
    }

    // ScheduledExam (검사 일정)
    public ScheduledExam saveExam(ScheduledExam exam) {
        exam.setId(IdGenerator.nextId("exam"));
        examRepo.save(exam);
        return exam;
    }

    public List<ScheduledExam> findExamsByPatient(Long pid) {
        return examRepo.findAll().stream()
                .filter(e -> e.getPatientId().equals(pid))
                .collect(Collectors.toList());
    }

    public List<ScheduledExam> findExamsByDoctor(Long did) {
        return examRepo.findAll().stream()
                .filter(e -> e.getDoctorId().equals(did))
                .collect(Collectors.toList());
    }
}