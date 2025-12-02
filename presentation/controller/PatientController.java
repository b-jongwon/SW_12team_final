
package presentation.controller;

import domain.service.PatientCareService;
import domain.medical.HealthRecord;
import domain.medical.RiskAssessment;
import domain.medical.ComplicationRisk;

import java.util.List;

public class PatientController {

    private final PatientCareService service = new PatientCareService();

    public HealthRecord addRecord(Long pid,
                                  int sys, int dia, double sugar,
                                  String smoking, String drinking,
                                  String activity, String riskFactors,
                                  double height, double weight) {

        return service.createHealthRecord(pid, sys, dia, sugar,
                smoking, drinking, activity, riskFactors,
                height, weight);
    }

    public List<HealthRecord> getRecords(Long pid) {
        return service.getRecords(pid);
    }

    public RiskAssessment assessRisk(Long pid,
                                     double score, double percent,
                                     String level, String summary) {
        return service.createRisk(pid, score, percent, level, summary);
    }

    public List<RiskAssessment> getRisk(Long pid) {
        return service.getRisk(pid);
    }

    public ComplicationRisk addCompRisk(Long pid,
                                        String type, double prob, String rec) {
        return service.createCompRisk(pid, type, prob, rec);
    }

    public List<ComplicationRisk> getCompRisk(Long pid) {
        return service.getCompRisk(pid);
    }
}
