package ui;

import presentation.controller.CommunityController;
import domain.user.User;
import domain.community.CommunityPost;
import domain.community.CommunityComment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class CommunityPanel extends JPanel {

    private final CommunityController controller = new CommunityController();
    private final User user;

    // 화면 전환을 위한 카드 레이아웃
    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel = new JPanel(cardLayout);

    // [화면 1] 게시글 목록 테이블
    private DefaultTableModel tableModel;
    private JTable postTable;

    // [화면 2] 글쓰기 입력 필드
    private JTextField titleField = new JTextField();
    private JTextArea contentArea = new JTextArea();

    // [화면 3] 상세 보기 및 댓글
    private JLabel detailTitleLabel = new JLabel();
    private JTextArea detailContentArea = new JTextArea();
    private JTextArea commentArea = new JTextArea(); // 댓글 목록
    private JTextField commentInput = new JTextField(); // 댓글 입력창
    private Long currentPostId = null; // 현재 보고 있는 게시글 ID

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
    // [화면 1] 게시글 목록 패널 (List View)
    // =========================================================================
    private JPanel createListPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshBtn = new JButton("새로고침");
        JButton writeBtn = new JButton("✏️ 글쓰기");
        top.add(refreshBtn);
        top.add(writeBtn);

        // 컬럼: 번호, 제목, 작성자, (숨김용) POST_ID
        String[] columnNames = {"번호", "제목", "작성자", "POST_ID"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        postTable = new JTable(tableModel);
        postTable.setRowHeight(25);

        // POST_ID 컬럼 숨기기
        postTable.getColumnModel().getColumn(3).setMinWidth(0);
        postTable.getColumnModel().getColumn(3).setMaxWidth(0);
        postTable.getColumnModel().getColumn(3).setWidth(0);

        // 더블클릭 → 상세 보기
        postTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int viewRow = postTable.getSelectedRow();
                    if (viewRow != -1) {
                        int modelRow = postTable.convertRowIndexToModel(viewRow);
                        Long postId = (Long) tableModel.getValueAt(modelRow, 3);
                        showPostDetail(postId);
                    }
                }
            }
        });

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(postTable), BorderLayout.CENTER);

        refreshBtn.addActionListener(e -> loadPosts());
        writeBtn.addActionListener(e -> {
            titleField.setText("");
            contentArea.setText("");
            cardLayout.show(mainPanel, "WRITE");
        });

        return panel;
    }

    // =========================================================================
    // [화면 2] 글쓰기 패널 (Write View)
    // =========================================================================
    private JPanel createWritePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.add(new JLabel("제목:"), BorderLayout.NORTH);
        inputPanel.add(titleField, BorderLayout.CENTER);

        JPanel contentPanel = new JPanel(new BorderLayout(5, 5));
        contentPanel.add(new JLabel("내용:"), BorderLayout.NORTH);
        contentArea.setLineWrap(true);
        contentPanel.add(new JScrollPane(contentArea), BorderLayout.CENTER);

        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.add(inputPanel, BorderLayout.NORTH);
        center.add(contentPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = new JButton("저장");
        JButton cancelBtn = new JButton("취소");
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);

        panel.add(center, BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);

        cancelBtn.addActionListener(e -> cardLayout.show(mainPanel, "LIST"));
        saveBtn.addActionListener(e -> {
            String title = titleField.getText().trim();
            String content = contentArea.getText().trim();
            if (title.isEmpty() || content.isEmpty()) {
                JOptionPane.showMessageDialog(this, "제목과 내용을 모두 입력해주세요.");
                return;
            }
            controller.post(user.getId(), title, content);
            JOptionPane.showMessageDialog(this, "게시글이 등록되었습니다.");
            loadPosts();
            cardLayout.show(mainPanel, "LIST");
        });

        return panel;
    }

    // =========================================================================
    // [화면 3] 상세 보기 및 댓글 패널 (Detail View)
    // =========================================================================
    private JPanel createDetailPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 상단: 뒤로가기 + 삭제 버튼
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

            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "이 게시글을 삭제하시겠습니까?",
                    "삭제 확인",
                    JOptionPane.YES_NO_OPTION
            );
            if (choice != JOptionPane.YES_OPTION) return;

            boolean ok = controller.deletePost(currentPostId, user);
            if (!ok) {
                JOptionPane.showMessageDialog(this, "삭제 권한이 없습니다.");
                return;
            }

            JOptionPane.showMessageDialog(this, "게시글이 삭제되었습니다.");
            currentPostId = null;
            cardLayout.show(mainPanel, "LIST");
            loadPosts();
        });

        // 1. 게시글 내용
        JPanel postPanel = new JPanel(new BorderLayout(5, 5));
        detailTitleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        detailContentArea.setEditable(false);
        detailContentArea.setBackground(new Color(240, 240, 240));
        detailContentArea.setBorder(BorderFactory.createTitledBorder("본문"));

        postPanel.add(detailTitleLabel, BorderLayout.NORTH);
        postPanel.add(new JScrollPane(detailContentArea), BorderLayout.CENTER);
        postPanel.setPreferredSize(new Dimension(0, 220));

        // 2. 댓글 영역
        JPanel commentPanel = new JPanel(new BorderLayout(5, 5));
        commentPanel.setBorder(BorderFactory.createTitledBorder("댓글 목록"));
        commentArea.setEditable(false);
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

        panel.add(topBar, BorderLayout.NORTH);
        panel.add(postPanel, BorderLayout.CENTER);
        panel.add(commentPanel, BorderLayout.SOUTH);

        return panel;
    }

    // =========================================================================
    // [로직] 데이터 로드 및 화면 갱신
    // =========================================================================

    private void loadPosts() {
        tableModel.setRowCount(0);
        List<CommunityPost> posts = controller.listPosts();
        posts.sort((p1, p2) -> Long.compare(p2.getId(), p1.getId()));

        int number = 1;
        for (CommunityPost p : posts) {
            String authorLabel = controller.getUserLabel(p.getAuthorId());
            tableModel.addRow(new Object[]{
                    number++,
                    p.getTitle(),
                    authorLabel,
                    p.getId()          // 숨김 컬럼
            });
        }
    }

    private void showPostDetail(Long postId) {
        this.currentPostId = postId;

        CommunityPost post = controller.getPost(postId);
        if (post == null) return;

        detailTitleLabel.setText(post.getTitle());
        detailContentArea.setText(post.getContent());
        loadComments(postId);

        cardLayout.show(mainPanel, "DETAIL");
    }

    private void loadComments(Long postId) {
        commentArea.setText("");
        List<CommunityComment> comments = controller.listComments(postId);

        if (comments.isEmpty()) {
            commentArea.append("작성된 댓글이 없습니다.\n");
        } else {
            for (CommunityComment c : comments) {
                String author = controller.getUserLabel(c.getAuthorId());
                commentArea.append(author + ": " + c.getContent() + "\n");
            }
        }
    }
}
