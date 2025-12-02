package ui;

import presentation.controller.PatientController;
import presentation.controller.ReportController;
import presentation.controller.AssignmentController; // [추가]
import domain.user.User;
import domain.patient.HealthRecord;
import domain.patient.RiskAssessment;
import domain.patient.GroupComparisonResult;
import domain.patient.PatientAssignment; // [추가]

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PatientPanel extends JPanel {

    // 컨트롤러들 (DI 적용 전이라 직접 생성)
    private final PatientController patientController = new PatientController();
    private final ReportController reportController = new ReportController();
    private final AssignmentController assignmentController = new AssignmentController(); // [추가]

    private User user;

    public PatientPanel(User user) {
        this.user = user;
        setLayout(new BorderLayout());

        // ==========================================
        // 1. 상단: 데이터 입력 버튼 (공통)
        // ==========================================
        JPanel topPanel = new JPanel();
        JButton addRecordBtn = new JButton("➕ 오늘의 건강 데이터 입력하기");
        addRecordBtn.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        addRecordBtn.setBackground(new Color(230, 240, 255));
        topPanel.add(addRecordBtn);
        add("North", topPanel);

        // ==========================================
        // 2. 중앙: 탭 패널 (기능별 조회 분리)
        // ==========================================
        JTabbedPane tabbedPane = new JTabbedPane();

        // 탭 1: 나의 건강 기록 (Raw Data)
        JPanel historyPanel = createHistoryPanel();
        tabbedPane.addTab("📋 건강 기록 조회", historyPanel);

        // 탭 2: 뇌졸중 및 합병증 위험도 (Risk Analysis)
        JPanel riskPanel = createRiskPanel();
        tabbedPane.addTab("⚠️ 위험도 분석 결과", riskPanel);

        // 탭 3: 또래 평균 비교 (Report)
        JPanel comparePanel = createComparePanel();
        tabbedPane.addTab("📊 또래 평균 비교", comparePanel);

        // 탭 4: 연결 관리 (Assignment) [NEW]
        JPanel connectionPanel = createConnectionPanel();
        tabbedPane.addTab("🔗 주치의/보호자 연결", connectionPanel);

        add("Center", tabbedPane);

        // ==========================================
        // [이벤트] 건강 기록 입력 (다이얼로그)
        // ==========================================
        addRecordBtn.addActionListener(e -> openInputDialog());
    }

    // ---------------------------------------------------------
    // 탭 1: 건강 기록 조회 패널 구현
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
            if (list.isEmpty()) {
                output.append("아직 입력된 기록이 없습니다.\n");
            } else {
                for (HealthRecord r : list) {
                    output.append(r.summary() + "\n");
                    output.append("--------------------------------------------------\n");
                }
            }
        });

        panel.add(refreshBtn, BorderLayout.NORTH);
        panel.add(new JScrollPane(output), BorderLayout.CENTER);
        return panel;
    }

    // ---------------------------------------------------------
    // 탭 2: 위험도 분석 결과 조회 패널 구현
    // ---------------------------------------------------------
    private JPanel createRiskPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea output = new JTextArea();
        output.setEditable(false);
        output.setForeground(new Color(150, 50, 0)); // 경고색

        JButton checkBtn = new JButton("내 위험도 확인하기");

        checkBtn.addActionListener(e -> {
            // PatientCareService가 자동으로 생성한 RiskAssessment 조회
            List<RiskAssessment> risks = patientController.getRisk(user.getId());

            output.setText("=== ⚠️ 뇌졸중 위험도 분석 리포트 ===\n\n");
            if (risks.isEmpty()) {
                output.append("분석된 데이터가 없습니다. 먼저 건강 기록을 입력해주세요.\n");
            } else {
                // 가장 최신 것 하나만 보여줌
                RiskAssessment latest = risks.get(risks.size() - 1);
                output.append("최종 분석 일시: " + latest.getAssessedAt() + "\n");
                output.append("위험 레벨: [" + latest.getRiskLevel() + "]\n");
                output.append("위험 점수: " + latest.getRiskScore() + "점\n");
                output.append("분석 소견: " + latest.getRecommendationSummary() + "\n");
            }
        });

        panel.add(checkBtn, BorderLayout.NORTH);
        panel.add(new JScrollPane(output), BorderLayout.CENTER);
        return panel;
    }

    // ---------------------------------------------------------
    // 탭 3: 또래 평균 비교 패널 구현 (ReportController 사용)
    // ---------------------------------------------------------
    private JPanel createComparePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea output = new JTextArea();
        output.setEditable(false);

        JButton loadBtn = new JButton("또래 비교 리포트 보기");
        // 테스트를 위해 강제로 리포트 생성하는 버튼 (실제로는 서버가 배치로 돌림)
        JButton createTestBtn = new JButton("비교 분석 요청 (테스트용)");

        JPanel btnPanel = new JPanel();
        btnPanel.add(loadBtn);
        btnPanel.add(createTestBtn);

        loadBtn.addActionListener(e -> {
            List<GroupComparisonResult> groups = reportController.getGroup(user.getId());
            output.setText("=== 📊 또래 그룹 비교 분석 ===\n\n");
            if (groups.isEmpty()) {
                output.append("생성된 비교 리포트가 없습니다.\n");
            } else {
                for (GroupComparisonResult g : groups) {
                    output.append("[그룹: " + g.getGroupKey() + "]\n");
                    output.append("나의 수치: " + g.getPatientMetric() + "\n");
                    output.append("그룹 평균: " + g.getGroupAverage() + "\n");
                    output.append("상위: " + String.format("%.1f", g.getPercentile()) + "%\n\n");
                }
            }
        });

        // [테스트 기능] 버튼을 누르면 가상의 비교 데이터를 생성해줌
        createTestBtn.addActionListener(e -> {
            reportController.createGroup(user.getId(), "40대 남성 평균", 135.0, 120.0, "GraphData");
            JOptionPane.showMessageDialog(this, "비교 분석이 완료되었습니다. '보기' 버튼을 눌러주세요.");
        });

        panel.add(btnPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(output), BorderLayout.CENTER);
        return panel;
    }

    // ---------------------------------------------------------
    // 탭 4: 주치의/보호자 연결 패널 구현 [NEW]
    // ---------------------------------------------------------
    private JPanel createConnectionPanel() {
        JPanel panel = new JPanel(null); // 자유 배치

        JLabel infoLabel = new JLabel("담당 주치의와 보호자의 로그인 ID를 입력하여 연결하세요.");
        infoLabel.setBounds(20, 20, 400, 30);
        panel.add(infoLabel);

        // 의사 입력
        JLabel docLabel = new JLabel("👨‍⚕️ 주치의 ID:");
        docLabel.setBounds(20, 70, 100, 30);
        JTextField docField = new JTextField();
        docField.setBounds(130, 70, 150, 30);
        panel.add(docLabel);
        panel.add(docField);

        // 보호자 입력
        JLabel careLabel = new JLabel("🏡 보호자 ID:");
        careLabel.setBounds(20, 120, 100, 30);
        JTextField careField = new JTextField();
        careField.setBounds(130, 120, 150, 30);
        panel.add(careLabel);
        panel.add(careField);

        // 연결 버튼
        JButton connectBtn = new JButton("연결 신청");
        connectBtn.setBounds(130, 170, 150, 40);
        panel.add(connectBtn);

        // 결과 출력
        JTextArea statusArea = new JTextArea();
        statusArea.setEditable(false);
        statusArea.setBounds(20, 230, 400, 100);
        statusArea.setBorder(BorderFactory.createTitledBorder("연결 상태"));
        panel.add(statusArea);

        // 버튼 클릭 시 연결 시도
        connectBtn.addActionListener(e -> {
            String docId = docField.getText().trim();
            String careId = careField.getText().trim();

            if (docId.isEmpty() && careId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "적어도 하나의 ID는 입력해주세요.");
                return;
            }

            try {
                // AssignmentController를 통해 연결 (ID -> User 검색 -> Assignment 생성)
                PatientAssignment result = assignmentController.connectDoctorAndCaregiver(user.getId(), docId, careId);

                JOptionPane.showMessageDialog(this, "연결 정보가 저장되었습니다!");
                statusArea.setText("✅ 연결 성공!\n");
                if (result.getDoctorId() != null) statusArea.append("- 주치의 (DB ID): " + result.getDoctorId() + "\n");
                if (result.getCaregiverId() != null) statusArea.append("- 보호자 (DB ID): " + result.getCaregiverId() + "\n");

            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "오류: " + ex.getMessage());
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "알 수 없는 오류가 발생했습니다.");
            }
        });

        return panel;
    }

    // ==========================================
    // [헬퍼] 입력 다이얼로그 (팝업창)
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
                JOptionPane.showMessageDialog(this, "저장 및 분석 완료! 각 탭에서 결과를 확인하세요.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "입력 오류: " + ex.getMessage());
            }
        }
    }
}