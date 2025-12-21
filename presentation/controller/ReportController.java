package presentation.controller;

import domain.service.ReportService;
import domain.patient.PersonalReport;
import domain.patient.GroupComparisonResult;

import java.util.List;

public class ReportController {

    private final ReportService service = new ReportService();

    // 1. 개인화 리포트 생성
    public PersonalReport createPersonalReport(Long patientId) {
        return service.createPersonalReport(patientId);
    }

    // 2. 개인화 리포트 조회
    public List<PersonalReport> getPersonalReports(Long patientId) {
        return service.getPersonalReports(patientId);
    }

    // 3. 또래 비교 리포트 조회
    public List<GroupComparisonResult> getGroup(Long patientId) {
        return service.getGroup(patientId);
    }

    // 4. 또래 비교 생성 (필요 시)
    public GroupComparisonResult createGroup(Long patientId, String groupKey, double metric, double avg, String chartData) {
        return service.createGroup(patientId, groupKey, metric, avg, chartData);
    }
}