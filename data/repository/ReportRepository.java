
package data.repository;

import infra.BaseJsonRepository;
import infra.IdGenerator;
import domain.patient.PersonalReport;
import domain.patient.GroupComparisonResult;
import com.google.gson.reflect.TypeToken;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 리포트 전용 레포지토리.
 * - PersonalReport
 * - GroupComparisonResult
 * 두 종류만 관리 (다이어그램 구조에 맞게 단순화).
 */
public class ReportRepository {

    private final BaseJsonRepository<PersonalReport> personalRepo =
        new BaseJsonRepository<>("data/personal_reports.json",
            new TypeToken<List<PersonalReport>>() {}) {};

    private final BaseJsonRepository<GroupComparisonResult> groupRepo =
        new BaseJsonRepository<>("data/group_comparisons.json",
            new TypeToken<List<GroupComparisonResult>>() {}) {};

    // PersonalReport
    public PersonalReport savePersonal(PersonalReport pr) {
        pr.setId(IdGenerator.nextId("personal_report"));
        personalRepo.save(pr);
        return pr;
    }

    public List<PersonalReport> getPersonalByPatient(Long pid) {
        return personalRepo.findAll().stream()
            .filter(r -> r.getPatientId().equals(pid))
            .collect(Collectors.toList());
    }

    // GroupComparisonResult
    public GroupComparisonResult saveGroup(GroupComparisonResult gr) {
        gr.setId(IdGenerator.nextId("group_report"));
        groupRepo.save(gr);
        return gr;
    }

    public List<GroupComparisonResult> getGroupByPatient(Long pid) {
        return groupRepo.findAll().stream()
            .filter(r -> r.getPatientId().equals(pid))
            .collect(Collectors.toList());
    }
}
