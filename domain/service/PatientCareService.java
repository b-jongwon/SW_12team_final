
package domain.service;

import data.repository.MedicalRepository;
import domain.medical.HealthRecord;
import domain.medical.RiskAssessment;
import domain.medical.ComplicationRisk;

import java.util.List;

public class PatientCareService {

    private final MedicalRepository repo = new MedicalRepository();

    public HealthRecord createHealthRecord(Long patientId,
                                           int sys, int dia, double sugar,
                                           String smoking, String drinking,
                                           String activity, String riskFactors,
                                           double height, double weight) {

        HealthRecord record = new HealthRecord();
        record.setPatientId(patientId);
        record.update(sys, dia, sugar, smoking, drinking,
                      activity, riskFactors, height, weight);

        return repo.saveNewRecord(record);
    }

    public List<HealthRecord> getRecords(Long patientId) {
        return repo.findRecordsByPatient(patientId);
    }

    public RiskAssessment createRisk(Long pid, double score, double percent,
                                     String level, String summary) {
        RiskAssessment r = new RiskAssessment();
        r.setPatientId(pid);
        r.assess(score, percent, level, summary);
        return repo.saveRisk(r);
    }

    public List<RiskAssessment> getRisk(Long pid) {
        return repo.findRiskByPatient(pid);
    }

    public ComplicationRisk createCompRisk(Long pid, String type,
                                           double prob, String rec) {
        ComplicationRisk r = new ComplicationRisk();
        r.setPatientId(pid);
        r.update(type, prob, rec);
        return repo.saveCompRisk(r);
    }

    public List<ComplicationRisk> getCompRisk(Long pid) {
        return repo.findCompRiskByPatient(pid);
    }
}
