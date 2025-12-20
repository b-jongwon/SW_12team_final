package ui;

import domain.patient.HealthRecord;
import domain.patient.RiskConfiguration;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

// JList의 각 항목을 '그림(Component)'으로 그려주는 렌더러
public class HealthRecordRenderer extends JPanel implements ListCellRenderer<HealthRecord> {
    private JLabel dateLabel = new JLabel();
    private JLabel bpLabel = new JLabel();
    private JLabel sugarLabel = new JLabel();
    private JLabel bmiLabel = new JLabel();
    private JLabel riskLabel = new JLabel();

    public HealthRecordRenderer() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY), // 구분선
                new EmptyBorder(10, 10, 10, 10) // 안쪽 여백
        ));

        // 1. 날짜 (왼쪽)
        dateLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        dateLabel.setPreferredSize(new Dimension(100, 50));
        add(dateLabel, BorderLayout.WEST);

        // 2. 수치 정보 (중앙)
        JPanel centerPanel = new JPanel(new GridLayout(2, 2, 5, 2));
        centerPanel.setOpaque(false); // 배경 투명하게 (메인 패널 색상 따라가게)

        // 폰트 설정
        Font valueFont = new Font("맑은 고딕", Font.BOLD, 14);
        bpLabel.setFont(valueFont);
        sugarLabel.setFont(valueFont);
        bmiLabel.setFont(valueFont);
        riskLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));

        centerPanel.add(bpLabel);
        centerPanel.add(sugarLabel);
        centerPanel.add(bmiLabel);
        centerPanel.add(riskLabel);

        add(centerPanel, BorderLayout.CENTER);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends HealthRecord> list, HealthRecord value, int index, boolean isSelected, boolean cellHasFocus) {
        // 데이터 바인딩
        dateLabel.setText("<html>" + value.getMeasuredAt().toLocalDate().toString() + "<br><small>" + value.getMeasuredAt().toLocalTime().toString().substring(0,5) + "</small></html>");

        bpLabel.setText("혈압: " + value.getSystolicBp() + "/" + value.getDiastolicBp());
        sugarLabel.setText("혈당: " + value.getBloodSugar());
        bmiLabel.setText("BMI: " + String.format("%.1f", value.getBmi()));
        riskLabel.setText("위험요인: " + (value.getMainRiskFactors().isEmpty() ? "없음" : value.getMainRiskFactors()));

        // ★ 시각화 핵심: 위험 수치에 따라 배경색 변경 (Traffic Light System)
        boolean isHighRisk = value.getSystolicBp() >= RiskConfiguration.BP_SYSTOLIC_THRESHOLD ||
                value.getBloodSugar() >= RiskConfiguration.SUGAR_THRESHOLD;

        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            if (isHighRisk) {
                setBackground(new Color(255, 230, 230));
                dateLabel.setForeground(Color.RED);
            } else {
                setBackground(Color.WHITE); // ✅ 정상: 흰색
                dateLabel.setForeground(Color.BLACK);
            }
        }

        return this;
    }
}