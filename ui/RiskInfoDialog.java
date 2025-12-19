package ui;

import domain.patient.HealthRecord;
import domain.patient.RiskConfiguration;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;

public class RiskInfoDialog extends JDialog {

    public RiskInfoDialog(Window owner, HealthRecord myLastRecord) {
        super(owner, "위험도 분석 상세 기준 정보", ModalityType.APPLICATION_MODAL);
        setSize(480, 420); // 창 크기 설정
        setLocationRelativeTo(owner); // 부모 창 중앙에 띄움
        setLayout(new BorderLayout());

        // 1. 현재 설정된 기준값 가져오기
        double sysLimit = RiskConfiguration.BP_SYSTOLIC_THRESHOLD;
        double diaLimit = RiskConfiguration.BP_DIASTOLIC_THRESHOLD;
        double sugarLimit = RiskConfiguration.SUGAR_THRESHOLD;
        double bmiLimit = RiskConfiguration.BMI_THRESHOLD;

        // 2. 출력할 텍스트 구성
        StringBuilder sb = new StringBuilder();
        sb.append("=== 🛑 뇌졸중 위험 판단 기준 (Risk Criteria) ===\n\n");
        sb.append(String.format(" • 수축기 혈압 : %.0f mmHg 이상 (위험)\n", sysLimit));
        sb.append(String.format(" • 이완기 혈압 : %.0f mmHg 이상 (위험)\n", diaLimit));
        sb.append(String.format(" • 공복 혈당   : %.0f mg/dL 이상 (당뇨 위험)\n", sugarLimit));
        sb.append(String.format(" • 비만도(BMI) : %.0f 이상 (비만)\n", bmiLimit));

        sb.append("\n============================================\n\n");
        sb.append("=== 👤 환자 데이터 분석 (Patient Data) ===\n\n");

        if (myLastRecord != null) {
            sb.append(String.format(" • 수축기 혈압 : %d mmHg ", myLastRecord.getSystolicBp()));
            sb.append(myLastRecord.getSystolicBp() >= sysLimit ? "(⚠️초과)" : "(✅정상)");
            sb.append("\n");

            sb.append(String.format(" • 이완기 혈압 : %d mmHg ", myLastRecord.getDiastolicBp()));
            sb.append(myLastRecord.getDiastolicBp() >= diaLimit ? "(⚠️초과)" : "(✅정상)");
            sb.append("\n");

            sb.append(String.format(" • 공복 혈당   : %.1f mg/dL ", myLastRecord.getBloodSugar()));
            sb.append(myLastRecord.getBloodSugar() >= sugarLimit ? "(⚠️초과)" : "(✅정상)");
            sb.append("\n");

            sb.append(String.format(" • BMI (체질량): %.1f ", myLastRecord.getBmi()));
            sb.append(myLastRecord.getBmi() >= bmiLimit ? "(⚠️초과)" : "(✅정상)");
            sb.append("\n");

            String dateStr = myLastRecord.getMeasuredAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            sb.append("\n(측정 일시: " + dateStr + ")");
        } else {
            sb.append("(분석할 건강 데이터가 존재하지 않습니다)\n");
        }

        // 3. 텍스트 영역에 표시
        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 14));
        area.setMargin(new Insets(15, 15, 15, 15)); // 여백

        add(new JScrollPane(area), BorderLayout.CENTER);

        // 4. 닫기 버튼
        JButton closeBtn = new JButton("닫기");
        closeBtn.addActionListener(e -> dispose());
        JPanel btnPanel = new JPanel();
        btnPanel.add(closeBtn);
        add(btnPanel, BorderLayout.SOUTH);
    }
}