package ui;

import presentation.controller.DoctorController;
import domain.service.DoctorService.PatientSummary;
import domain.user.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter; // 날짜 포맷팅을 위해 추가
import java.util.List;

public class DoctorPanel extends JPanel {
    private User doctor;
    private final DoctorController controller = new DoctorController();

    // UI 컴포넌트
    private JTable patientTable;
    private DefaultTableModel tableModel; // patientModel 변수명을 tableModel로 통일
    private JTable requestTable;
    private DefaultTableModel requestModel;
    private JTextArea noteArea;

    public DoctorPanel(User doctor) {
        this.doctor = doctor;
        setLayout(new BorderLayout());

        // 1. 상단
        JLabel titleLabel = new JLabel("👨‍⚕️ " + doctor.getName() + " 선생님의 진료실");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // 2. 중앙 SplitPane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(450);

        // ========================================================
        // [왼쪽] 탭 패널 (담당 환자 vs 연결 요청)
        // ========================================================
        JTabbedPane leftTab = new JTabbedPane();
        leftTab.addTab("담당 환자 목록", createMyPatientPanel());
        leftTab.addTab("🔔 신규 연결 요청", createRequestPanel());

        splitPane.setLeftComponent(leftTab);

        // ========================================================
        // [오른쪽] 상세 작업
        // ========================================================
        JPanel rightPanel = new JPanel(new BorderLayout());
        noteArea = new JTextArea();
        noteArea.setBorder(BorderFactory.createTitledBorder("📝 진료 소견 / 메모 작성"));

        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        JButton saveNoteBtn = new JButton("소견 저장");
        JButton scheduleBtn = new JButton("📅 다음 검사 예약");
        btnPanel.add(saveNoteBtn);
        btnPanel.add(scheduleBtn);

        rightPanel.add(new JScrollPane(noteArea), BorderLayout.CENTER);
        rightPanel.add(btnPanel, BorderLayout.SOUTH);
        splitPane.setRightComponent(rightPanel);

        add(splitPane, BorderLayout.CENTER);

        // ----------------------------------------------------
        // [이벤트] 소견 저장 버튼 (버그 수정됨)
        // ----------------------------------------------------
        saveNoteBtn.addActionListener(e -> {
            // [핵심 수정] 변수에 의존하지 않고, 버튼 누를 때 테이블 확인
            int row = patientTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "먼저 왼쪽 '담당 환자 목록' 탭에서 환자를 선택해주세요.");
                return;
            }

            // 테이블의 3번째 컬럼(DB_ID)에서 ID 직접 가져오기
            Long targetId = (Long) tableModel.getValueAt(row, 3);
            String content = noteArea.getText().trim();

            if (content.isEmpty()) {
                JOptionPane.showMessageDialog(this, "소견 내용을 입력해주세요.");
                return;
            }

            controller.saveNote(doctor.getId(), targetId, content);
            JOptionPane.showMessageDialog(this, "진료 소견이 저장되었습니다.");
            noteArea.setText("");
        });

        // ----------------------------------------------------
        // [이벤트] 검사 예약 버튼 (버그 수정됨)
        // ----------------------------------------------------
        scheduleBtn.addActionListener(e -> {
            int row = patientTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "먼저 왼쪽 '담당 환자 목록' 탭에서 환자를 선택해주세요.");
                return;
            }

            Long targetId = (Long) tableModel.getValueAt(row, 3);

            // 날짜 입력 편의성 개선 (공백 입력 가능)
            String dateStr = JOptionPane.showInputDialog("예약 날짜 (yyyy-MM-dd HH:mm):");
            if (dateStr != null && !dateStr.isEmpty()) {
                try {
                    // "2025-10-25 14:30" 형식 지원
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    LocalDateTime date = LocalDateTime.parse(dateStr, formatter);

                    controller.scheduleExam(doctor.getId(), targetId, date, "검사");
                    JOptionPane.showMessageDialog(this, "예약되었습니다.");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "날짜 형식이 틀렸습니다. (예: 2025-10-25 14:30)");
                }
            }
        });
    }

    // --------------------------------------------------------
    // [탭 1] 내 담당 환자 목록 패널
    // --------------------------------------------------------
    private JPanel createMyPatientPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 상단 버튼 패널
        JPanel topBtn = new JPanel();
        JButton refreshBtn = new JButton("새로고침");
        JButton sortBtn = new JButton("⚠️ 위험도순 정렬");
        topBtn.add(refreshBtn);
        topBtn.add(sortBtn);
        panel.add(topBtn, BorderLayout.NORTH);

        String[] cols = {"ID", "이름", "상태", "DB_ID"};
        tableModel = new DefaultTableModel(cols, 0) { // 변수명 tableModel로 통일
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        patientTable = new JTable(tableModel);

        // [NEW] 더블클릭 시 상세 보기 팝업 (히스토리 보기)
        patientTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = patientTable.getSelectedRow();
                if (row != -1) {
                    // 클릭 시 오른쪽 메모장 제목 변경 (시각적 피드백)
                    String name = (String) tableModel.getValueAt(row, 1);
                    noteArea.setBorder(BorderFactory.createTitledBorder("📝 " + name + "님 소견 작성"));

                    // 더블클릭 시 상세 팝업
                    if (evt.getClickCount() == 2) {
                        Long pId = (Long) tableModel.getValueAt(row, 3);
                        new PatientDetailDialog(
                                (JFrame) SwingUtilities.getWindowAncestor(DoctorPanel.this),
                                name, pId, controller
                        ).setVisible(true);
                    }
                }
            }
        });

        panel.add(new JScrollPane(patientTable), BorderLayout.CENTER);

        refreshBtn.addActionListener(e -> loadMyPatients());

        // 위험도 정렬 로직
        sortBtn.addActionListener(e -> {
            List<PatientSummary> list = controller.getMyPatients(doctor.getId());
            list.sort((p1, p2) -> {
                int s1 = getRiskScore(p1.getStatus());
                int s2 = getRiskScore(p2.getStatus());
                return Integer.compare(s2, s1); // 내림차순
            });
            updatePatientTable(list);
        });

        loadMyPatients(); // 초기 로드
        return panel;
    }

    // --------------------------------------------------------
    // [탭 2] 연결 요청 관리 패널
    // --------------------------------------------------------
    private JPanel createRequestPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel btnBox = new JPanel();
        JButton refreshBtn = new JButton("요청 새로고침");
        JButton acceptBtn = new JButton("✅ 수락");
        JButton rejectBtn = new JButton("❌ 거절");
        btnBox.add(refreshBtn);
        btnBox.add(acceptBtn);
        btnBox.add(rejectBtn);
        panel.add(btnBox, BorderLayout.NORTH);

        String[] cols = {"환자 ID", "이름", "신청 상태", "ASSIGN_ID"};
        requestModel = new DefaultTableModel(cols, 0);
        requestTable = new JTable(requestModel);
        panel.add(new JScrollPane(requestTable), BorderLayout.CENTER);

        acceptBtn.addActionListener(e -> processRequest(true));
        rejectBtn.addActionListener(e -> processRequest(false));
        refreshBtn.addActionListener(e -> loadRequests());

        loadRequests();
        return panel;
    }

    // 데이터 로드: 내 환자
    private void loadMyPatients() {
        List<PatientSummary> list = controller.getMyPatients(doctor.getId());
        updatePatientTable(list);
    }

    private void updatePatientTable(List<PatientSummary> list) {
        tableModel.setRowCount(0);
        for (PatientSummary p : list) {
            tableModel.addRow(new Object[]{p.getLoginId(), p.getName(), p.getStatus(), p.getRealId()});
        }
    }

    // 데이터 로드: 요청 목록
    private void loadRequests() {
        requestModel.setRowCount(0);
        List<PatientSummary> list = controller.getPendingRequests(doctor.getId());
        for (PatientSummary p : list) {
            requestModel.addRow(new Object[]{p.getLoginId(), p.getName(), "대기중", p.getAssignmentId()});
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
        controller.reply(assignId, accept);

        String msg = accept ? "수락되었습니다." : "거절되었습니다.";
        JOptionPane.showMessageDialog(this, msg);

        loadRequests();
        loadMyPatients();
    }

    private int getRiskScore(String status) {
        if ("고위험".equals(status)) return 3;
        if ("주의".equals(status)) return 2;
        if ("정상".equals(status)) return 1;
        return 0;
    }
}