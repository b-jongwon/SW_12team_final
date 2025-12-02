package ui;

import presentation.controller.PatientController;
import domain.user.User;
import domain.medical.HealthRecord;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PatientPanel extends JPanel {
    private final PatientController controller = new PatientController();
    private User user;

    public PatientPanel(User user) {
        this.user = user;
        setLayout(new BorderLayout());

        // 버튼
        JButton addRecordBtn = new JButton("건강 기록 추가");
        JButton listRecordBtn = new JButton("기록 조회");

        JPanel top = new JPanel();
        top.add(addRecordBtn);
        top.add(listRecordBtn);

        JTextArea output = new JTextArea();
        output.setEditable(false);

        add("North", top);
        add("Center", new JScrollPane(output));

        // 추가 버튼
        addRecordBtn.addActionListener(e -> {
            controller.addRecord(
                    user.getId(),
                    130, 85, 110.5,
                    "No", "Occasional",
                    "Medium", "고혈압 위험",
                    1.75, 65
            );
            output.append("건강 기록 하나 추가됨!\n");
        });

        // 조회 버튼
        listRecordBtn.addActionListener(e -> {
            List<HealthRecord> list = controller.getRecords(user.getId());
            output.append("=== 건강 기록 목록 ===\n");
            for (HealthRecord r : list) {
                output.append(r.summary() + "\n");
            }
        });
    }
}
