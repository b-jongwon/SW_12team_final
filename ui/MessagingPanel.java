package ui;

import presentation.controller.MessagingController;
import domain.user.User;
import domain.messaging.Message;
import domain.messaging.MessageThread;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MessagingPanel extends JPanel {

    private final MessagingController controller = new MessagingController();
    private final User user;

    // UI 컴포넌트
    private JComboBox<ThreadItem> threadCombo; // 대화방 선택 박스
    private JTextArea chatArea;                // 채팅 내용 보여주는 곳
    private JTextField inputField;             // 메시지 입력창
    private JButton sendBtn;                   // 전송 버튼

    public MessagingPanel(User user) {
        this.user = user;
        setLayout(new BorderLayout());

        // ==========================================
        // [North] 대화방 선택 영역
        // ==========================================
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("대화방 선택:"));

        threadCombo = new JComboBox<>();
        threadCombo.setPreferredSize(new Dimension(350, 30)); // 폭을 조금 넓힘
        topPanel.add(threadCombo);

        JButton refreshBtn = new JButton("새로고침");
        topPanel.add(refreshBtn);

        add(topPanel, BorderLayout.NORTH);

        // ==========================================
        // [Center] 채팅 내용 영역
        // ==========================================
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        chatArea.setMargin(new Insets(10, 10, 10, 10)); // 여백

        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);

        // ==========================================
        // [South] 메시지 입력 영역
        // ==========================================
        JPanel bottomPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        inputField.setFont(new Font("맑은 고딕", Font.PLAIN, 14));

        sendBtn = new JButton("전송");

        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(sendBtn, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);

        // ==========================================
        // [이벤트 리스너 등록]
        // ==========================================

        // 1. 새로고침 버튼
        refreshBtn.addActionListener(e -> loadThreads());

        // 2. 대화방 선택 변경 시
        threadCombo.addActionListener(e -> loadMessages());

        // 3. 전송 버튼
        sendBtn.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());

        // 패널 열릴 때 초기 로드
        loadThreads();
    }

    // ---------------------------------------------------
    // [로직 1] 대화방 목록 불러오기 (사용자님이 작성하신 부분 반영)
    // ---------------------------------------------------
    private void loadThreads() {
        threadCombo.removeAllItems();
        // 백엔드에서 내가 포함된 모든 방을 가져옴 (환자 본인, 의사, 보호자 포함)
        List<MessageThread> threads = controller.getThreads(user.getId());

        if (threads.isEmpty()) {
            chatArea.setText("참여 중인 대화방이 없습니다.\n(연결이 수락되면 자동으로 방이 생성됩니다)");
        } else {
            for (MessageThread t : threads) {
                // 방 제목 생성
                String roomName = "환자(ID:" + t.getPatientId() + ")의 건강관리방";

                // 구성원 수 계산
                int memberCount = 1; // 환자는 무조건 있음
                if (t.getDoctorId() != null) memberCount++; // 의사 있으면 +1
                if (t.getCaregiverIds() != null) {
                    memberCount += t.getCaregiverIds().size(); // 보호자 수만큼 +
                }

                // 라벨 만들기: "환자(...)의 건강관리방 (참여자: 3명)"
                String label = String.format("%s (참여자: %d명)", roomName, memberCount);

                // 콤보박스에 추가
                threadCombo.addItem(new ThreadItem(t.getId(), label));
            }
        }
    }

    // ---------------------------------------------------
    // [로직 2] 메시지 내용 불러오기
    // ---------------------------------------------------
    private void loadMessages() {
        ThreadItem selected = (ThreadItem) threadCombo.getSelectedItem();
        if (selected == null) {
            chatArea.setText("");
            return;
        }

        chatArea.setText(""); // 초기화
        List<Message> messages = controller.getMessages(selected.threadId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (Message m : messages) {
            String sender = (m.getSenderId().equals(user.getId())) ? "[나]" : "[ID:" + m.getSenderId() + "]";
            String time = m.getSentAt().format(formatter);

            chatArea.append(sender + " " + time + "\n");
            chatArea.append(" " + m.getContent() + "\n\n");
        }

        // 스크롤 맨 아래로
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    // ---------------------------------------------------
    // [로직 3] 메시지 전송
    // ---------------------------------------------------
    private void sendMessage() {
        ThreadItem selected = (ThreadItem) threadCombo.getSelectedItem();
        String content = inputField.getText().trim();

        if (selected == null) {
            JOptionPane.showMessageDialog(this, "대화방을 먼저 선택해주세요.");
            return;
        }
        if (content.isEmpty()) return;

        controller.send(selected.threadId, user.getId(), content);

        inputField.setText("");
        loadMessages(); // 전송 후 즉시 갱신
    }

    // 콤보박스용 아이템 클래스
    private static class ThreadItem {
        Long threadId;
        String label;

        public ThreadItem(Long threadId, String label) {
            this.threadId = threadId;
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }
}