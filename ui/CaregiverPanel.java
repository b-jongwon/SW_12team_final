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

    // UI 컴포넌트
    private JTable familyTable;
    private DefaultTableModel familyModel;

    private JTable requestTable;
    private DefaultTableModel requestModel;

    public CaregiverPanel(User caregiver) {
        this.caregiver = caregiver;
        setLayout(new BorderLayout());

        // 1. 상단 제목
        JLabel title = new JLabel("🏡 가족 건강 모니터링 (" + caregiver.getName() + ")");
        title.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(title, BorderLayout.NORTH);

        // 2. 중앙: 탭 패널 (가족 목록 vs 연결 요청)
        JTabbedPane tab = new JTabbedPane();
        tab.addTab("내 가족 목록", createFamilyPanel());
        tab.addTab("🔔 연결 요청", createRequestPanel()); // [NEW]

        add(tab, BorderLayout.CENTER);
    }

    // --------------------------------------------------------
    // [탭 1] 내 가족 목록 패널
    // --------------------------------------------------------
    private JPanel createFamilyPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JButton refreshBtn = new JButton("목록 새로고침");
        panel.add(refreshBtn, BorderLayout.NORTH);

        // [수정] 마지막에 "ID" 컬럼 추가 (데이터 식별용)
        String[] colNames = {"가족 이름", "현재 위험 단계", "최근 분석 소견", "ID"};

        familyModel = new DefaultTableModel(colNames, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        familyTable = new JTable(familyModel);
        familyTable.setRowHeight(30);

        // (선택 사항) ID 컬럼 숨기기 코드를 넣을 수도 있지만, 개발 중엔 보이는 게 편합니다.

        // [NEW] 테이블 더블클릭 이벤트 리스너
        familyTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) { // 더블 클릭 시
                    int row = familyTable.getSelectedRow();
                    if (row != -1) {
                        // 테이블에서 데이터 가져오기
                        String pName = (String) familyModel.getValueAt(row, 0); // 이름
                        Long pId = (Long) familyModel.getValueAt(row, 3);      // ID (4번째 컬럼)

                        // 상세 보기 팝업 열기
                        new PatientDetailDialog(
                                (JFrame) SwingUtilities.getWindowAncestor(CaregiverPanel.this),
                                pName, pId, controller // CaregiverController 전달
                        ).setVisible(true);
                    }
                }
            }
        });

        panel.add(new JScrollPane(familyTable), BorderLayout.CENTER);

        refreshBtn.addActionListener(e -> loadFamilyData());
        loadFamilyData(); // 초기 로드
        return panel;
    }

    // --------------------------------------------------------
    // [탭 2] 연결 요청 관리 패널
    // --------------------------------------------------------
    private JPanel createRequestPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 상단 버튼 영역
        JPanel btnBox = new JPanel();
        JButton refreshBtn = new JButton("요청 새로고침");
        JButton acceptBtn = new JButton("✅ 수락");
        JButton rejectBtn = new JButton("❌ 거절");

        btnBox.add(refreshBtn);
        btnBox.add(acceptBtn);
        btnBox.add(rejectBtn);
        panel.add(btnBox, BorderLayout.NORTH);

        // 요청 목록 테이블
        String[] cols = {"환자 ID", "이름", "신청 상태", "ASSIGN_ID"};
        requestModel = new DefaultTableModel(cols, 0);
        requestTable = new JTable(requestModel);
        requestTable.setRowHeight(25);

        panel.add(new JScrollPane(requestTable), BorderLayout.CENTER);

        // 버튼 이벤트 연결
        acceptBtn.addActionListener(e -> processRequest(true));
        rejectBtn.addActionListener(e -> processRequest(false));
        refreshBtn.addActionListener(e -> loadRequests());

        loadRequests(); // 초기 로드
        return panel;
    }

    // ========================================================
    // 데이터 로드 및 처리 메서드
    // ========================================================

    // 내 가족 데이터 로드
    private void loadFamilyData() {
        familyModel.setRowCount(0);
        List<FamilySummary> list = controller.getMyFamily(caregiver.getId());

        for (FamilySummary f : list) {
            // [수정] 행을 추가할 때 f.getPatientId()를 마지막에 넣어줍니다.
            familyModel.addRow(new Object[]{
                    f.getName(),
                    f.getRiskLevel(),
                    f.getDescription(),
                    f.getPatientId() // [중요] ID 추가
            });
        }
    }

    // 요청 목록 로드
    private void loadRequests() {
        requestModel.setRowCount(0);
        List<FamilySummary> list = controller.getPendingRequests(caregiver.getId());
        for (FamilySummary f : list) {
            requestModel.addRow(new Object[]{ f.getLoginId(), f.getName(), "대기중", f.getAssignmentId() });
        }
    }

    // 수락/거절 처리
    private void processRequest(boolean accept) {
        int row = requestTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "처리할 요청을 선택해주세요.");
            return;
        }

        Long assignId = (Long) requestModel.getValueAt(row, 3);

        // 컨트롤러 호출
        controller.reply(assignId, accept);

        String msg = accept ? "✅ 수락되었습니다. 가족 목록 탭을 확인하세요." : "❌ 거절되었습니다.";
        JOptionPane.showMessageDialog(this, msg);

        // 화면 갱신
        loadRequests();
        loadFamilyData();
    }
}