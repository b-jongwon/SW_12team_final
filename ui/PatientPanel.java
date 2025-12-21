package ui;

import presentation.controller.PatientController;
import presentation.controller.ReportController;
import presentation.controller.AssignmentController;

import domain.user.User;
import domain.patient.HealthRecord;
import domain.patient.RiskAssessment;
import domain.patient.ComplicationRisk;
import domain.patient.GroupComparisonResult;
import domain.service.AssignmentService.ConnectionSummary;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.time.format.DateTimeFormatter;


public class PatientPanel extends JPanel {

    private final PatientController patientController = new PatientController();
    private final ReportController reportController = new ReportController();
    private final AssignmentController assignmentController = new AssignmentController();

    private final DateTimeFormatter timeFmt =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

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
        JButton alertBtn = new JButton("🔔 알림 내역 확인");
        alertBtn.setBackground(new Color(255, 250, 205));
        topPanel.add(addRecordBtn);
        topPanel.add(alertBtn);
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

        tabbedPane.addTab("📘 맞춤형 건강 가이드", createGuidePanel());

        add("Center", tabbedPane);

        // 이벤트 리스너
        addRecordBtn.addActionListener(e -> openInputDialog());

        // [NEW] 알림 버튼 클릭 시 다이얼로그 띄우기
        alertBtn.addActionListener(e -> {
            Window win = SwingUtilities.getWindowAncestor(this);
            new AlertHistoryDialog(win, user.getId()).setVisible(true);
        });

    }

    // ---------------------------------------------------------
    // 탭 1: 건강 기록 조회 패널
    // ---------------------------------------------------------
    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // [변경 1] 텍스트 영역(JTextArea) 대신 리스트 모델과 JList 생성
        DefaultListModel<HealthRecord> listModel = new DefaultListModel<>();
        JList<HealthRecord> historyList = new JList<>(listModel);

        // [변경 2] ★ 아까 만든 예쁜 카드 렌더러 장착!
        historyList.setCellRenderer(new HealthRecordRenderer());

        JButton refreshBtn = new JButton("목록 새로고침");
        refreshBtn.addActionListener(e -> {
            listModel.clear(); // 기존 목록 초기화
            List<HealthRecord> list = patientController.getRecords(user.getId());

            if (list.isEmpty()) {
                // 기록이 없을 때 안내 (리스트에는 텍스트를 못 넣으므로 팝업이나 빈 상태 유지)
                // 필요하다면 더미 데이터를 넣거나 메시지를 띄울 수 있음
            } else {
                // [변경 3] 최신순(날짜 내림차순)으로 정렬하여 보기 좋게 만듦
                list.sort((r1, r2) -> r2.getMeasuredAt().compareTo(r1.getMeasuredAt()));

                // 데이터를 모델에 추가 (이제 텍스트가 아니라 객체 자체를 넣음)
                for (HealthRecord r : list) {
                    listModel.addElement(r);
                }
            }
        });

        // 패널이 열릴 때 자동으로 한 번 로드
        refreshBtn.doClick();

        panel.add(refreshBtn, BorderLayout.NORTH);
        panel.add(new JScrollPane(historyList), BorderLayout.CENTER);
        return panel;
    }

    // ---------------------------------------------------------
    // 탭 2: 위험도 분석 결과 패널 (시각화 적용 버전)
    // ---------------------------------------------------------
    private JPanel createRiskPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 리스트와 모델 준비
        DefaultListModel<RiskViewItem> listModel = new DefaultListModel<>();
        JList<RiskViewItem> list = new JList<>(listModel);

        // ★ 아까 만든 합병증 렌더러 재사용 (모양 똑같이 예쁨)
        list.setCellRenderer(new ComplicationRenderer());
        list.setFixedCellHeight(100);

        JButton checkBtn = new JButton("내 뇌졸중 위험도 확인하기");
        checkBtn.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        checkBtn.setBackground(new Color(255, 240, 230)); // 연한 주황

        checkBtn.addActionListener(e -> {
            listModel.clear();
            List<RiskAssessment> risks = patientController.getRisk(user.getId());

            if (risks.isEmpty()) {
                JOptionPane.showMessageDialog(this, "분석된 데이터가 없습니다.");
            } else {
                int count = 1;
                for (RiskAssessment r : risks) {
                    // DB 데이터를 화면용 객체(RiskViewItem)로 변환
                    // (RiskAssessment에는 getRiskScore()가 있다고 가정)
                    double score = r.getRiskScore();

                    listModel.addElement(new RiskViewItem(
                            count++,
                            "뇌졸중 위험",  // 제목 통일
                            score,
                            r.getRiskLevel(),
                            r.getRecommendationSummary()
                    ));
                }
            }
        });

        // 관련 정보 보기 버튼은 그대로 유지
        JButton infoBtn = new JButton("ℹ️ 관련 정보 보기");
        infoBtn.addActionListener(evt -> {
            // ... 기존 로직 ...
            List<HealthRecord> recs = patientController.getRecords(user.getId());
            HealthRecord last = recs.isEmpty() ? null : recs.get(recs.size()-1);
            Window win = SwingUtilities.getWindowAncestor(this);
            new RiskInfoDialog(win, last).setVisible(true);
        });

        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(checkBtn);
        topPanel.add(infoBtn);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(list), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createComplicationPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        // 상단: 새로고침 버튼
        JButton refreshBtn = new JButton("최신 데이터로 분석 새로고침");
        refreshBtn.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        refreshBtn.setBackground(new Color(220, 255, 220));
        mainPanel.add(refreshBtn, BorderLayout.NORTH);

        // 중앙: 상세 분석 내용을 담을 패널 (초기엔 비어있음)
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        refreshBtn.addActionListener(e -> {
            contentPanel.removeAll(); // 기존 내용 지우기

            // 1. 최신 기록 가져오기
            List<HealthRecord> records = patientController.getRecords(user.getId());
            if (records.isEmpty()) {
                JLabel emptyMsg = new JLabel("분석할 데이터가 없습니다. 먼저 건강 데이터를 입력해주세요.");
                emptyMsg.setAlignmentX(Component.CENTER_ALIGNMENT);
                contentPanel.add(Box.createVerticalStrut(50));
                contentPanel.add(emptyMsg);
            } else {
                HealthRecord last = records.get(records.size() - 1); // 최신 데이터

                // 2. 합병증 위험도 계산 결과 가져오기 (리스트 중 마지막꺼)
                List<ComplicationRisk> risks = patientController.getCompRisk(user.getId());
                ComplicationRisk latestRisk = risks.isEmpty() ? null : risks.get(risks.size()-1);

                // --- UI 구성 시작 ---

                // (A) 종합 결과 카드 (크게)
                JPanel summaryPanel = new JPanel(new BorderLayout());
                summaryPanel.setBorder(BorderFactory.createTitledBorder(" 종합 분석 결과"));
                summaryPanel.setMaximumSize(new Dimension(800, 150));

                JLabel resultLabel = new JLabel();
                resultLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
                resultLabel.setHorizontalAlignment(SwingConstants.CENTER);

                if (latestRisk != null) {
                    resultLabel.setText("<html><center>" + latestRisk.getRecommendation() + "</center></html>");
                    // 점수에 따라 색상
                    if (latestRisk.getProbability() >= 70) resultLabel.setForeground(Color.RED);
                    else if (latestRisk.getProbability() >= 40) resultLabel.setForeground(Color.ORANGE);
                    else resultLabel.setForeground(new Color(0, 150, 0));
                }
                summaryPanel.add(resultLabel, BorderLayout.CENTER);
                contentPanel.add(summaryPanel);
                contentPanel.add(Box.createVerticalStrut(10));

                // (B) 항목별 상세 카드 (Grid Layout) - 혈압, 혈당, BMI, 습관
                JPanel detailGrid = new JPanel(new GridLayout(2, 2, 10, 10));
                detailGrid.setMaximumSize(new Dimension(800, 300));

                // 1. 혈압 상태
                detailGrid.add(createDetailCard(" 혈압 상태",
                        last.getSystolicBp() + "/" + last.getDiastolicBp(),
                        last.getSystolicBp() >= 140 ? "주의 필요 (고혈압)" : "정상 범위",
                        last.getSystolicBp() >= 140));

                // 2. 혈당 상태
                detailGrid.add(createDetailCard(" 공복 혈당",
                        last.getBloodSugar() + " mg/dL",
                        last.getBloodSugar() >= 126 ? "관리 필요 (당뇨)" : "정상 범위",
                        last.getBloodSugar() >= 126));

                // 3. 비만도
                detailGrid.add(createDetailCard(" 체질량(BMI)",
                        String.format("%.1f", last.getBmi()),
                        last.getBmi() >= 25 ? "체중 조절 권장" : "건강한 체중",
                        last.getBmi() >= 25));

                // 4. 생활 습관
                boolean badHabit = "Yes".equalsIgnoreCase(last.getSmoking());
                detailGrid.add(createDetailCard(" 생활 습관",
                        "흡연: " + last.getSmoking(),
                        badHabit ? "금연이 시급합니다" : "좋은 습관 유지 중",
                        badHabit));

                contentPanel.add(detailGrid);
            }

            contentPanel.revalidate();
            contentPanel.repaint();
        });

        // 초기 로드
        refreshBtn.doClick();

        return mainPanel;
    }
    // [Helper] 항목별 상세 카드를 예쁘게 만들어주는 메서드
    private JPanel createDetailCard(String title, String value, String status, boolean isDanger) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        card.setBackground(Color.WHITE);

        JLabel titleLbl = new JLabel(" " + title);
        titleLbl.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        titleLbl.setBorder(new EmptyBorder(5, 5, 5, 5));

        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("Verdana", Font.BOLD, 20));
        valueLbl.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel statusLbl = new JLabel(status);
        statusLbl.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        statusLbl.setHorizontalAlignment(SwingConstants.CENTER);
        statusLbl.setOpaque(true);
        statusLbl.setBorder(new EmptyBorder(5,0,5,0));

        if (isDanger) {
            statusLbl.setBackground(new Color(255, 230, 230)); // 연한 빨강 배경
            statusLbl.setForeground(Color.RED);
        } else {
            statusLbl.setBackground(new Color(230, 255, 230)); // 연한 초록 배경
            statusLbl.setForeground(new Color(0, 100, 0));
        }

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valueLbl, BorderLayout.CENTER);
        card.add(statusLbl, BorderLayout.SOUTH);

        return card;
    }
    // ---------------------------------------------------------
    // ---------------------------------------------------------
    // 탭 4: 또래 평균 비교 패널 (여러 기준 시뮬레이션 결과)
    // ---------------------------------------------------------
    private JPanel createComparePanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        JButton refreshBtn = new JButton("비교 시뮬레이션 실행");
        refreshBtn.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        mainPanel.add(refreshBtn, BorderLayout.NORTH);

        // 결과를 담을 스크롤 패널
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        mainPanel.add(new JScrollPane(listPanel), BorderLayout.CENTER);

        refreshBtn.addActionListener(e -> {
            listPanel.removeAll();

            // 서비스에서 시뮬레이션 결과 3종 세트 가져오기
            // (PatientCareService에 getSimulationResults 메서드가 있어야 함)
            List<GroupComparisonResult> simulations = patientController.getSimulations(user.getId());
            // Note: Controller에 getSimulations 메서드를 추가해서 Service의 getSimulationResults를 호출하게 연결해주세요.

            if (simulations.isEmpty()) {
                JLabel msg = new JLabel("비교할 데이터가 부족합니다.");
                msg.setAlignmentX(Component.CENTER_ALIGNMENT);
                listPanel.add(Box.createVerticalStrut(20));
                listPanel.add(msg);
            } else {
                for (GroupComparisonResult result : simulations) {
                    // 기존에 만들어둔 Renderer 재사용 (JList용이지만 여기서 패널처럼 씀)
                    CompareRenderer renderer = new CompareRenderer();
                    // JList가 없으므로 dummy 값 전달
                    Component comp = renderer.getListCellRendererComponent(null, result, 0, false, false);

                    // 레이아웃 보정을 위해 패널에 감싸기
                    JPanel wrapper = new JPanel(new BorderLayout());
                    wrapper.add(comp, BorderLayout.CENTER);
                    wrapper.setMaximumSize(new Dimension(1000, 120)); // 높이 고정

                    listPanel.add(wrapper);
                    listPanel.add(Box.createVerticalStrut(10)); // 간격
                }
            }
            listPanel.revalidate();
            listPanel.repaint();
        });

        // 초기 실행
        refreshBtn.doClick();

        return mainPanel;
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

        inputPanel.add(new JLabel("👨‍⚕️ 주치의 ID:"));
        inputPanel.add(docField);
        inputPanel.add(new JLabel("🏡 보호자 ID:"));
        inputPanel.add(careField);
        inputPanel.add(new JLabel(""));
        inputPanel.add(connectBtn);

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
                String statusKo;
                switch (s.getStatus()) {
                    case "ACCEPTED":
                        statusKo = "연결 완료";
                        break;
                    case "PENDING":
                        statusKo = "연결 대기 중";
                        break;
                    case "REJECTED":
                        statusKo = "연결 거절됨";
                        break;
                    default:
                        statusKo = s.getStatus();
                }

                model.addRow(new Object[]{
                        s.getRole(),
                        s.getName(),
                        statusKo
                });
            }

        };

        refreshBtn.addActionListener(e -> loadStatus.run());

        connectBtn.addActionListener(e -> {
            try {
                assignmentController.requestConnection(
                        user.getId(),
                        docField.getText().trim(),
                        careField.getText().trim()
                );
                JOptionPane.showMessageDialog(this, "신청되었습니다! (대기 중)");
                docField.setText("");
                careField.setText("");
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
            for (var n : notes)
                noteModel.addRow(new Object[]{
                        n.getCreatedAt().format(timeFmt),
                        n.getContent()
                });

            examModel.setRowCount(0);
            var exams = patientController.getMyExams(user.getId());
            for (var e : exams)
                examModel.addRow(new Object[]{e.getExamDate(), e.getDescription(), e.getStatus()});
        };

        refreshBtn.addActionListener(e -> loadData.run());
        loadData.run();
        return wrapper;
    }
    private JPanel createGuidePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 리스트 모델
        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> contentList = new JList<>(listModel);

        // 상세 내용 영역
        JTextArea detailArea = new JTextArea();
        detailArea.setEditable(false);
        detailArea.setLineWrap(true);
        detailArea.setFont(new Font("맑은 고딕", Font.PLAIN, 14));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(contentList), new JScrollPane(detailArea));
        split.setDividerLocation(250);

        JButton loadBtn = new JButton("내 맞춤 가이드 불러오기");

        loadBtn.addActionListener(e -> {
            listModel.clear();
            detailArea.setText("");

            // 컨트롤러 -> 서비스 -> 내 위험도에 맞는 글만 가져옴 (ALL + 내 위험도)
            List<domain.content.ContentItem> items = patientController.getContents(user.getId());

            if (items.isEmpty()) {
                listModel.addElement("등록된 맞춤 가이드가 없습니다.");
            } else {
                for (domain.content.ContentItem item : items) {
                    // 리스트에는 "[고위험] [식단] 제목" 형태로 표시
                    listModel.addElement(item.getSummary());
                }
            }
        });

        // 리스트 클릭 시 상세 내용 표시
        contentList.addListSelectionListener(evt -> {
            if (!evt.getValueIsAdjusting()) {
                int idx = contentList.getSelectedIndex();
                if (idx != -1) {
                    List<domain.content.ContentItem> items = patientController.getContents(user.getId());
                    if (idx < items.size()) {
                        domain.content.ContentItem selected = items.get(idx);
                        detailArea.setText(
                                "제목: " + selected.getTitle() + "\n" +
                                        "카테고리: " + selected.getCategory() + "\n" +
                                        "대상: " + selected.getTargetRisk() + "\n\n" +
                                        selected.getDescription()
                        );
                        detailArea.setCaretPosition(0);
                    }
                }
            }
        });

        panel.add(loadBtn, BorderLayout.NORTH);
        panel.add(split, BorderLayout.CENTER);

        return panel;
    }
    private void openInputDialog() {
        JPanel inputPanel = new JPanel(new GridLayout(0, 2, 5, 5));

        // [추가] 나이와 성별 필드
        JTextField ageField = new JTextField();
        String[] genders = {"Male", "Female"};
        JComboBox<String> genderCombo = new JComboBox<>(genders);

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

        // UI 배치
        inputPanel.add(new JLabel("나이 (세):"));
        inputPanel.add(ageField);
        inputPanel.add(new JLabel("성별:"));
        inputPanel.add(genderCombo);

        inputPanel.add(new JLabel("수축기 혈압:"));
        inputPanel.add(sysField);
        inputPanel.add(new JLabel("이완기 혈압:"));
        inputPanel.add(diaField);
        inputPanel.add(new JLabel("혈당 (mg/dL):"));
        inputPanel.add(sugarField);

        inputPanel.add(new JLabel("흡연:"));
        inputPanel.add(smokeCombo);
        inputPanel.add(new JLabel("음주:"));
        inputPanel.add(drinkCombo);
        inputPanel.add(new JLabel("활동량:"));
        inputPanel.add(activityCombo);

        inputPanel.add(new JLabel("기타 위험요인:"));
        inputPanel.add(riskField);
        inputPanel.add(new JLabel("키 (m):"));
        inputPanel.add(heightField);
        inputPanel.add(new JLabel("몸무게 (kg):"));
        inputPanel.add(weightField);

        int result = JOptionPane.showConfirmDialog(
                this,
                inputPanel,
                "건강 데이터 입력 (빈칸은 0으로 저장됨)",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (result == JOptionPane.OK_OPTION) {
            try {
                // [핵심] 빈칸 입력 시 0으로 처리하는 헬퍼 함수 사용 (아래 parseOrZero 참고)
                int age = parseOrZero(ageField.getText());
                String gender = (String) genderCombo.getSelectedItem();

                int sys = parseOrZero(sysField.getText());
                int dia = parseOrZero(diaField.getText());
                double sugar = parseDoubleOrZero(sugarField.getText());

                String smoking = (String) smokeCombo.getSelectedItem();
                String drinking = (String) drinkCombo.getSelectedItem();
                String activity = (String) activityCombo.getSelectedItem();
                String riskFactors = riskField.getText().trim();

                double height = parseDoubleOrZero(heightField.getText());
                double weight = parseDoubleOrZero(weightField.getText());

                // 컨트롤러 호출
                patientController.addRecord(
                        user.getId(),
                        age, gender, // 추가된 파라미터
                        sys, dia, sugar,
                        smoking, drinking, activity,
                        riskFactors, height, weight
                );

                JOptionPane.showMessageDialog(this, "저장 완료! (입력값 기반 분석 시작)");

            } catch (Exception ex) {
                // 혹시 모를 에러 방지
                JOptionPane.showMessageDialog(this, "오류 발생: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    // [유틸] 빈 문자열이면 0을 반환, 아니면 파싱 (입력 스트레스 방지)
    private int parseOrZero(String text) {
        if (text == null || text.trim().isEmpty()) return 0;
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            return 0; // 숫자가 아닌 이상한 문자 넣어도 0 처리
        }
    }

    private double parseDoubleOrZero(String text) {
        if (text == null || text.trim().isEmpty()) return 0.0;
        try {
            return Double.parseDouble(text.trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
    // ==========================================
    // [시각화용 내부 클래스 1] 데이터를 화면용으로 변환하는 객체
    // ==========================================
    class RiskViewItem {
        int round;          // 회차
        String title;       // 항목명 (예: 심혈관)
        double score;       // 점수 (0~100)
        String riskLevel;   // 위험 단계
        String advice;      // 조언

        public RiskViewItem(int round, String title, double score, String riskLevel, String advice) {
            this.round = round;
            this.title = title;
            this.score = score;
            this.riskLevel = riskLevel;
            this.advice = advice;
        }
    }

    // ==========================================
    // [시각화용 내부 클래스 2] 게이지 바(그래프)를 그려주는 렌더러
    // ==========================================
    class ComplicationRenderer extends JPanel implements ListCellRenderer<RiskViewItem> {
        private JLabel titleLabel = new JLabel();
        private JProgressBar scoreBar = new JProgressBar(0, 100);
        private JLabel detailLabel = new JLabel();

        public ComplicationRenderer() {
            setLayout(new BorderLayout(5, 5));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                    new EmptyBorder(10, 10, 10, 10)));
            setOpaque(true);

            // 제목 폰트
            titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 15));

            // 그래프 설정
            scoreBar.setStringPainted(true);
            scoreBar.setPreferredSize(new Dimension(100, 20));

            // 내용 폰트
            detailLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
            detailLabel.setForeground(Color.DARK_GRAY);

            add(titleLabel, BorderLayout.NORTH);
            add(scoreBar, BorderLayout.CENTER);
            add(detailLabel, BorderLayout.SOUTH);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends RiskViewItem> list, RiskViewItem value, int index, boolean isSelected, boolean cellHasFocus) {
            // 데이터 넣기
            titleLabel.setText(String.format("[%d회차] %s 분석", value.round, value.title));
            detailLabel.setText("결과: " + value.advice);

            scoreBar.setValue((int) value.score);
            scoreBar.setString("위험도 " + value.score + "점 (" + value.riskLevel + ")");

            // 점수에 따른 색상 변경 (신호등 색상)
            if (value.score >= 70) scoreBar.setForeground(new Color(220, 50, 50)); // 빨강
            else if (value.score >= 40) scoreBar.setForeground(Color.ORANGE);      // 주황
            else scoreBar.setForeground(new Color(50, 180, 50));                   // 초록

            // 선택 시 배경색
            if (isSelected) setBackground(new Color(230, 240, 255));
            else setBackground(Color.WHITE);

            return this;
        }
    }
    // ==========================================
    // 또래 비교 전용 렌더러 (막대 2개 비교)
    // ==========================================
    class CompareRenderer extends JPanel implements ListCellRenderer<GroupComparisonResult> {
        private JLabel dateLabel = new JLabel();
        private JLabel groupLabel = new JLabel();

        // 그래프 바 (최대값을 나중에 유동적으로 바꿀 예정)
        private JProgressBar myBar = new JProgressBar(0, 100);
        private JProgressBar avgBar = new JProgressBar(0, 100);

        public CompareRenderer() {
            setLayout(new BorderLayout(10, 10));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                    new EmptyBorder(15, 15, 15, 15)));
            setOpaque(true);

            // 1. 상단: 그룹명과 날짜
            JPanel topPanel = new JPanel(new BorderLayout());
            topPanel.setOpaque(false);
            groupLabel.setFont(new Font("맑은 고딕", Font.BOLD, 15));
            dateLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
            dateLabel.setForeground(Color.GRAY);

            topPanel.add(groupLabel, BorderLayout.WEST);
            topPanel.add(dateLabel, BorderLayout.EAST);
            add(topPanel, BorderLayout.NORTH);

            // 2. 중앙: 막대 그래프 2개
            JPanel barPanel = new JPanel(new GridLayout(2, 1, 5, 5));
            barPanel.setOpaque(false);

            // 스타일 설정
            myBar.setStringPainted(true);
            avgBar.setStringPainted(true);
            avgBar.setForeground(Color.LIGHT_GRAY); // 평균은 항상 회색

            barPanel.add(myBar);
            barPanel.add(avgBar);
            add(barPanel, BorderLayout.CENTER);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends GroupComparisonResult> list,
                                                      GroupComparisonResult value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            // 1. 텍스트 바인딩
            groupLabel.setText("그룹: " + value.getGroupKey());
            if (value.getCreatedAt() != null) {
                dateLabel.setText(value.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            } else {
                dateLabel.setText("-");
            }

            // 2. 항목별로 그래프 최대값(Max) 다르게 설정 (그래프가 꽉 차 보이게)
            int maxScale = 100; // 기본(위험도 점수 등)
            String unit = "점";

            if (value.getGroupKey().contains("BMI")) {
                maxScale = 50; // BMI는 50만 돼도 초고도비만이므로 스케일을 줄임
                unit = "";
            } else if (value.getGroupKey().contains("혈당")) {
                maxScale = 200; // 혈당은 200까지
                unit = "mg/dL";
            }

            myBar.setMaximum(maxScale);
            avgBar.setMaximum(maxScale);

            // 3. 값 설정 및 소수점 깔끔하게 자르기 (String.format 사용)
            myBar.setValue((int) value.getPatientMetric());
            myBar.setString(String.format("나의 수치: %.1f %s", value.getPatientMetric(), unit));

            avgBar.setValue((int) value.getGroupAverage());
            avgBar.setString(String.format("그룹 평균: %.1f %s", value.getGroupAverage(), unit));

            // 4. 색상 로직 (내가 평균보다 높으면 빨강, 낮으면 파랑)
            if (value.getPatientMetric() > value.getGroupAverage()) {
                myBar.setForeground(new Color(255, 100, 100)); // 높음(주의) -> 빨강
            } else {
                myBar.setForeground(new Color(100, 180, 255)); // 낮음(양호) -> 파랑
            }

            // 배경색 처리
            if (isSelected) setBackground(new Color(240, 245, 255));
            else setBackground(Color.WHITE);

            return this;
        }
    }
}
