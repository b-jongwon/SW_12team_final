package ui;

import presentation.controller.ReportController;
import domain.patient.PersonalReport;

import javax.swing.*;
import java.awt.*;

public class PersonalReportDialog extends JDialog {

    public PersonalReportDialog(Window owner, Long patientId) {
        super(owner, "📄 개인 건강 맞춤 리포트", ModalityType.APPLICATION_MODAL);
        setSize(500, 600);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 14));
        area.setMargin(new Insets(20, 20, 20, 20));

        // 컨트롤러를 통해 리포트 생성 및 가져오기
        ReportController controller = new ReportController();

        // Service에서 만들어진 PersonalReport 객체를 받아옴
        PersonalReport report = controller.createPersonalReport(patientId);

        // PersonalReport 객체의 getFormatText()를 호출하여 화면에 출력
        area.setText(report.getFormatText());
        area.setCaretPosition(0); // 스크롤 맨 위로

        add(new JScrollPane(area), BorderLayout.CENTER);

        JButton closeBtn = new JButton("닫기");
        closeBtn.addActionListener(e -> dispose());
        add(closeBtn, BorderLayout.SOUTH);
    }
}