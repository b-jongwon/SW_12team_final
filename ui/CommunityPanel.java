package ui;

import presentation.controller.CommunityController;
import domain.user.User;
import domain.community.CommunityPost;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CommunityPanel extends JPanel {

    private final CommunityController controller = new CommunityController();
    private User user;

    public CommunityPanel(User user) {
        this.user = user;

        setLayout(new BorderLayout());

        JTextArea output = new JTextArea();
        output.setEditable(false);

        JButton createBtn = new JButton("게시글 작성");
        JButton listBtn = new JButton("게시글 조회");

        JPanel top = new JPanel();
        top.add(createBtn);
        top.add(listBtn);

        add("North", top);
        add("Center", new JScrollPane(output));

        createBtn.addActionListener(e -> {
            CommunityPost p = controller.post(user.getId(), "테스트 제목", "테스트 내용");
            output.append("게시글 작성됨: id=" + p.getId() + "\n");
        });

        listBtn.addActionListener(e -> {
            List<CommunityPost> posts = controller.listPosts();
            output.append("=== 게시글 목록 ===\n");
            for (CommunityPost p : posts) {
                output.append("[" + p.getId() + "] " + p.getTitle() + "\n");
            }
        });
    }
}
