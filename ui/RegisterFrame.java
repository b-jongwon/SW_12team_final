package ui;

import presentation.controller.AuthController;
import javax.swing.*;
import java.awt.*;

public class RegisterFrame extends JFrame {

    private final AuthController authController = new AuthController();

    public RegisterFrame() {

        setTitle("회원가입");
        setSize(400, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(6, 1, 10, 10));

        JTextField loginField = new JTextField();
        JPasswordField pwField = new JPasswordField();
        JTextField nameField = new JTextField();

        String[] roles = {"PATIENT", "DOCTOR", "CAREGIVER"};
        JComboBox<String> roleCombo = new JComboBox<>(roles);

        JButton registerBtn = new JButton("가입하기");

        panel.add(new JLabel("Login ID:"));
        panel.add(loginField);
        panel.add(new JLabel("Password:"));
        panel.add(pwField);
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Role 선택:"));
        panel.add(roleCombo);

        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(registerBtn, BorderLayout.SOUTH);

        registerBtn.addActionListener(e -> {

            String loginId = loginField.getText().trim();
            String password = new String(pwField.getPassword());
            String name = nameField.getText().trim();
            String role = (String) roleCombo.getSelectedItem();

            if (loginId.isEmpty() || password.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(RegisterFrame.this, "모든 필드를 입력해야 합니다!");
                return;
            }

            try {
                authController.register(loginId, password, name, role);

                JOptionPane.showMessageDialog(RegisterFrame.this, "회원가입 성공!");
                dispose(); // 창 닫기

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(RegisterFrame.this, "회원가입 실패: " + ex.getMessage());
            }

        });

        setVisible(true);
    }
}
