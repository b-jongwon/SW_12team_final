package ui;

import presentation.controller.DoctorController;
import domain.service.DoctorService.PatientSummary; // DTO 임포트
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
    private JTable patientTable;
    private DefaultTableModel tableModel;
    private JTextArea noteArea;

    // 현재 선택된 환자의 실제 ID 저장용
    private Long selectedPatientId = null;

    public DoctorPanel(User doctor) {
        this.doctor = doctor;
        setLayout(new BorderLayout());

        // 1. 상단 제목
        JLabel titleLabel = new JLabel("👨‍⚕️ " + doctor.getName() + " 선생님의 진료실");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // 2. 중앙 SplitPane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(400);

        // [왼쪽] 환자 목록 패널
        JPanel leftPanel = new JPanel(new BorderLayout());

        // 새로고침 버튼 추가
        JButton refreshBtn = new JButton("목록 새로고침");
        leftPanel.add(refreshBtn, BorderLayout.NORTH);

        // 테이블 모델 설정 (ID, 이름, 상태)
        String[] colNames = {"환자 ID (Login)", "이름", "위험도 상태", "DB_ID(Hidden)"};
        tableModel = new DefaultTableModel(colNames, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        patientTable = new JTable(tableModel);

        // 마지막 열(DB_ID)은 숨기는 게 좋지만, 일단 개발 편의상 보이게 둡니다.
        // (숨기려면: patientTable.removeColumn(patientTable.getColumnModel().getColumn(3));)

        // 테이블 클릭 이벤트: 환자 선택
        patientTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = patientTable.getSelectedRow();
                if (row != -1) {
                    // 3번째 컬럼(DB_ID)에서 실제 ID를 가져옴
                    selectedPatientId = (Long) tableModel.getValueAt(row, 3);
                    String pName = (String) tableModel.getValueAt(row, 1);
                    noteArea.setBorder(BorderFactory.createTitledBorder("📝 " + pName + "님에 대한 소견 작성"));
                }
            }
        });

        leftPanel.add(new JScrollPane(patientTable), BorderLayout.CENTER);
        leftPanel.setBorder(BorderFactory.createTitledBorder("담당 환자 리스트"));

        // [오른쪽] 상세 작업 패널 (이전 코드 유지)
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
        // [이벤트] 데이터 로드 (새로고침)
        // ==========================================
        refreshBtn.addActionListener(e -> loadPatientList());

        // 초기 로드
        loadPatientList();

        // ==========================================
        // [이벤트] 소견 저장
        // ==========================================
        saveNoteBtn.addActionListener(e -> {
            if (selectedPatientId == null) {
                JOptionPane.showMessageDialog(this, "목록에서 환자를 선택해주세요.");
                return;
            }
            String content = noteArea.getText().trim();
            if (content.isEmpty()) return;

            controller.saveNote(doctor.getId(), selectedPatientId, content);
            JOptionPane.showMessageDialog(this, "진료 소견이 저장되었습니다.");
            noteArea.setText("");
        });

        // ==========================================
        // [이벤트] 검사 예약
        // ==========================================
        scheduleBtn.addActionListener(e -> {
            if (selectedPatientId == null) {
                JOptionPane.showMessageDialog(this, "목록에서 환자를 선택해주세요.");
                return;
            }
            String dateStr = JOptionPane.showInputDialog("예약 날짜 (yyyy-MM-ddTHH:mm):");
            if (dateStr != null && !dateStr.isEmpty()) {
                try {
                    controller.scheduleExam(doctor.getId(), selectedPatientId, LocalDateTime.parse(dateStr), "정기 검진");
                    JOptionPane.showMessageDialog(this, "검사가 예약되었습니다.");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "날짜 형식 오류: " + ex.getMessage());
                }
            }
        });
    }

    // [메서드] 실제 데이터 불러와서 테이블 채우기
    private void loadPatientList() {
        tableModel.setRowCount(0); // 초기화
        selectedPatientId = null;  // 선택 초기화

        // 컨트롤러를 통해 "진짜" 데이터 가져오기
        List<PatientSummary> patients = controller.getMyPatients(doctor.getId());

        if (patients.isEmpty()) {
            // 데이터가 없을 경우 안내
            // (아직 배정된 환자가 없으므로 빈 줄이 정상입니다)
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