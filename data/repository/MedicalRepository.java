
package data.repository;

import infra.BaseJsonRepository;
import infra.IdGenerator;

import domain.medical.HealthRecord;
import domain.medical.RiskAssessment;
import domain.medical.ComplicationRisk;

import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.stream.Collectors;

public class MedicalRepository {

    private final BaseJsonRepository<HealthRecord> healthRepo =
        new BaseJsonRepository<>("data/health_records.json",
            new TypeToken<List<HealthRecord>>() {}) {};

    private final BaseJsonRepository<RiskAssessment> riskRepo =
        new BaseJsonRepository<>("data/risk_assessments.json",
            new TypeToken<List<RiskAssessment>>() {}) {};

    private final BaseJsonRepository<ComplicationRisk> compRepo =
        new BaseJsonRepository<>("data/complication_risks.json",
            new TypeToken<List<ComplicationRisk>>() {}) {};

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
}
