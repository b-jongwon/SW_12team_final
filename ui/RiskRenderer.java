package ui;

import domain.patient.HealthRecord;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * HealthRecord(건강 기록) 객체를 리스트에서 보여줄 때,
 * 단순 텍스트가 아닌 '위험도에 따라 색상이 변하는 카드 형태'로 그려주는 렌더러
 */
public class RiskRenderer extends JPanel implements ListCellRenderer<HealthRecord> {

    // 컴포넌트 재사용을 위해 필드로 선언
    private JLabel dateLabel = new JLabel();
    private JLabel bpLabel = new JLabel();
    private JLabel sugarLabel = new JLabel();
    private JLabel bmiLabel = new JLabel();
    private JLabel riskLabel = new JLabel();
    private JPanel centerPanel = new JPanel(new GridLayout(2, 2, 5, 2));

    public RiskRenderer() {
        // 1. 전체 레이아웃 및 스타일 설정
        setLayout(new BorderLayout(10, 10));
        setOpaque(true); // ★ 중요: 이게 있어야 배경색(빨강/흰색)이 보입니다.

        // 테두리: 아래쪽에 회색 구분선, 안쪽에 여백
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                new EmptyBorder(10, 10, 10, 10)
        ));

        // 2. 날짜 (왼쪽 영역)
        dateLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        dateLabel.setHorizontalAlignment(SwingConstants.CENTER); // 가운데 정렬
        dateLabel.setPreferredSize(new Dimension(100, 50)); // 너비 고정
        add(dateLabel, BorderLayout.WEST);

        // 3. 수치 정보 (중앙 영역) - 격자 배열 (2행 2열)
        centerPanel.setOpaque(false); // ★ 중요: 투명하게 해야 뒤쪽 배경색이 비쳐 보임

        // 폰트 통일
        Font valueFont = new Font("맑은 고딕", Font.BOLD, 14);
        bpLabel.setFont(valueFont);
        sugarLabel.setFont(valueFont);
        bmiLabel.setFont(valueFont);
        riskLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        riskLabel.setForeground(Color.DARK_GRAY);

        centerPanel.add(bpLabel);
        centerPanel.add(sugarLabel);
        centerPanel.add(bmiLabel);
        centerPanel.add(riskLabel);

        add(centerPanel, BorderLayout.CENTER);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends HealthRecord> list,
                                                  HealthRecord value,
                                                  int index,
                                                  boolean isSelected,
                                                  boolean cellHasFocus) {

        // 1. 데이터 바인딩 (값을 화면에 꽂기)
        // 날짜/시간 처리 (HTML 태그를 사용해 두 줄로 표시)
        String dateStr = value.getMeasuredAt().toLocalDate().toString();
        String timeStr = value.getMeasuredAt().toLocalTime().toString().substring(0, 5); // HH:mm
        dateLabel.setText("<html><div style='text-align:center;'>" + dateStr + "<br><small>" + timeStr + "</small></div></html>");

        bpLabel.setText("혈압: " + value.getSystolicBp() + "/" + value.getDiastolicBp());
        sugarLabel.setText("혈당: " + value.getBloodSugar());
        bmiLabel.setText(String.format("BMI: %.1f", value.getBmi()));

        // 위험요인 텍스트 처리 (null 체크 포함)
        String risks = value.getMainRiskFactors();
        if (risks == null || risks.trim().isEmpty()) {
            riskLabel.setText("위험요인: 없음");
        } else {
            riskLabel.setText("위험요인: " + risks);
        }

        // 2. ★ 시각화 핵심: 위험 수치(고혈압/고혈당)에 따른 배경색 변경 (Traffic Light System)
        // 기준: 수축기 혈압 140 이상 OR 혈당 126 이상
        boolean isHighRisk = value.getSystolicBp() >= 140 || value.getBloodSugar() >= 126;

        if (isSelected) {
            // 리스트에서 선택되었을 때 (파란색 계열)
            setBackground(new Color(220, 230, 255));
            dateLabel.setForeground(Color.BLUE);
        } else {
            // 선택 안 되었을 때 (위험도에 따라 분기)
            if (isHighRisk) {
                setBackground(new Color(255, 230, 230)); // 연한 빨강 (경고)
                dateLabel.setForeground(Color.RED);      // 날짜 글씨도 빨갛게 강조
            } else {
                setBackground(Color.WHITE);              // 정상: 흰색
                dateLabel.setForeground(Color.BLACK);
            }
        }

        return this; // 완성된 패널(나 자신)을 반환
    }
}