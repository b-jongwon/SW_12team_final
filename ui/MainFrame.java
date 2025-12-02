package ui;

import domain.user.User;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel = new JPanel(cardLayout);

    private User currentUser;

    public MainFrame(User user) {
        this.currentUser = user;

        setTitle("Dashboard - " + user.getName());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 상단 메뉴
        JPanel topMenu = new JPanel();
        JButton patientBtn = new JButton("환자 기능");
        JButton msgBtn = new JButton("메시지");
        JButton commBtn = new JButton("커뮤니티");

        topMenu.add(patientBtn);
        topMenu.add(msgBtn);
        topMenu.add(commBtn);

        // 페이지 추가
        mainPanel.add(new PatientPanel(user), "patient");
        mainPanel.add(new MessagingPanel(user), "msg");
        mainPanel.add(new CommunityPanel(user), "comm");

        // 버튼 액션
        patientBtn.addActionListener(e -> cardLayout.show(mainPanel, "patient"));
        msgBtn.addActionListener(e -> cardLayout.show(mainPanel, "msg"));
        commBtn.addActionListener(e -> cardLayout.show(mainPanel, "comm"));

        getContentPane().add(topMenu, BorderLayout.NORTH);
        getContentPane().add(mainPanel, BorderLayout.CENTER);

        setVisible(true);
    }
}
