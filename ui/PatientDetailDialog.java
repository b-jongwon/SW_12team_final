package ui;

import domain.medical.DoctorNote;
import domain.patient.HealthRecord;
import presentation.controller.CaregiverController;
import presentation.controller.DoctorController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

public class PatientDetailDialog extends JDialog {

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public PatientDetailDialog(JFrame parent, String patientName, Long patientId, Object controller) {
        super(parent, patientName + "님의 상세 건강 정보", true);
        setSize(1000, 680);
        setLocationRelativeTo(parent);

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(12, 12, 12, 12));
        setContentPane(root);

        // ----------------------------
        // 데이터 로드
        // ----------------------------
        List<HealthRecord> records = loadRecords(controller, patientId);
        List<DoctorNote> notes = loadNotes(controller, patientId);

        HealthRecord latest = records.stream()
                .filter(r -> r.getMeasuredAt() != null)
                .max(Comparator.comparing(HealthRecord::getMeasuredAt))
                .orElse(null);

        // ----------------------------
        // 상단: 프로필 카드
        // ----------------------------
        root.add(createProfileCard(patientName, patientId, latest), BorderLayout.NORTH);

        // ----------------------------
        // 건강 기록 테이블
        // ----------------------------
        String[] recordCols = {"측정일시", "혈압(수/이)", "혈당", "흡연", "음주", "활동", "위험요인", "BMI"};
        DefaultTableModel recordModel = new DefaultTableModel(recordCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable recordTable = new JTable(recordModel);
        styleTable(recordTable);

        JScrollPane recordScroll = new JScrollPane(recordTable);
        recordScroll.setBorder(BorderFactory.createTitledBorder("건강 기록 내역"));

        // ----------------------------
        // 의사 소견 테이블
        // ----------------------------
        String[] noteCols = {"작성일", "소견 내용"};
        DefaultTableModel noteModel = new DefaultTableModel(noteCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable noteTable = new JTable(noteModel);
        styleTable(noteTable);
        noteTable.getColumnModel().getColumn(1).setPreferredWidth(720);

        JScrollPane noteScroll = new JScrollPane(noteTable);
        noteScroll.setBorder(BorderFactory.createTitledBorder("의사 소견 기록"));

        // ----------------------------
        // 중앙: SplitPane
        // ----------------------------
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, recordScroll, noteScroll);
        split.setResizeWeight(0.62);
        split.setBorder(null);
        root.add(split, BorderLayout.CENTER);

        // ----------------------------
        // 채우기
        // ----------------------------
        fillRecordTable(recordModel, records);
        fillNoteTable(noteModel, notes);

        // ----------------------------
        // 하단 버튼
        // ----------------------------
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeBtn = new JButton("닫기");
        closeBtn.addActionListener(e -> dispose());
        bottom.add(closeBtn);
        root.add(bottom, BorderLayout.SOUTH);
    }

    // ==========================
    // Controller 분기 로드
    // ==========================
    private List<HealthRecord> loadRecords(Object controller, Long patientId) {
        if (controller instanceof DoctorController dc) return dc.getPatientRecords(patientId);
        if (controller instanceof CaregiverController cc) return cc.getPatientRecords(patientId);
        return List.of();
    }

    private List<DoctorNote> loadNotes(Object controller, Long patientId) {
        if (controller instanceof DoctorController dc) return dc.getPatientNotes(patientId);
        if (controller instanceof CaregiverController cc) return cc.getPatientNotes(patientId);
        return List.of();
    }

    // ==========================
    // Profile Card
    // ==========================
    private JPanel createProfileCard(String patientName, Long patientId, HealthRecord latest) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(10, 12, 10, 12)
        ));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4, 6, 4, 6);
        gc.anchor = GridBagConstraints.WEST;

        JLabel title = new JLabel("환자 프로필");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        gc.gridx = 0; gc.gridy = 0; gc.gridwidth = 6; gc.weightx = 1; gc.fill = GridBagConstraints.HORIZONTAL;
        card.add(title, gc);

        gc.gridwidth = 1; gc.weightx = 0; gc.fill = GridBagConstraints.NONE;

        String age = (latest == null || latest.getAge() <= 0) ? "-" : (latest.getAge() + "세");
        String gender = (latest == null) ? "-" : nullSafe(latest.getGender());
        String smoking = (latest == null) ? "-" : nullSafe(latest.getSmoking());
        String drinking = (latest == null) ? "-" : nullSafe(latest.getDrinking());
        String activity = (latest == null) ? "-" : nullSafe(latest.getActivityLevel());
        String lastMeasured = (latest == null) ? "-" : formatDt(latest.getMeasuredAt());

        addField(card, gc, 0, 1, "이름", patientName);
        addField(card, gc, 2, 1, "ID", String.valueOf(patientId));
        addField(card, gc, 4, 1, "최근측정", lastMeasured);

        addField(card, gc, 0, 2, "나이", age);
        addField(card, gc, 2, 2, "성별", gender);
        addField(card, gc, 4, 2, "흡연", smoking);

        addField(card, gc, 0, 3, "음주", drinking);
        addField(card, gc, 2, 3, "활동", activity);

        // 빈칸 채움
        gc.gridx = 4; gc.gridy = 3; gc.weightx = 1; gc.fill = GridBagConstraints.HORIZONTAL;
        card.add(new JLabel(""), gc);

        return card;
    }

    private void addField(JPanel panel, GridBagConstraints gc, int x, int y, String label, String value) {
        JLabel l = new JLabel(label + ":");
        l.setFont(l.getFont().deriveFont(Font.BOLD));
        gc.gridx = x; gc.gridy = y;
        panel.add(l, gc);

        JLabel v = new JLabel(value);
        gc.gridx = x + 1;
        panel.add(v, gc);
    }

    // ==========================
    // Table fill
    // ==========================
    private void fillRecordTable(DefaultTableModel model, List<HealthRecord> records) {
        model.setRowCount(0);

        for (HealthRecord r : records) {
            String bp = r.getSystolicBp() + "/" + r.getDiastolicBp();
            model.addRow(new Object[] {
                    formatDt(r.getMeasuredAt()),
                    bp,
                    r.getBloodSugar(),
                    nullSafe(r.getSmoking()),
                    nullSafe(r.getDrinking()),
                    nullSafe(r.getActivityLevel()),
                    nullSafe(r.getMainRiskFactors()),
                    String.format("%.1f", r.getBmi())
            });
        }
    }

    private void fillNoteTable(DefaultTableModel model, List<DoctorNote> notes) {
        model.setRowCount(0);

        for (DoctorNote n : notes) {
            model.addRow(new Object[] {
                    formatDt(n.getCreatedAt()),
                    nullSafe(n.getContent())
            });
        }
    }

    // ==========================
    // Styling / Utils
    // ==========================
    private void styleTable(JTable table) {
        table.setRowHeight(28);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setFont(table.getTableHeader().getFont().deriveFont(Font.BOLD));

        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);

                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 247, 250));
                }

                // 가운데 정렬(날짜/수치류)
                if (col == 0 || col == 1 || col == 2 || col == 7) {
                    setHorizontalAlignment(SwingConstants.CENTER);
                } else {
                    setHorizontalAlignment(SwingConstants.LEFT);
                }

                setBorder(noFocusBorder);
                return c;
            }
        });
    }

    private String formatDt(LocalDateTime dt) {
        if (dt == null) return "-";
        return DT_FMT.format(dt);
    }

    private String nullSafe(String s) {
        return (s == null || s.isBlank()) ? "-" : s;
    }
}
