package ui;

import presentation.controller.AdminController;
import domain.user.User;
import domain.content.Announcement;
import domain.content.ContentItem;
import domain.patient.RiskConfiguration;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminPanel extends JPanel {

    private final AdminController controller = new AdminController();
    private User admin;

    public AdminPanel(User admin) {
        this.admin = admin;
        setLayout(new BorderLayout());

        // 제목
        JLabel titleLabel = new JLabel("👑 시스템 관리자 모드 (" + admin.getName() + ")");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // 탭 패널
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("📢 공지사항 관리", createAnnouncementPanel());
        tabbedPane.addTab("📚 건강 콘텐츠 관리", createContentPanel());
        tabbedPane.addTab("⚙️ 위험도 기준 설정", createConfigPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }
    private JPanel createConfigPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 설명 라벨
        JLabel infoLabel = new JLabel("※ 환자의 위험도 분석에 사용되는 기준값(Threshold)을 수정합니다. 변경 즉시 적용됩니다.");
        infoLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(infoLabel, BorderLayout.NORTH);

        // 설정값 입력 폼
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // 각 항목별 입력 필드 생성
        JTextField bpSysField = new JTextField(String.valueOf(RiskConfiguration.BP_SYSTOLIC_THRESHOLD));
        JTextField bpDiaField = new JTextField(String.valueOf(RiskConfiguration.BP_DIASTOLIC_THRESHOLD));
        JTextField sugarField = new JTextField(String.valueOf(RiskConfiguration.SUGAR_THRESHOLD));
        JTextField bmiField = new JTextField(String.valueOf(RiskConfiguration.BMI_THRESHOLD));
        JTextField cholField = new JTextField(String.valueOf(RiskConfiguration.CHOLESTEROL_THRESHOLD));

        formPanel.add(new JLabel("고혈압 기준 (수축기):")); formPanel.add(bpSysField);
        formPanel.add(new JLabel("고혈압 기준 (이완기):")); formPanel.add(bpDiaField);
        formPanel.add(new JLabel("당뇨 기준 (혈당):"));     formPanel.add(sugarField);
        formPanel.add(new JLabel("비만 기준 (BMI):"));      formPanel.add(bmiField);
        formPanel.add(new JLabel("콜레스테롤 기준:"));      formPanel.add(cholField);

        panel.add(formPanel, BorderLayout.CENTER);

        // 저장 버튼
        JButton saveBtn = new JButton("💾 설정 저장 및 적용");
        saveBtn.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        saveBtn.setPreferredSize(new Dimension(0, 50));

        saveBtn.addActionListener(e -> {
            try {
                // 입력값 파싱 및 적용
                double sys = Double.parseDouble(bpSysField.getText());
                double dia = Double.parseDouble(bpDiaField.getText());
                double sugar = Double.parseDouble(sugarField.getText());
                double bmi = Double.parseDouble(bmiField.getText());
                double chol = Double.parseDouble(cholField.getText());

                // Static 변수 업데이트 (메모리 상 즉시 반영)
                RiskConfiguration.BP_SYSTOLIC_THRESHOLD = sys;
                RiskConfiguration.BP_DIASTOLIC_THRESHOLD = dia;
                RiskConfiguration.SUGAR_THRESHOLD = sugar;
                RiskConfiguration.BMI_THRESHOLD = bmi;
                RiskConfiguration.CHOLESTEROL_THRESHOLD = chol;

                RiskConfiguration.save();

                JOptionPane.showMessageDialog(this,
                        "설정이 변경되었습니다.\n이제부터 환자들의 위험도 분석 시 이 기준이 적용됩니다.");

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "올바른 숫자를 입력해주세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 20, 50));
        bottomPanel.add(saveBtn, BorderLayout.CENTER);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    // ----------------------------------------------------
    // 탭 1: 공지사항 관리 패널
    // ----------------------------------------------------
    private JPanel createAnnouncementPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 입력 폼
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        JTextField titleField = new JTextField();
        JTextField contentField = new JTextField();
        JButton addBtn = new JButton("공지 등록");

        inputPanel.add(new JLabel("공지 제목:")); inputPanel.add(titleField);
        inputPanel.add(new JLabel("공지 내용:")); inputPanel.add(contentField);

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(inputPanel, BorderLayout.CENTER);
        topContainer.add(addBtn, BorderLayout.EAST);
        panel.add(topContainer, BorderLayout.NORTH);

        // 목록 테이블
        String[] cols = {"ID", "제목", "내용", "작성일"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        // 이벤트: 등록
        addBtn.addActionListener(e -> {
            controller.announce(titleField.getText(), contentField.getText());
            JOptionPane.showMessageDialog(this, "공지사항이 등록되었습니다.");
            loadAnnouncements(model);
            titleField.setText(""); contentField.setText("");
        });

        loadAnnouncements(model); // 초기 로드
        return panel;
    }

    private void loadAnnouncements(DefaultTableModel model) {
        model.setRowCount(0);
        List<Announcement> list = controller.getAnnouncements();
        for (Announcement a : list) {
            model.addRow(new Object[]{a.getId(), a.getTitle(), a.getContent(), a.getCreatedAt()});
        }
    }

    // ----------------------------------------------------
    // 탭 2: 건강 콘텐츠 관리 패널
    // ----------------------------------------------------
    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 입력 폼
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        String[] cats = {"운동", "식단", "상식"};
        JComboBox<String> catCombo = new JComboBox<>(cats);
        JTextField titleField = new JTextField();
        JTextField descField = new JTextField();
        JButton addBtn = new JButton("콘텐츠 등록");

        inputPanel.add(new JLabel("카테고리:")); inputPanel.add(catCombo);
        inputPanel.add(new JLabel("제목:")); inputPanel.add(titleField);
        inputPanel.add(new JLabel("설명:")); inputPanel.add(descField);

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(inputPanel, BorderLayout.CENTER);
        topContainer.add(addBtn, BorderLayout.EAST);
        panel.add(topContainer, BorderLayout.NORTH);

        // 목록 테이블
        String[] cols = {"ID", "카테고리", "제목", "설명"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        // 이벤트: 등록
        addBtn.addActionListener(e -> {
            controller.addContent((String)catCombo.getSelectedItem(), titleField.getText(), descField.getText());
            JOptionPane.showMessageDialog(this, "건강 콘텐츠가 등록되었습니다.");
            loadContents(model);
            titleField.setText(""); descField.setText("");
        });

        loadContents(model); // 초기 로드
        return panel;
    }

    private void loadContents(DefaultTableModel model) {
        model.setRowCount(0);
        List<ContentItem> list = controller.getContents();
        for (ContentItem c : list) {
            model.addRow(new Object[]{c.getId(), c.getCategory(), c.getTitle(), c.getDescription()});
        }
    }
}