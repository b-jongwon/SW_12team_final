
package presentation.controller;

import domain.service.ReportService;
import domain.patient.PersonalReport;
import domain.patient.GroupComparisonResult;

import java.util.List;

/**
 * 리포트 컨트롤러:
 * GUI나 콘솔에서 직접 호출해서
 * - 개인 리포트
 * - 그룹 비교 리포트
 * 를 다룰 수 있게 해주는 계층.
 */
public class ReportController {

    private final ReportService service = new ReportService();

    public PersonalReport createPersonal(Long patientId, String summary, String complicationSummary) {
        return service.createPersonal(patientId, summary, complicationSummary);
    }

    public List<PersonalReport> getPersonal(Long patientId) {
        return service.getPersonal(patientId);
    }

    public GroupComparisonResult createGroup(Long patientId, String groupKey,
                                             double metric, double avg,
                                             String chartData) {
        return service.getOrCalculateComparison(patientId);
    }

    public List<GroupComparisonResult> getGroup(Long patientId) {
        return service.getGroup(patientId);
    }
}
