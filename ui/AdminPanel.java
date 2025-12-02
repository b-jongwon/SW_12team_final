package ui;

import presentation.controller.AdminController;
import domain.user.User;
import domain.content.Announcement;
import domain.content.ContentItem;

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

        add(tabbedPane, BorderLayout.CENTER);
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