package ui;

import domain.patient.HealthRecord;
import domain.patient.RiskConfiguration;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

// JList의 각 항목을 '그림(Component)'으로 그려주는 렌더러
public class HealthRecordRenderer extends JPanel implements ListCellRenderer<HealthRecord> {
    private JLabel dateLabel = new JLabel();

    // 기존 라벨
    private JLabel bpLabel = new JLabel();
    private JLabel sugarLabel = new JLabel();
    private JLabel bmiLabel = new JLabel();
    private JLabel riskLabel = new JLabel();

    // [추가] 나이/성별, 생활습관(흡연/음주) 라벨
    private JLabel basicInfoLabel = new JLabel();
    private JLabel habitLabel = new JLabel();

    public HealthRecordRenderer() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY), // 구분선
                new EmptyBorder(10, 10, 10, 10) // 안쪽 여백
        ));

        // 1. 날짜 (왼쪽)
        dateLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        dateLabel.setPreferredSize(new Dimension(100, 60)); // 높이를 약간 키움
        dateLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(dateLabel, BorderLayout.WEST);

        // 2. 수치 정보 (중앙)
        // [변경] 정보를 더 많이 담기 위해 2줄 -> 3줄로 변경 (3행 2열)
        JPanel centerPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        centerPanel.setOpaque(false);

        // 폰트 설정
        Font valueFont = new Font("맑은 고딕", Font.BOLD, 13);
        Font subFont = new Font("맑은 고딕", Font.BOLD, 12);

        bpLabel.setFont(valueFont);
        sugarLabel.setFont(valueFont);
        bmiLabel.setFont(valueFont);
        riskLabel.setFont(subFont);

        // [추가] 새 라벨 폰트 설정
        basicInfoLabel.setFont(subFont);
        habitLabel.setFont(subFont);
        basicInfoLabel.setForeground(Color.DARK_GRAY);
        habitLabel.setForeground(Color.DARK_GRAY);

        // 패널에 순서대로 추가
        // 1행
        centerPanel.add(bpLabel);
        centerPanel.add(sugarLabel);

        // 2행
        centerPanel.add(bmiLabel);
        centerPanel.add(riskLabel);

        // 3행 (새로 추가된 정보)
        centerPanel.add(basicInfoLabel);
        centerPanel.add(habitLabel);

        add(centerPanel, BorderLayout.CENTER);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends HealthRecord> list, HealthRecord value, int index, boolean isSelected, boolean cellHasFocus) {
        // 1. 날짜 바인딩
        String dateStr = value.getMeasuredAt().toLocalDate().toString();
        String timeStr = value.getMeasuredAt().toLocalTime().toString().substring(0,5);
        dateLabel.setText("<html><div style='text-align:center;'>" + dateStr + "<br><small>" + timeStr + "</small></div></html>");

        // 2. 주요 수치 바인딩
        bpLabel.setText("혈압: " + value.getSystolicBp() + "/" + value.getDiastolicBp());
        sugarLabel.setText("혈당: " + value.getBloodSugar());
        bmiLabel.setText("BMI: " + String.format("%.1f", value.getBmi()));
        riskLabel.setText("위험요인: " + (value.getMainRiskFactors().isEmpty() ? "없음" : value.getMainRiskFactors()));

        // 3. [추가] 상세 정보 바인딩 (나이, 성별, 흡연, 음주)
        String genderKor = "정보없음";
        if ("Male".equalsIgnoreCase(value.getGender())) genderKor = "남성";
        else if ("Female".equalsIgnoreCase(value.getGender())) genderKor = "여성";

        // 예: [25세 / 남성]
        basicInfoLabel.setText(String.format("인적사항: %d세 / %s", value.getAge(), genderKor));

        // 예: [흡연: Yes / 음주: Occasional]
        habitLabel.setText(String.format("생활: 흡연(%s) / 음주(%s) / 활동(%s)",
                value.getSmoking(), value.getDrinking(), value.getActivityLevel()));

        // 4. 색상 처리 (위험도에 따른 배경색)
        boolean isHighRisk = value.getSystolicBp() >= RiskConfiguration.BP_SYSTOLIC_THRESHOLD ||
                value.getBloodSugar() >= RiskConfiguration.SUGAR_THRESHOLD;

        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            if (isHighRisk) {
                setBackground(new Color(255, 235, 235)); // 위험: 연한 빨강
                dateLabel.setForeground(Color.RED);
            } else {
                setBackground(Color.WHITE); // 정상: 흰색
                dateLabel.setForeground(Color.BLACK);
            }
        }

        return this;
    }
}