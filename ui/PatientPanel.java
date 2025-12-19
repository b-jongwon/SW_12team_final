package ui;

import presentation.controller.PatientController;
import presentation.controller.ReportController;
import presentation.controller.AssignmentController;
import domain.user.User;
import domain.patient.HealthRecord;
import domain.patient.RiskAssessment;
import domain.patient.ComplicationRisk;
import domain.patient.GroupComparisonResult;
import domain.patient.PatientAssignment;
import domain.service.AssignmentService.ConnectionSummary;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PatientPanel extends JPanel {

    private final PatientController patientController = new PatientController();
    private final ReportController reportController = new ReportController();
    private final AssignmentController assignmentController = new AssignmentController();

    private User user;

    public PatientPanel(User user) {
        this.user = user;
        setLayout(new BorderLayout());

        // ==========================================
        // 1. 상단: 데이터 입력 버튼
        // ==========================================
        JPanel topPanel = new JPanel();
        JButton addRecordBtn = new JButton("➕ 오늘의 건강 데이터 입력하기");
        addRecordBtn.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        addRecordBtn.setBackground(new Color(230, 240, 255));
        topPanel.add(addRecordBtn);
        add("North", topPanel);

        // ==========================================
        // 2. 중앙: 탭 패널
        // ==========================================
        JTabbedPane tabbedPane = new JTabbedPane();

        // 탭 1: 건강 기록
        tabbedPane.addTab("📋 건강 기록 조회", createHistoryPanel());

        // 탭 2: 위험도 분석
        tabbedPane.addTab("⚠️ 위험도 분석 결과", createRiskPanel());

        //추가: 합병증 위험도 분석
        tabbedPane.addTab("📉 합병증 위험도 분석", createComplicationPanel());
        // 탭 3: 또래 평균 비교
        tabbedPane.addTab("📊 또래 평균 비교", createComparePanel());

        // 탭 4: 연결 관리
        tabbedPane.addTab("🔗 주치의/보호자 연결", createConnectionPanel());

        // 탭 5: [NEW] 진료 및 예약 내역 (위치 수정됨!)
        tabbedPane.addTab("🏥 진료 및 예약", createMedicalPanel());

        add("Center", tabbedPane);

        // 이벤트 리스너
        addRecordBtn.addActionListener(e -> openInputDialog());
    }

    // ---------------------------------------------------------
    // 탭 1: 건강 기록 조회 패널
    // ---------------------------------------------------------
    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea output = new JTextArea();
        output.setEditable(false);
        output.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JButton refreshBtn = new JButton("목록 새로고침");
        refreshBtn.addActionListener(e -> {
            List<HealthRecord> list = patientController.getRecords(user.getId());
            output.setText("=== 📋 나의 건강 기록 히스토리 ===\n\n");
            if (list.isEmpty()) output.append("아직 입력된 기록이 없습니다.\n");
            else {
                for (HealthRecord r : list) {
                    output.append(r.summary() + "\n--------------------------------------------------\n");
                }
            }
        });

        panel.add(refreshBtn, BorderLayout.NORTH);
        panel.add(new JScrollPane(output), BorderLayout.CENTER);
        return panel;
    }

    // ---------------------------------------------------------
    // 탭 2: 위험도 분석 결과 패널
    // ---------------------------------------------------------
    private JPanel createRiskPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea output = new JTextArea();
        output.setEditable(false);
        output.setForeground(new Color(150, 50, 0));

        JButton checkBtn = new JButton("내 위험도 확인하기");
        checkBtn.addActionListener(e -> {
            List<RiskAssessment> risks = patientController.getRisk(user.getId());

            output.setText("=== ⚠️ 뇌졸중 위험도 분석 리포트 ===\n\n");
            if (risks.isEmpty()) output.append("분석된 데이터가 없습니다.\n");
            else {
                int count = 1;
                for (RiskAssessment r : risks) {
                    output.append(String.format("[%d회차 분석 결과]\n", count++));
                    output.append(" - 위험 레벨: " + r.getRiskLevel() + "\n");
                    output.append(" - 위험 점수: " + r.getRiskScore() + "점\n");
                    output.append(" - 상세 소견: " + r.getRecommendationSummary() + "\n");
                    output.append("--------------------------------------------------\n");
                }
            }
            output.setCaretPosition(output.getDocument().getLength());
        });

        panel.add(checkBtn, BorderLayout.NORTH);
        panel.add(new JScrollPane(output), BorderLayout.CENTER);
        return panel;
    }

    //위험도 분석 패널(추가)
    private JPanel createComplicationPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea output = new JTextArea();
        output.setEditable(false);
        output.setForeground(new Color(0, 100, 50)); // 초록색 계열로 구분

        JButton checkBtn = new JButton("합병증 위험도 확인하기");
        checkBtn.addActionListener(e -> {
            // Controller를 통해 합병증 위험도 데이터를 가져옴
            List<ComplicationRisk> compRisks = patientController.getCompRisk(user.getId());

            output.setText("=== 📉 합병증(심혈관 등) 위험도 분석 ===\n\n");
            if (compRisks.isEmpty()) {
                output.append("분석된 데이터가 없습니다.\n(건강 기록을 입력하면 자동으로 분석됩니다)\n");
            } else {
                int count = 1;
                for (ComplicationRisk r : compRisks) {
                    output.append(String.format("[%d회차 분석]\n", count++));
                    output.append(" - 분석 항목: " + r.getComplicationType() + "\n");
                    output.append(" - 위험 점수: " + r.getProbability() + "\n");
                    output.append(" - 분석 결과: " + r.getRecommendation() + "\n"); // 예: "위험도: 높음"
                    output.append("--------------------------------------------------\n");
                }
            }
            // 스크롤을 맨 아래로 이동
            output.setCaretPosition(output.getDocument().getLength());
        });

        panel.add(checkBtn, BorderLayout.NORTH);
        panel.add(new JScrollPane(output), BorderLayout.CENTER);
        return panel;
    }

    // ---------------------------------------------------------
    // 탭 3: 또래 평균 비교 패널
    // ---------------------------------------------------------
    private JPanel createComparePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea output = new JTextArea();
        output.setEditable(false);

        JButton loadBtn = new JButton("또래 비교 리포트 보기");
        JButton createTestBtn = new JButton("비교 분석 요청 (테스트용)");

        JPanel btnPanel = new JPanel();
        btnPanel.add(loadBtn);
        btnPanel.add(createTestBtn);

        loadBtn.addActionListener(e -> {
            List<GroupComparisonResult> groups = reportController.getGroup(user.getId());
            output.setText("=== 📊 또래 그룹 비교 분석 ===\n\n");
            if (groups.isEmpty()) output.append("생성된 비교 리포트가 없습니다.\n");
            else {
                for (GroupComparisonResult g : groups) {
                    output.append("[그룹: " + g.getGroupKey() + "]\n");
                    output.append("나의 수치: " + g.getPatientMetric() + "\n");
                    output.append("그룹 평균: " + g.getGroupAverage() + "\n");
                    output.append("상위: " + String.format("%.1f", g.getPercentile()) + "%\n\n");
                }
            }
        });

        createTestBtn.addActionListener(e -> {
            reportController.createGroup(user.getId(), "40대 남성 평균", 135.0, 120.0, "GraphData");
            JOptionPane.showMessageDialog(this, "비교 분석 완료.");
        });

        panel.add(btnPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(output), BorderLayout.CENTER);
        return panel;
    }

    // ---------------------------------------------------------
    // 탭 4: 주치의/보호자 연결 패널
    // ---------------------------------------------------------
    private JPanel createConnectionPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("새로운 연결 신청"));

        JTextField docField = new JTextField();
        JTextField careField = new JTextField();
        JButton connectBtn = new JButton("신청하기");

        inputPanel.add(new JLabel("👨‍⚕️ 주치의 ID:")); inputPanel.add(docField);
        inputPanel.add(new JLabel("🏡 보호자 ID:")); inputPanel.add(careField);
        inputPanel.add(new JLabel("")); inputPanel.add(connectBtn);

        panel.add(inputPanel, BorderLayout.NORTH);

        String[] cols = {"구분", "이름(ID)", "현재 상태"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("📋 내 연결 현황"));

        panel.add(scrollPane, BorderLayout.CENTER);

        JButton refreshBtn = new JButton("현황 새로고침");
        panel.add(refreshBtn, BorderLayout.SOUTH);

        Runnable loadStatus = () -> {
            model.setRowCount(0);
            List<ConnectionSummary> list = assignmentController.getStatus(user.getId());
            for (ConnectionSummary s : list) {
                model.addRow(new Object[]{s.getRole(), s.getName(), s.getStatus()});
            }
        };

        refreshBtn.addActionListener(e -> loadStatus.run());

        connectBtn.addActionListener(e -> {
            try {
                assignmentController.requestConnection(user.getId(), docField.getText().trim(), careField.getText().trim());
                JOptionPane.showMessageDialog(this, "신청되었습니다! (대기 중)");
                docField.setText(""); careField.setText("");
                loadStatus.run();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "오류: " + ex.getMessage());
            }
        });

        loadStatus.run();
        return panel;
    }

    // ---------------------------------------------------------
    // [NEW] 탭 5: 진료 및 예약 내역 패널
    // ---------------------------------------------------------
    private JPanel createMedicalPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));

        // 1. 의사 소견 테이블
        String[] noteCols = {"작성일", "소견 내용"};
        DefaultTableModel noteModel = new DefaultTableModel(noteCols, 0);
        JTable noteTable = new JTable(noteModel);
        JScrollPane noteScroll = new JScrollPane(noteTable);
        noteScroll.setBorder(BorderFactory.createTitledBorder("📝 의사 선생님의 소견"));

        // 2. 검사 예약 테이블
        String[] examCols = {"예약 일시", "검사 내용", "상태"};
        DefaultTableModel examModel = new DefaultTableModel(examCols, 0);
        JTable examTable = new JTable(examModel);
        JScrollPane examScroll = new JScrollPane(examTable);
        examScroll.setBorder(BorderFactory.createTitledBorder("📅 잡혀있는 검사 일정"));

        panel.add(noteScroll);
        panel.add(examScroll);

        // 3. 하단 새로고침 버튼
        JButton refreshBtn = new JButton("내역 새로고침");

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(panel, BorderLayout.CENTER);
        wrapper.add(refreshBtn, BorderLayout.SOUTH);

        Runnable loadData = () -> {
            noteModel.setRowCount(0);
            var notes = patientController.getMyNotes(user.getId());
            for (var n : notes) noteModel.addRow(new Object[]{n.getCreatedAt(), n.getContent()});

            examModel.setRowCount(0);
            var exams = patientController.getMyExams(user.getId());
            for (var e : exams) examModel.addRow(new Object[]{e.getExamDate(), e.getDescription(), e.getStatus()});
        };

        refreshBtn.addActionListener(e -> loadData.run());
        loadData.run();

        return wrapper;
    }

    // ==========================================
    // [헬퍼] 입력 다이얼로그
    // ==========================================
    private void openInputDialog() {
        JPanel inputPanel = new JPanel(new GridLayout(0, 2, 5, 5));

        JTextField sysField = new JTextField();
        JTextField diaField = new JTextField();
        JTextField sugarField = new JTextField();
        String[] yesNo = {"No", "Yes"};
        JComboBox<String> smokeCombo = new JComboBox<>(yesNo);
        String[] drinkOptions = {"None", "Occasional", "Frequent"};
        JComboBox<String> drinkCombo = new JComboBox<>(drinkOptions);
        String[] activityOptions = {"Low", "Medium", "High"};
        JComboBox<String> activityCombo = new JComboBox<>(activityOptions);
        JTextField riskField = new JTextField("없음");
        JTextField heightField = new JTextField();
        JTextField weightField = new JTextField();

        inputPanel.add(new JLabel("수축기 혈압:")); inputPanel.add(sysField);
        inputPanel.add(new JLabel("이완기 혈압:")); inputPanel.add(diaField);
        inputPanel.add(new JLabel("혈당 (mg/dL):")); inputPanel.add(sugarField);
        inputPanel.add(new JLabel("흡연:")); inputPanel.add(smokeCombo);
        inputPanel.add(new JLabel("음주:")); inputPanel.add(drinkCombo);
        inputPanel.add(new JLabel("활동량:")); inputPanel.add(activityCombo);
        inputPanel.add(new JLabel("기타 위험요인:")); inputPanel.add(riskField);
        inputPanel.add(new JLabel("키 (m):")); inputPanel.add(heightField);
        inputPanel.add(new JLabel("몸무게 (kg):")); inputPanel.add(weightField);

        int result = JOptionPane.showConfirmDialog(this, inputPanel, "건강 데이터 입력", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                int sys = Integer.parseInt(sysField.getText().trim());
                int dia = Integer.parseInt(diaField.getText().trim());
                double sugar = Double.parseDouble(sugarField.getText().trim());
                String smoking = (String) smokeCombo.getSelectedItem();
                String drinking = (String) drinkCombo.getSelectedItem();
                String activity = (String) activityCombo.getSelectedItem();
                String riskFactors = riskField.getText().trim();
                double height = Double.parseDouble(heightField.getText().trim());
                double weight = Double.parseDouble(weightField.getText().trim());

                patientController.addRecord(user.getId(), sys, dia, sugar, smoking, drinking, activity, riskFactors, height, weight);
                JOptionPane.showMessageDialog(this, "저장 및 분석 완료!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "입력 오류: " + ex.getMessage());
            }
        }
    }
}