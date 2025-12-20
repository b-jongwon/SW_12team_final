package ui;

import presentation.controller.MessagingController;
import data.repository.UserRepository;
import domain.user.User;
import domain.messaging.Message;
import domain.messaging.MessageThread;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class MessagingPanel extends JPanel {

    private final MessagingController controller = new MessagingController();
    private final UserRepository userRepo = new UserRepository();
    private final User user;

    private JComboBox<ThreadItem> threadCombo;
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendBtn;

    public MessagingPanel(User user) {
        this.user = user;
        setLayout(new BorderLayout());

        // =========================
        // 상단: 대화방 선택
        // =========================
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("대화방 선택:"));

        threadCombo = new JComboBox<>();
        threadCombo.setPreferredSize(new Dimension(420, 30));
        topPanel.add(threadCombo);

        JButton refreshBtn = new JButton("새로고침");
        topPanel.add(refreshBtn);

        add(topPanel, BorderLayout.NORTH);

        // =========================
        // 중앙: 채팅 영역
        // =========================
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        chatArea.setMargin(new Insets(10, 10, 10, 10));
        add(new JScrollPane(chatArea), BorderLayout.CENTER);

        // =========================
        // 하단: 입력 영역
        // =========================
        JPanel bottomPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        sendBtn = new JButton("전송");

        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(sendBtn, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        // 이벤트
        refreshBtn.addActionListener(e -> loadThreads());
        threadCombo.addActionListener(e -> loadMessages());
        sendBtn.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());

        loadThreads();
    }

    // =====================================================
    // 대화방 목록 로드 (방 이름 수정)
    // =====================================================
    private void loadThreads() {
        threadCombo.removeAllItems();
        List<MessageThread> threads = controller.getThreads(user.getId());

        if (threads.isEmpty()) {
            chatArea.setText("참여 중인 대화방이 없습니다.");
            return;
        }

        for (MessageThread t : threads) {
            String patientLabel = getUserLabel(t.getPatientId());
            String roomName = patientLabel + "의 건강 관리방";

            int memberCount = 1;
            if (t.getDoctorId() != null) memberCount++;
            if (t.getCaregiverIds() != null) memberCount += t.getCaregiverIds().size();

            String label = String.format("%s (참여자 %d명)", roomName, memberCount);
            threadCombo.addItem(new ThreadItem(t.getId(), label));
        }
    }

    // =====================================================
    // 메시지 로드 (이름 표시)
    // =====================================================
    private void loadMessages() {
        ThreadItem selected = (ThreadItem) threadCombo.getSelectedItem();
        if (selected == null) {
            chatArea.setText("");
            return;
        }

        chatArea.setText("");
        List<Message> messages = controller.getMessages(selected.threadId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (Message m : messages) {
            String sender;
            if (m.getSenderId().equals(user.getId())) {
                sender = "[나]";
            } else {
                sender = getUserLabel(m.getSenderId());
            }

            String time = m.getSentAt().format(formatter);

            chatArea.append(sender + "  " + time + "\n");
            chatArea.append("  " + m.getContent() + "\n\n");
        }

        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    // =====================================================
    // 메시지 전송
    // =====================================================
    private void sendMessage() {
        ThreadItem selected = (ThreadItem) threadCombo.getSelectedItem();
        String content = inputField.getText().trim();

        if (selected == null || content.isEmpty()) return;

        controller.send(selected.threadId, user.getId(), content);
        inputField.setText("");
        loadMessages();
    }

    // =====================================================
    // 사용자 이름 + 역할 표시
    // =====================================================
    private String getUserLabel(Long userId) {
        Optional<User> opt = userRepo.findById(userId);
        if (opt.isEmpty()) return "알 수 없음";

        User u = opt.get();
        String role;
        switch (u.getRole()) {
            case "PATIENT": role = "환자"; break;
            case "DOCTOR": role = "의사"; break;
            case "CAREGIVER": role = "보호자"; break;
            case "ADMIN": role = "관리자"; break;
            default: role = u.getRole();
        }
        return u.getName() + "(" + role + ")";
    }

    // =====================================================
    // 콤보박스 아이템
    // =====================================================
    private static class ThreadItem {
        Long threadId;
        String label;

        ThreadItem(Long threadId, String label) {
            this.threadId = threadId;
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }
}
