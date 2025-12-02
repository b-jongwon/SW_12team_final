package ui;

import domain.user.User;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel = new JPanel(cardLayout);
    private JPanel topMenu = new JPanel(new FlowLayout(FlowLayout.LEFT));

    private User currentUser;

    public MainFrame(User user) {
        this.currentUser = user;

        // 창 제목에 역할과 이름 표시
        setTitle("뇌졸중 예방 시스템 - " + user.getName() + " [" + user.getRole() + "]");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        JButton logoutBtn = new JButton("🚪 로그아웃");
        logoutBtn.setBackground(new Color(255, 220, 220)); // 연한 빨간색 (강조)

        logoutBtn.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "정말 로그아웃 하시겠습니까?",
                    "로그아웃 확인",
                    JOptionPane.YES_NO_OPTION
            );

            if (choice == JOptionPane.YES_OPTION) {
                System.out.println("로그아웃 되었습니다.");

                // 1. 현재 메인 화면 닫기 (메모리 해제)
                dispose();

                // 2. 로그인 화면 다시 열기
                new LoginFrame();
            }
        });

        // 메뉴 패널에 로그아웃 버튼 추가 (가장 앞에 추가하거나 뒤에 추가)
        topMenu.add(logoutBtn);
        // 구분선 역할의 빈 라벨 추가 (디자인용)
        topMenu.add(new JLabel(" | "));
        // =========================================================
        // [핵심 로직] 사용자 역할(Role)에 따른 화면 구성 분기
        // =========================================================
        String role = user.getRole(); // "PATIENT", "DOCTOR", "CAREGIVER", "ADMIN"

        if ("DOCTOR".equalsIgnoreCase(role)) {
            configureForDoctor();
        } else if ("CAREGIVER".equalsIgnoreCase(role)) {
            configureForCaregiver();
        } else if ("ADMIN".equalsIgnoreCase(role)) {
            // [추가됨] 관리자일 경우
            configureForAdmin();
        } else {
            // 그 외(기본)는 환자로 처리
            configureForPatient();
        }

        // 공통: 하단에 로그아웃 버튼 등 추가 가능
        getContentPane().add(topMenu, BorderLayout.NORTH);
        getContentPane().add(mainPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    // -----------------------------------------------------------------
    // [환자용] 화면 구성
    // -----------------------------------------------------------------
    private void configureForPatient() {
        JButton homeBtn = new JButton("🏠 나의 건강");
        JButton msgBtn = new JButton("📩 메시지");
        JButton commBtn = new JButton("💬 커뮤니티");

        topMenu.add(homeBtn);
        topMenu.add(msgBtn);
        topMenu.add(commBtn);

        mainPanel.add(new PatientPanel(currentUser), "HOME");
        mainPanel.add(new MessagingPanel(currentUser), "MSG");
        mainPanel.add(new CommunityPanel(currentUser), "COMM");

        homeBtn.addActionListener(e -> cardLayout.show(mainPanel, "HOME"));
        msgBtn.addActionListener(e -> cardLayout.show(mainPanel, "MSG"));
        commBtn.addActionListener(e -> cardLayout.show(mainPanel, "COMM"));

        cardLayout.show(mainPanel, "HOME");
    }

    // -----------------------------------------------------------------
    // [의사용] 화면 구성
    // -----------------------------------------------------------------
    private void configureForDoctor() {
        JButton patientListBtn = new JButton("👨‍⚕️ 담당 환자 관리");
        JButton msgBtn = new JButton("📩 상담 메시지");
        JButton commBtn = new JButton("📢 건강 칼럼(커뮤니티)");

        topMenu.add(patientListBtn);
        topMenu.add(msgBtn);
        topMenu.add(commBtn);

        mainPanel.add(new DoctorPanel(currentUser), "DOC_HOME");
        mainPanel.add(new MessagingPanel(currentUser), "MSG");
        mainPanel.add(new CommunityPanel(currentUser), "COMM");

        patientListBtn.addActionListener(e -> cardLayout.show(mainPanel, "DOC_HOME"));
        msgBtn.addActionListener(e -> cardLayout.show(mainPanel, "MSG"));
        commBtn.addActionListener(e -> cardLayout.show(mainPanel, "COMM"));

        cardLayout.show(mainPanel, "DOC_HOME");
    }

    // -----------------------------------------------------------------
    // [보호자용] 화면 구성
    // -----------------------------------------------------------------
    private void configureForCaregiver() {
        JButton monitorBtn = new JButton("👪 가족 모니터링");
        JButton msgBtn = new JButton("📩 메시지");
        JButton commBtn = new JButton("💬 커뮤니티");

        topMenu.add(monitorBtn);
        topMenu.add(msgBtn);
        topMenu.add(commBtn);

        mainPanel.add(new CaregiverPanel(currentUser), "CARE_HOME");
        mainPanel.add(new MessagingPanel(currentUser), "MSG");
        mainPanel.add(new CommunityPanel(currentUser), "COMM");

        monitorBtn.addActionListener(e -> cardLayout.show(mainPanel, "CARE_HOME"));
        msgBtn.addActionListener(e -> cardLayout.show(mainPanel, "MSG"));
        commBtn.addActionListener(e -> cardLayout.show(mainPanel, "COMM"));

        cardLayout.show(mainPanel, "CARE_HOME");
    }

    // -----------------------------------------------------------------
    // [관리자용] 화면 구성 (NEW)
    // -----------------------------------------------------------------
    private void configureForAdmin() {
        JButton adminBtn = new JButton("⚙️ 시스템 관리");
        // 관리자는 보통 커뮤니티 관리도 하므로 추가 가능
        JButton commBtn = new JButton("💬 커뮤니티 관리");

        topMenu.add(adminBtn);
        topMenu.add(commBtn);

        // AdminPanel 추가
        mainPanel.add(new AdminPanel(currentUser), "ADMIN_HOME");
        mainPanel.add(new CommunityPanel(currentUser), "COMM");

        adminBtn.addActionListener(e -> cardLayout.show(mainPanel, "ADMIN_HOME"));
        commBtn.addActionListener(e -> cardLayout.show(mainPanel, "COMM"));

        cardLayout.show(mainPanel, "ADMIN_HOME");
    }
}