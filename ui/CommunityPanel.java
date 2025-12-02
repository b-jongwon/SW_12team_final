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
    private JTextArea commentArea = new JTextArea(); // 댓글 목록 보여줄 곳
    private JTextField commentInput = new JTextField(); // 댓글 입력창
    private Long currentPostId = null; // 현재 보고 있는 게시글 ID

    public CommunityPanel(User user) {
        this.user = user;
        setLayout(new BorderLayout());

        // 3개의 화면(카드) 생성
        mainPanel.add(createListPanel(), "LIST");     // 목록 화면
        mainPanel.add(createWritePanel(), "WRITE");   // 글쓰기 화면
        mainPanel.add(createDetailPanel(), "DETAIL"); // 상세 화면

        add(mainPanel, BorderLayout.CENTER);

        // 초기 화면은 목록
        cardLayout.show(mainPanel, "LIST");
        loadPosts(); // 데이터 로드
    }

    // =========================================================================
    // [화면 1] 게시글 목록 패널 (List View)
    // =========================================================================
    private JPanel createListPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 상단 버튼
        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshBtn = new JButton("새로고침");
        JButton writeBtn = new JButton("✏️ 글쓰기");
        top.add(refreshBtn);
        top.add(writeBtn);

        // 테이블 (ID, 제목, 작성자ID)
        String[] columnNames = {"ID", "제목", "작성자ID"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override // 셀 수정 불가
            public boolean isCellEditable(int row, int column) { return false; }
        };
        postTable = new JTable(tableModel);
        postTable.setRowHeight(25);

        // 이벤트: 목록에서 행 더블클릭 -> 상세화면 이동
        postTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = postTable.getSelectedRow();
                    if (row != -1) {
                        Long postId = (Long) tableModel.getValueAt(row, 0);
                        showPostDetail(postId); // 상세 화면으로 이동
                    }
                }
            }
        });

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(postTable), BorderLayout.CENTER);

        // 버튼 이벤트
        refreshBtn.addActionListener(e -> loadPosts());
        writeBtn.addActionListener(e -> {
            // 입력창 초기화 후 화면 전환
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

        // 이벤트
        cancelBtn.addActionListener(e -> cardLayout.show(mainPanel, "LIST"));
        saveBtn.addActionListener(e -> {
            String title = titleField.getText().trim();
            String content = contentArea.getText().trim();
            if (title.isEmpty() || content.isEmpty()) {
                JOptionPane.showMessageDialog(this, "제목과 내용을 모두 입력해주세요.");
                return;
            }
            // 컨트롤러 호출 (저장)
            controller.post(user.getId(), title, content);
            JOptionPane.showMessageDialog(this, "게시글이 등록되었습니다.");
            loadPosts(); // 목록 갱신
            cardLayout.show(mainPanel, "LIST"); // 목록으로 복귀
        });

        return panel;
    }

    // =========================================================================
    // [화면 3] 상세 보기 및 댓글 패널 (Detail View)
    // =========================================================================
    private JPanel createDetailPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1. 게시글 내용 영역
        JPanel postPanel = new JPanel(new BorderLayout(5, 5));
        detailTitleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        detailContentArea.setEditable(false);
        detailContentArea.setBackground(new Color(240, 240, 240));
        detailContentArea.setBorder(BorderFactory.createTitledBorder("본문"));

        postPanel.add(detailTitleLabel, BorderLayout.NORTH);
        postPanel.add(new JScrollPane(detailContentArea), BorderLayout.CENTER);
        postPanel.setPreferredSize(new Dimension(0, 200));

        // 2. 댓글 영역
        JPanel commentPanel = new JPanel(new BorderLayout(5, 5));
        commentPanel.setBorder(BorderFactory.createTitledBorder("댓글 목록"));
        commentArea.setEditable(false);
        commentPanel.add(new JScrollPane(commentArea), BorderLayout.CENTER);

        // 댓글 입력
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        JButton addCommentBtn = new JButton("댓글 등록");
        inputPanel.add(commentInput, BorderLayout.CENTER);
        inputPanel.add(addCommentBtn, BorderLayout.EAST);
        commentPanel.add(inputPanel, BorderLayout.SOUTH);

        // 상단: 뒤로가기 버튼
        JButton backBtn = new JButton("⬅ 목록으로");
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "LIST"));

        panel.add(backBtn, BorderLayout.NORTH);
        panel.add(postPanel, BorderLayout.CENTER);
        panel.add(commentPanel, BorderLayout.SOUTH);

        // 댓글 등록 이벤트
        addCommentBtn.addActionListener(e -> {
            String text = commentInput.getText().trim();
            if (text.isEmpty() || currentPostId == null) return;

            // 컨트롤러 호출
            controller.comment(currentPostId, user.getId(), text);
            commentInput.setText("");
            loadComments(currentPostId); // 댓글창 새로고침
        });

        return panel;
    }

    // =========================================================================
    // [로직] 데이터 로드 및 화면 갱신 메서드들
    // =========================================================================

    // 게시글 목록 불러오기
    private void loadPosts() {
        tableModel.setRowCount(0); // 테이블 초기화
        List<CommunityPost> posts = controller.listPosts();
        // 최신글이 위로 오게 역순 정렬 (ID 기준)
        posts.sort((p1, p2) -> Long.compare(p2.getId(), p1.getId()));

        for (CommunityPost p : posts) {
            tableModel.addRow(new Object[]{p.getId(), p.getTitle(), p.getAuthorId()});
        }
    }

    // 상세 화면 데이터 세팅 및 화면 전환
    private void showPostDetail(Long postId) {
        this.currentPostId = postId;

        // 1. 게시글 정보 찾기 (편의상 전체 리스트에서 검색, 실제론 findById가 효율적)
        List<CommunityPost> posts = controller.listPosts();
        CommunityPost post = posts.stream()
                .filter(p -> p.getId().equals(postId))
                .findFirst()
                .orElse(null);

        if (post != null) {
            detailTitleLabel.setText(post.getTitle());
            detailContentArea.setText(post.getContent());
            loadComments(postId); // 댓글 로드
            cardLayout.show(mainPanel, "DETAIL"); // 화면 전환
        }
    }

    // 댓글 목록 불러오기
    private void loadComments(Long postId) {
        commentArea.setText("");
        List<CommunityComment> comments = controller.listComments(postId);

        if (comments.isEmpty()) {
            commentArea.append("작성된 댓글이 없습니다.\n");
        } else {
            for (CommunityComment c : comments) {
                String author = (c.getAuthorId().equals(user.getId())) ? "[나]" : "[User " + c.getAuthorId() + "]";
                commentArea.append(author + ": " + c.getContent() + "\n");
            }
        }
    }
}