package ui;

import presentation.controller.CommunityController;
import domain.user.User;
import domain.community.CommunityPost;
import domain.community.CommunityComment;
import domain.content.Announcement;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class CommunityPanel extends JPanel {

    private final CommunityController controller = new CommunityController();
    private final User user;

    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel = new JPanel(cardLayout);

    private DefaultTableModel tableModel;
    private JTable postTable;

    private JTextField titleField = new JTextField();
    private JTextArea contentArea = new JTextArea();

    private JLabel detailTitleLabel = new JLabel();
    private JTextArea detailContentArea = new JTextArea();
    private JTextArea commentArea = new JTextArea();
    private JTextField commentInput = new JTextField();
    private Long currentPostId = null;

    public CommunityPanel(User user) {
        this.user = user;
        setLayout(new BorderLayout());

        mainPanel.add(createListPanel(), "LIST");
        mainPanel.add(createWritePanel(), "WRITE");
        mainPanel.add(createDetailPanel(), "DETAIL");

        add(mainPanel, BorderLayout.CENTER);

        cardLayout.show(mainPanel, "LIST");
        loadPosts();
    }

    // =========================================================================
    // [화면 1] 게시글 목록
    // =========================================================================
    private JPanel createListPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshBtn = new JButton("새로고침");
        JButton writeBtn = new JButton("✏️ 글쓰기");
        top.add(refreshBtn);
        top.add(writeBtn);

        String[] cols = {"번호", "제목", "작성자", "POST_ID", "TYPE"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        postTable = new JTable(tableModel);
        postTable.setRowHeight(25);

        // 숨김 컬럼
        postTable.getColumnModel().getColumn(3).setMinWidth(0);
        postTable.getColumnModel().getColumn(3).setMaxWidth(0);
        postTable.getColumnModel().getColumn(4).setMinWidth(0);
        postTable.getColumnModel().getColumn(4).setMaxWidth(0);

        postTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = postTable.getSelectedRow();
                    if (row == -1) return;

                    Long postId = (Long) tableModel.getValueAt(row, 3);
                    String type = (String) tableModel.getValueAt(row, 4);

                    if ("NOTICE".equals(type)) {
                        showAnnouncementDetail(postId);
                    } else {
                        showPostDetail(postId);
                    }
                }
            }
        });

        refreshBtn.addActionListener(e -> loadPosts());
        writeBtn.addActionListener(e -> {
            titleField.setText("");
            contentArea.setText("");
            cardLayout.show(mainPanel, "WRITE");
        });

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(postTable), BorderLayout.CENTER);
        return panel;
    }

    // =========================================================================
    // [화면 2] 글쓰기 패널 (제목 + 본문 정상)
    // =========================================================================
    private JPanel createWritePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 제목 영역
        JPanel titlePanel = new JPanel(new BorderLayout(5, 5));
        titlePanel.add(new JLabel("제목:"), BorderLayout.NORTH);
        titlePanel.add(titleField, BorderLayout.CENTER);

        // 본문 영역
        JPanel contentPanel = new JPanel(new BorderLayout(5, 5));
        contentPanel.add(new JLabel("내용:"), BorderLayout.NORTH);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentPanel.add(new JScrollPane(contentArea), BorderLayout.CENTER);

        // 제목 / 본문 분할
        JSplitPane splitPane = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                titlePanel,
                contentPanel
        );
        splitPane.setDividerLocation(60);
        splitPane.setResizeWeight(0.1);

        panel.add(splitPane, BorderLayout.CENTER);

        // 버튼
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = new JButton("저장");
        JButton cancelBtn = new JButton("취소");
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);

        cancelBtn.addActionListener(e -> cardLayout.show(mainPanel, "LIST"));

        saveBtn.addActionListener(e -> {
            String title = titleField.getText().trim();
            String content = contentArea.getText().trim();

            if (title.isEmpty() || content.isEmpty()) {
                JOptionPane.showMessageDialog(this, "제목과 내용을 모두 입력하세요.");
                return;
            }

            controller.post(user.getId(), user.getName(), title, content);
            loadPosts();
            cardLayout.show(mainPanel, "LIST");
        });

        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    // =========================================================================
    // [화면 3] 상세 보기 + 댓글 (위/아래 크기 조절)
    // =========================================================================
    private JPanel createDetailPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton backBtn = new JButton("⬅ 목록으로");
        JButton deleteBtn = new JButton("🗑 삭제");
        topBar.add(backBtn);
        topBar.add(deleteBtn);

        backBtn.addActionListener(e -> {
            cardLayout.show(mainPanel, "LIST");
            loadPosts();
        });

        deleteBtn.addActionListener(e -> {
            if (currentPostId == null) return;
            if (!controller.deletePost(currentPostId, user)) {
                JOptionPane.showMessageDialog(this, "삭제 권한이 없습니다.");
                return;
            }
            currentPostId = null;
            loadPosts();
            cardLayout.show(mainPanel, "LIST");
        });

        panel.add(topBar, BorderLayout.NORTH);

        // 본문
        JPanel postPanel = new JPanel(new BorderLayout(5, 5));
        detailTitleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        detailContentArea.setEditable(false);
        detailContentArea.setLineWrap(true);
        detailContentArea.setWrapStyleWord(true);
        postPanel.add(detailTitleLabel, BorderLayout.NORTH);
        postPanel.add(new JScrollPane(detailContentArea), BorderLayout.CENTER);

        // 댓글
        JPanel commentPanel = new JPanel(new BorderLayout(5, 5));
        commentArea.setEditable(false);
        commentArea.setLineWrap(true);
        commentArea.setWrapStyleWord(true);
        commentPanel.add(new JScrollPane(commentArea), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        JButton addCommentBtn = new JButton("댓글 등록");
        inputPanel.add(commentInput, BorderLayout.CENTER);
        inputPanel.add(addCommentBtn, BorderLayout.EAST);
        commentPanel.add(inputPanel, BorderLayout.SOUTH);

        addCommentBtn.addActionListener(e -> {
            String text = commentInput.getText().trim();
            if (text.isEmpty() || currentPostId == null) return;

            controller.comment(currentPostId, user.getId(), text);
            commentInput.setText("");
            loadComments(currentPostId);
        });

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                postPanel,
                commentPanel
        );
        splitPane.setDividerLocation(260);
        splitPane.setResizeWeight(0.6);

        panel.add(splitPane, BorderLayout.CENTER);
        return panel;
    }

    // =========================================================================
    // 데이터 로딩
    // =========================================================================
    private void loadPosts() {
        tableModel.setRowCount(0);

        for (Announcement a : controller.listAnnouncements()) {
            tableModel.addRow(new Object[]{
                    "📢",
                    "[공지] " + a.getTitle(),
                    "관리자",
                    a.getId(),
                    "NOTICE"
            });
        }

        int num = 1;
        for (CommunityPost p : controller.listPosts()) {
            tableModel.addRow(new Object[]{
                    num++,
                    p.getTitle(),
                    controller.getUserLabel(p.getAuthorId()),
                    p.getId(),
                    "POST"
            });
        }
    }

    private void showPostDetail(Long postId) {
        currentPostId = postId;
        CommunityPost post = controller.getPost(postId);
        if (post == null) return;

        detailTitleLabel.setText(post.getTitle());
        detailContentArea.setText(post.getContent());
        loadComments(postId);
        cardLayout.show(mainPanel, "DETAIL");
    }

    private void showAnnouncementDetail(Long id) {
        controller.listAnnouncements().stream()
                .filter(a -> a.getId().equals(id))
                .findFirst()
                .ifPresent(a ->
                        JOptionPane.showMessageDialog(
                                this,
                                a.getContent(),
                                "공지사항",
                                JOptionPane.INFORMATION_MESSAGE
                        )
                );
    }

    private void loadComments(Long postId) {
        commentArea.setText("");
        List<CommunityComment> list = controller.listComments(postId);

        if (list.isEmpty()) {
            commentArea.append("작성된 댓글이 없습니다.\n");
        } else {
            for (CommunityComment c : list) {
                commentArea.append(
                        controller.getUserLabel(c.getAuthorId())
                                + ": " + c.getContent() + "\n"
                );
            }
        }
    }
}
