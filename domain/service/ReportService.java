
package domain.service;

import data.repository.ReportRepository;
import data.repository.MedicalRepository; // [NEW] 데이터 조회를 위해 필요
import domain.patient.PersonalReport;
import domain.patient.GroupComparisonResult;
import domain.patient.HealthRecord;

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
    private final MedicalRepository medicalRepo = new MedicalRepository();

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

    public GroupComparisonResult getOrCalculateComparison(Long patientId) {
        // 1. 내 최근 기록 가져오기
        List<HealthRecord> myRecords = medicalRepo.findRecordsByPatient(patientId);
        if (myRecords.isEmpty()) {
            return null; // 기록이 없으면 분석 불가
        }
        HealthRecord latest = myRecords.get(myRecords.size() - 1);
        double mySysBp = (double) latest.getSystolicBp();

        // 2. 전체 환자 기록 가져와서 평균 계산 (간단하게 전체 평균으로 구현)
        // 실제로는 나이대/성별 필터링이 들어가야 하지만, 현재 User 정보에 나이 필드가 없으므로 전체 평균으로 대체
        List<HealthRecord> allRecords = medicalRepo.findAllRecords();

        double sumBp = 0;
        int count = 0;
        for (HealthRecord r : allRecords) {
            if (r.getSystolicBp() > 0) { // 유효한 값만
                sumBp += r.getSystolicBp();
                count++;
            }
        }
        double groupAvg = count > 0 ? sumBp / count : 0.0;

        // 3. 결과 생성 및 저장
        GroupComparisonResult gr = new GroupComparisonResult();
        gr.initialize(patientId, "전체 사용자 그룹"); // 그룹명
        gr.calculate(mySysBp, groupAvg);
        gr.setChartData("Simple Bar Chart Data"); // UI에서 그래프를 그리기 위한 데이터(여기선 더미)

        return repo.saveGroup(gr);
    }

    public List<GroupComparisonResult> getGroup(Long patientId) {
        return repo.getGroupByPatient(patientId);
    }
}
