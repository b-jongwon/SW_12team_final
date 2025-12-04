package ui;

import presentation.controller.DoctorController;
import domain.service.DoctorService.PatientSummary;
import domain.user.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;

public class DoctorPanel extends JPanel {
    private User doctor;
    private final DoctorController controller = new DoctorController();

    // UI 컴포넌트
    private JTable patientTable; // 수락된 환자
    private DefaultTableModel patientModel;

    private JTable requestTable; // 대기중인 요청
    private DefaultTableModel requestModel;

    private JTextArea noteArea;
    private Long selectedPatientId = null;

    public DoctorPanel(User doctor) {
        this.doctor = doctor;
        setLayout(new BorderLayout());

        // 1. 상단
        JLabel titleLabel = new JLabel("👨‍⚕️ " + doctor.getName() + " 선생님의 진료실");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
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
        // [오른쪽] 상세 작업 (기존 코드 유지)
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
        // [이벤트] 오른쪽 버튼 액션
        // ----------------------------------------------------
        saveNoteBtn.addActionListener(e -> {
            if (selectedPatientId == null) {
                JOptionPane.showMessageDialog(this, "담당 환자 탭에서 환자를 선택해주세요.");
                return;
            }
            String content = noteArea.getText().trim();
            if (content.isEmpty()) return;
            controller.saveNote(doctor.getId(), selectedPatientId, content);
            JOptionPane.showMessageDialog(this, "저장되었습니다.");
            noteArea.setText("");
        });

        scheduleBtn.addActionListener(e -> {
            if (selectedPatientId == null) {
                JOptionPane.showMessageDialog(this, "환자를 선택해주세요.");
                return;
            }
            String dateStr = JOptionPane.showInputDialog("예약 날짜 (yyyy-MM-ddTHH:mm):");
            if (dateStr != null) {
                try {
                    controller.scheduleExam(doctor.getId(), selectedPatientId, LocalDateTime.parse(dateStr), "검사");
                    JOptionPane.showMessageDialog(this, "예약되었습니다.");
                } catch (Exception ex) { JOptionPane.showMessageDialog(this, "오류: " + ex.getMessage()); }
            }
        });
    }

    // --------------------------------------------------------
    // [탭 1] 내 담당 환자 목록 패널
    // --------------------------------------------------------
    private JPanel createMyPatientPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JButton refreshBtn = new JButton("새로고침");
        panel.add(refreshBtn, BorderLayout.NORTH);

        String[] cols = {"ID", "이름", "상태", "DB_ID"};
        patientModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        patientTable = new JTable(patientModel);

        // 클릭 시 환자 선택
        patientTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = patientTable.getSelectedRow();
                if (row != -1) {
                    selectedPatientId = (Long) patientModel.getValueAt(row, 3);
                    String name = (String) patientModel.getValueAt(row, 1);
                    noteArea.setBorder(BorderFactory.createTitledBorder("📝 " + name + "님 소견 작성"));
                }
            }
        });

        panel.add(new JScrollPane(patientTable), BorderLayout.CENTER);

        refreshBtn.addActionListener(e -> loadMyPatients());
        loadMyPatients(); // 초기 로드
        return panel;
    }

    // --------------------------------------------------------
    // [탭 2] 연결 요청 관리 패널
    // --------------------------------------------------------
    private JPanel createRequestPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JButton refreshBtn = new JButton("요청 새로고침");

        JPanel btnBox = new JPanel();
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

        // 수락 버튼 이벤트
        acceptBtn.addActionListener(e -> processRequest(true));
        rejectBtn.addActionListener(e -> processRequest(false));
        refreshBtn.addActionListener(e -> loadRequests());

        loadRequests(); // 초기 로드
        return panel;
    }

    // 데이터 로드: 내 환자
    private void loadMyPatients() {
        patientModel.setRowCount(0);
        selectedPatientId = null;
        List<PatientSummary> list = controller.getMyPatients(doctor.getId());
        for (PatientSummary p : list) {
            patientModel.addRow(new Object[]{p.getLoginId(), p.getName(), p.getStatus(), p.getRealId()});
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

    private void processRequest(boolean accept) {
        int row = requestTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "처리할 요청을 선택해주세요.");
            return;
        }

        // 테이블에서 ASSIGN_ID 가져오기
        Long assignId = (Long) requestModel.getValueAt(row, 3);

        // 1. 컨트롤러 호출 (상태 변경)
        controller.reply(assignId, accept);

        String msg = accept ? "✅ 수락되었습니다. 담당 환자 탭을 확인하세요." : "❌ 거절되었습니다.";
        JOptionPane.showMessageDialog(this, msg);

        // 2. [UI 갱신] 두 테이블 모두 새로고침
        // (요청 목록에서는 사라지고, 수락했다면 환자 목록에는 추가됨)
        loadRequests();   // 요청 대기열 갱신 (사라짐)
        loadMyPatients(); // 환자 목록 갱신 (나타남)
    }
}