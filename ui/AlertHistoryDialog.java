package ui;

import domain.patient.Alert;
import presentation.controller.MessagingController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AlertHistoryDialog extends JDialog {

    private final MessagingController controller = new MessagingController();
    private Long userId;

    public AlertHistoryDialog(Window owner, Long userId) {
        super(owner, "🔔 알림 수신 내역", ModalityType.APPLICATION_MODAL);
        this.userId = userId;
        setSize(550, 350);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        // 테이블 설정
        String[] cols = {"수신 시간", "알림 내용"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);

        // 컬럼 너비 조정
        table.getColumnModel().getColumn(0).setPreferredWidth(120);
        table.getColumnModel().getColumn(1).setPreferredWidth(380);
        table.setRowHeight(25);

        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton closeBtn = new JButton("닫기");
        closeBtn.addActionListener(e -> dispose());
        add(closeBtn, BorderLayout.SOUTH);

        // 데이터 로드
        loadData(model);
    }

    private void loadData(DefaultTableModel model) {
        List<Alert> alerts = controller.getAlerts(userId);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        // 최신순 정렬 (ID 내림차순)
        alerts.sort((a1, a2) -> Long.compare(a2.getId(), a1.getId()));

        if (alerts.isEmpty()) {
            model.addRow(new Object[]{"-", "수신된 알림이 없습니다."});
        } else {
            for (Alert a : alerts) {
                model.addRow(new Object[]{
                        a.getCreatedAt() != null ? a.getCreatedAt().format(fmt) : "-",
                        a.getMessage()
                });
                a.markRead(); // 확인한 것으로 처리 (파일 저장은 안 되지만 메모리상 반영)
            }
        }
    }
}