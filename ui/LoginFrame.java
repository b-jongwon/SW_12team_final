package ui;

import presentation.controller.AuthController;
import domain.user.User;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

public class LoginFrame extends JFrame {

    private final AuthController authController = new AuthController();

    public LoginFrame() {

        setTitle("Stroke Prevention System - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));

        JTextField loginIdField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        JButton loginBtn = new JButton("로그인");
        JButton registerBtn = new JButton("회원가입");

        panel.add(new JLabel("Login ID:"));
        panel.add(loginIdField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);

        JPanel btnPanel = new JPanel();
        btnPanel.add(loginBtn);
        btnPanel.add(registerBtn);

        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(btnPanel, BorderLayout.SOUTH);

        loginBtn.addActionListener(e -> {
            String id = loginIdField.getText();
            String pw = new String(passwordField.getPassword());

            Optional<User> result = authController.login(id, pw);

            if (result.isPresent()) {
                JOptionPane.showMessageDialog(LoginFrame.this, "로그인 성공!");

                // MainFrame에 로그인된 사용자 전달
                new MainFrame(result.get());
                dispose();

            } else {
                JOptionPane.showMessageDialog(LoginFrame.this, "로그인 실패");
            }
        });

        registerBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "회원가입 화면은 아직 미구현");
        });

        setVisible(true);
    }
}
