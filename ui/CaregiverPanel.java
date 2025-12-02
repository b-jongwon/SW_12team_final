package ui;

import presentation.controller.CaregiverController;
import domain.service.CaregiverService.FamilySummary;
import domain.user.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CaregiverPanel extends JPanel {

    private final CaregiverController controller = new CaregiverController();
    private final User caregiver;

    private DefaultTableModel tableModel;

    public CaregiverPanel(User caregiver) {
        this.caregiver = caregiver;
        setLayout(new BorderLayout());

        // 1. 상단 제목
        JLabel title = new JLabel("🏡 가족 건강 모니터링 (" + caregiver.getName() + ")");
        title.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(title, BorderLayout.NORTH);

        // 2. 중앙: 가족 목록 테이블
        String[] colNames = {"가족 이름", "현재 위험 단계", "최근 분석 소견"};
        tableModel = new DefaultTableModel(colNames, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setFont(new Font("맑은 고딕", Font.PLAIN, 14));

        // 위험도 컬럼 글자색 변경 (Renderer) - 선택 사항

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("관리 중인 가족 목록"));
        add(scrollPane, BorderLayout.CENTER);

        // 3. 하단: 새로고침 버튼
        JButton refreshBtn = new JButton("🔄 상태 새로고침");
        refreshBtn.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(refreshBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        // 이벤트: 새로고침
        refreshBtn.addActionListener(e -> loadFamilyData());

        // 초기 데이터 로드
        loadFamilyData();
    }

    private void loadFamilyData() {
        tableModel.setRowCount(0); // 초기화
        List<FamilySummary> familyList = controller.getMyFamily(caregiver.getId());

        if (familyList.isEmpty()) {
            // 데이터가 없을 때 안내 메시지는 테이블이 비어있는 것으로 대체하거나 팝업
        } else {
            for (FamilySummary f : familyList) {
                tableModel.addRow(new Object[]{
                        f.getName(),
                        f.getRiskLevel(),
                        f.getDescription()
                });
            }
        }
    }
}