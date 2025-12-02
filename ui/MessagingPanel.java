package ui;

import presentation.controller.MessagingController;
import domain.user.User;
import domain.messaging.MessageThread;
import domain.messaging.Message;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MessagingPanel extends JPanel {

    private final MessagingController controller = new MessagingController();
    private User user;

    public MessagingPanel(User user) {
        this.user = user;

        setLayout(new BorderLayout());

        JTextArea output = new JTextArea();
        output.setEditable(false);

        JButton loadBtn = new JButton("내 메시지 스레드 확인");
        JButton sendBtn = new JButton("메시지 보내기");

        JPanel top = new JPanel();
        top.add(loadBtn);
        top.add(sendBtn);

        add("North", top);
        add("Center", new JScrollPane(output));

        loadBtn.addActionListener(e -> {
            List<MessageThread> list = controller.getThreads(user.getId());
            output.append("=== 메시지 스레드 ===\n");
            for (MessageThread t : list) {
                output.append("thread id = " + t.getId() + "\n");
            }
        });

        sendBtn.addActionListener(e -> {
            controller.send(1L, user.getId(), "테스트 메시지");
            output.append("메시지 전송됨\n");
        });
    }
}
