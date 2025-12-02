package ui;

import presentation.controller.DoctorController;
import domain.user.User;
import domain.service.DoctorService.PatientSummary; // DTO 임포트

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;

public class DoctorPanel extends JPanel {
    private User doctor;
    private final DoctorController controller = new DoctorController();

    // UI 컴포넌트
    private JTable patientTable;
    private DefaultTableModel tableModel;
    private JTextArea noteArea;

    private Long selectedPatientId = null; // 선택된 환자의 실제 ID

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
        splitPane.setDividerLocation(400);

        // [왼쪽] 환자 목록
        JPanel leftPanel = new JPanel(new BorderLayout());
        JButton refreshBtn = new JButton("목록 새로고침"); // 새로고침 버튼
        leftPanel.add(refreshBtn, BorderLayout.NORTH);

        // 테이블 모델 (가짜 데이터 제거함)
        String[] colNames = {"ID", "이름", "위험도", "DB_ID(Hidden)"};
        tableModel = new DefaultTableModel(colNames, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        patientTable = new JTable(tableModel);

        // 테이블 선택 이벤트
        patientTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = patientTable.getSelectedRow();
                if (row != -1) {
                    selectedPatientId = (Long) tableModel.getValueAt(row, 3); // DB_ID 가져오기
                    String pName = (String) tableModel.getValueAt(row, 1);
                    noteArea.setBorder(BorderFactory.createTitledBorder("📝 " + pName + "님 소견 작성"));
                }
            }
        });

        leftPanel.add(new JScrollPane(patientTable), BorderLayout.CENTER);

        // [오른쪽] 상세 작업 (기존 유지)
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

        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);
        add(splitPane, BorderLayout.CENTER);

        // ==========================================
        // 이벤트 리스너
        // ==========================================

        // 1. 목록 새로고침 (진짜 데이터 로드)
        refreshBtn.addActionListener(e -> loadPatientList());

        // 2. 소견 저장
        saveNoteBtn.addActionListener(e -> {
            if (selectedPatientId == null) {
                JOptionPane.showMessageDialog(this, "환자를 선택해주세요.");
                return;
            }
            String content = noteArea.getText().trim();
            if (content.isEmpty()) return;

            controller.saveNote(doctor.getId(), selectedPatientId, content);
            JOptionPane.showMessageDialog(this, "저장되었습니다.");
            noteArea.setText("");
        });

        // 3. 검사 예약
        scheduleBtn.addActionListener(e -> {
            if (selectedPatientId == null) {
                JOptionPane.showMessageDialog(this, "환자를 선택해주세요.");
                return;
            }
            String dateStr = JOptionPane.showInputDialog("예약 날짜 (yyyy-MM-ddTHH:mm):");
            if (dateStr != null && !dateStr.isEmpty()) {
                try {
                    controller.scheduleExam(doctor.getId(), selectedPatientId, LocalDateTime.parse(dateStr), "정기 검진");
                    JOptionPane.showMessageDialog(this, "예약되었습니다.");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "오류: " + ex.getMessage());
                }
            }
        });

        // 초기 로드
        loadPatientList();
    }

    // [메서드] 컨트롤러에서 진짜 데이터를 가져와 테이블에 채움
    private void loadPatientList() {
        tableModel.setRowCount(0);
        selectedPatientId = null;

        List<PatientSummary> patients = controller.getMyPatients(doctor.getId());

        if (patients.isEmpty()) {
            // 데이터가 없을 때 (배정된 환자가 없음)
        } else {
            for (PatientSummary p : patients) {
                tableModel.addRow(new Object[]{
                        p.getLoginId(),
                        p.getName(),
                        p.getStatus(),
                        p.getRealId()
                });
            }
        }
    }
}