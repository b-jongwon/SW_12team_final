
package domain.service;

import data.repository.ReportRepository;
import domain.report.PersonalReport;
import domain.report.GroupComparisonResult;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 리포트 관련 비즈니스 로직:
 * - 개인 리포트 생성/조회
 * - 그룹 비교 리포트 생성/조회
 * 합병증 관련 내용도 PersonalReport/GroupComparisonResult 안에서 같이 다룸.
 */
public class ReportService {

    private final ReportRepository repo = new ReportRepository();

    public PersonalReport createPersonal(Long patientId, String summary, String complicationSummary) {
        PersonalReport pr = new PersonalReport();
        pr.init(patientId);
        pr.setSummaryText(summary);
        pr.setComplicationSummary(complicationSummary);
        pr.setPeriod(LocalDateTime.now().minusDays(30), LocalDateTime.now());
        return repo.savePersonal(pr);
    }

    public List<PersonalReport> getPersonal(Long patientId) {
        return repo.getPersonalByPatient(patientId);
    }

    public GroupComparisonResult createGroup(Long patientId, String groupKey,
                                             double patientMetric, double groupAvg,
                                             String chartData) {
        GroupComparisonResult gr = new GroupComparisonResult();
        gr.initialize(patientId, groupKey);
        gr.calculate(patientMetric, groupAvg);
        gr.setChartData(chartData);
        return repo.saveGroup(gr);
    }

    public List<GroupComparisonResult> getGroup(Long patientId) {
        return repo.getGroupByPatient(patientId);
    }
}
