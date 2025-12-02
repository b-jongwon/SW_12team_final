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

    // 1. 컨트롤러 (DI 적용 전 직접 생성)
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
        threadCombo.setPreferredSize(new Dimension(300, 30));
        topPanel.add(threadCombo);

        JButton refreshBtn = new JButton("새로고침");
        topPanel.add(refreshBtn);

        // (테스트용) 대화방이 없을 때 강제로 하나 만드는 버튼
        JButton createTestThreadBtn = new JButton("의사와 대화 시작(테스트)");
        topPanel.add(createTestThreadBtn);

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

        // 1. 새로고침 버튼: 내 대화방 목록 불러오기
        refreshBtn.addActionListener(e -> loadThreads());

        // 2. 대화방 콤보박스 변경 시: 해당 방의 메시지 로드
        threadCombo.addActionListener(e -> loadMessages());

        // 3. 전송 버튼: 메시지 보내기
        sendBtn.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage()); // 엔터키로도 전송 가능하게

        // 4. (테스트용) 의사와의 대화방 생성
        createTestThreadBtn.addActionListener(e -> {
            // 임의의 의사 ID(예: 999)와 대화방 생성 시도
            // 실제로는 '환자 배정' 로직을 통해 의사 ID를 알아와야 함
            controller.createThread(user.getId(), null, 999L);
            JOptionPane.showMessageDialog(this, "테스트용 대화방이 생성되었습니다.\n'새로고침'을 눌러주세요.");
        });

        // 패널 열릴 때 초기 로드
        loadThreads();
    }

    // ---------------------------------------------------
    // [로직 1] 내가 참여중인 대화방 목록 불러오기
    // ---------------------------------------------------
    private void loadThreads() {
        threadCombo.removeAllItems();
        List<MessageThread> threads = controller.getThreads(user.getId());

        if (threads.isEmpty()) {
            chatArea.setText("참여 중인 대화방이 없습니다.\n상단의 '의사와 대화 시작' 버튼을 눌러보세요.");
        } else {
            for (MessageThread t : threads) {
                // 콤보박스에 아이템 추가 (보여지는 텍스트와 실제 ID를 묶기 위해 Wrapper 클래스 사용)
                String label = "대화방 #" + t.getId() + " (with Doctor " + t.getDoctorId() + ")";
                threadCombo.addItem(new ThreadItem(t.getId(), label));
            }
        }
    }

    // ---------------------------------------------------
    // [로직 2] 선택된 대화방의 메시지 내역 불러오기
    // ---------------------------------------------------
    private void loadMessages() {
        ThreadItem selected = (ThreadItem) threadCombo.getSelectedItem();
        if (selected == null) return;

        chatArea.setText(""); // 초기화
        List<Message> messages = controller.getMessages(selected.threadId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (Message m : messages) {
            String sender = (m.getSenderId().equals(user.getId())) ? "[나]" : "[상대방]";
            String time = m.getSentAt().format(formatter);

            chatArea.append(sender + " " + time + "\n");
            chatArea.append(" " + m.getContent() + "\n\n");
        }

        // 스크롤 맨 아래로
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    // ---------------------------------------------------
    // [로직 3] 메시지 전송하기
    // ---------------------------------------------------
    private void sendMessage() {
        ThreadItem selected = (ThreadItem) threadCombo.getSelectedItem();
        String content = inputField.getText().trim();

        if (selected == null) {
            JOptionPane.showMessageDialog(this, "대화방을 먼저 선택해주세요.");
            return;
        }
        if (content.isEmpty()) return;

        // 컨트롤러 호출
        controller.send(selected.threadId, user.getId(), content);

        // UI 업데이트
        inputField.setText("");
        loadMessages(); // 보낸 후 목록 다시 로드해서 내 메시지 표시
    }

    // 콤보박스용 아이템 클래스 (내부 클래스)
    private static class ThreadItem {
        Long threadId;
        String label;

        public ThreadItem(Long threadId, String label) {
            this.threadId = threadId;
            this.label = label;
        }

        @Override
        public String toString() {
            return label; // 콤보박스에 표시될 문자열
        }
    }
}