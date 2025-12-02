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

        // =========================================================
        // [핵심 로직] 사용자 역할(Role)에 따른 화면 구성 분기
        // =========================================================
        String role = user.getRole(); // "PATIENT", "DOCTOR", "CAREGIVER"

        if ("DOCTOR".equalsIgnoreCase(role)) {
            // [의사] 1. 환자 관리 패널, 2. 메시지, 3. 커뮤니티
            configureForDoctor();
        } else if ("CAREGIVER".equalsIgnoreCase(role)) {
            // [보호자] 1. 가족 모니터링 패널(미구현시 대체), 2. 메시지, 3. 커뮤니티
            configureForCaregiver();
        } else {
            // [환자] 1. 나의 건강 패널, 2. 메시지, 3. 커뮤니티
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
        // 메뉴 버튼 생성
        JButton homeBtn = new JButton("🏠 나의 건강");
        JButton msgBtn = new JButton("📩 메시지");
        JButton commBtn = new JButton("💬 커뮤니티");

        topMenu.add(homeBtn);
        topMenu.add(msgBtn);
        topMenu.add(commBtn);

        // 패널 추가
        mainPanel.add(new PatientPanel(currentUser), "HOME");
        mainPanel.add(new MessagingPanel(currentUser), "MSG");
        mainPanel.add(new CommunityPanel(currentUser), "COMM");

        // 이벤트 연결
        homeBtn.addActionListener(e -> cardLayout.show(mainPanel, "HOME"));
        msgBtn.addActionListener(e -> cardLayout.show(mainPanel, "MSG"));
        commBtn.addActionListener(e -> cardLayout.show(mainPanel, "COMM"));

        // 초기 화면
        cardLayout.show(mainPanel, "HOME");
    }

    // -----------------------------------------------------------------
    // [의사용] 화면 구성
    // -----------------------------------------------------------------
    private void configureForDoctor() {
        // 의사 전용 메뉴
        JButton patientListBtn = new JButton("👨‍⚕️ 담당 환자 관리");
        JButton msgBtn = new JButton("📩 상담 메시지");
        JButton commBtn = new JButton("📢 건강 칼럼(커뮤니티)");

        topMenu.add(patientListBtn);
        topMenu.add(msgBtn);
        topMenu.add(commBtn);

        // 패널 추가 (DoctorPanel 사용!)
        mainPanel.add(new DoctorPanel(currentUser), "DOC_HOME");
        mainPanel.add(new MessagingPanel(currentUser), "MSG");
        mainPanel.add(new CommunityPanel(currentUser), "COMM");

        // 이벤트 연결
        patientListBtn.addActionListener(e -> cardLayout.show(mainPanel, "DOC_HOME"));
        msgBtn.addActionListener(e -> cardLayout.show(mainPanel, "MSG"));
        commBtn.addActionListener(e -> cardLayout.show(mainPanel, "COMM"));

        // 초기 화면
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

        // [수정 완료] 이제 임시 패널이 아니라 진짜 CaregiverPanel을 사용합니다!
        mainPanel.add(new CaregiverPanel(currentUser), "CARE_HOME");
        mainPanel.add(new MessagingPanel(currentUser), "MSG");
        mainPanel.add(new CommunityPanel(currentUser), "COMM");

        monitorBtn.addActionListener(e -> cardLayout.show(mainPanel, "CARE_HOME"));
        msgBtn.addActionListener(e -> cardLayout.show(mainPanel, "MSG"));
        commBtn.addActionListener(e -> cardLayout.show(mainPanel, "COMM"));

        cardLayout.show(mainPanel, "CARE_HOME");
    }
}