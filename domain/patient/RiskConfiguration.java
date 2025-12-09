package domain.patient;

import infra.JsonUtil;
import java.io.File;

public class RiskConfiguration {

    // 저장할 파일 경로
    private static final String CONFIG_FILE = "data/risk_config.json";

    // 1. 기준값 (기본값으로 초기화해두지만, 파일이 있으면 덮어씌워짐)
    public static double BP_SYSTOLIC_THRESHOLD = 140.0;  // 수축기 혈압
    public static double BP_DIASTOLIC_THRESHOLD = 90.0;  // 이완기 혈압
    public static double SUGAR_THRESHOLD = 126.0;        // 공복 혈당
    public static double BMI_THRESHOLD = 25.0;           // 비만 (BMI)
    public static double CHOLESTEROL_THRESHOLD = 200.0;  // 콜레스테롤 (예시)

    // --------------------------------------------------------
    // [핵심] 설정을 저장하고 불러오기 위한 내부 DTO 클래스
    // (Static 변수는 바로 JSON 변환이 안 되므로, 객체에 담아서 저장함)
    // --------------------------------------------------------
    private static class ConfigData {
        double sys;
        double dia;
        double sugar;
        double bmi;
        double chol;

        public ConfigData(double sys, double dia, double sugar, double bmi, double chol) {
            this.sys = sys;
            this.dia = dia;
            this.sugar = sugar;
            this.bmi = bmi;
            this.chol = chol;
        }
    }

    // 2. 파일에서 설정 불러오기 (Main 실행 시 호출)
    public static void load() {
        File file = new File(CONFIG_FILE);
        if (!file.exists()) {
            System.out.println("ℹ️ [RiskConfig] 저장된 설정 파일이 없어 기본값을 사용합니다.");
            return;
        }

        try {
            // JSON 읽어서 DTO로 변환
            ConfigData data = JsonUtil.readJson(CONFIG_FILE, ConfigData.class);
            if (data != null) {
                // 읽어온 값을 static 변수에 적용
                BP_SYSTOLIC_THRESHOLD = data.sys;
                BP_DIASTOLIC_THRESHOLD = data.dia;
                SUGAR_THRESHOLD = data.sugar;
                BMI_THRESHOLD = data.bmi;
                CHOLESTEROL_THRESHOLD = data.chol;
                System.out.println("✅ [RiskConfig] 위험도 기준 설정을 파일에서 불러왔습니다.");
            }
        } catch (Exception e) {
            System.out.println("⚠️ [RiskConfig] 설정 로드 실패 (기본값 사용): " + e.getMessage());
        }
    }

    // 3. 현재 설정을 파일에 저장하기 (Admin이 수정 시 호출)
    public static void save() {
        try {
            // 현재 static 변수 값들을 DTO에 담기
            ConfigData data = new ConfigData(
                    BP_SYSTOLIC_THRESHOLD,
                    BP_DIASTOLIC_THRESHOLD,
                    SUGAR_THRESHOLD,
                    BMI_THRESHOLD,
                    CHOLESTEROL_THRESHOLD
            );

            // JSON 파일로 저장
            JsonUtil.writeJson(CONFIG_FILE, data);
            System.out.println("💾 [RiskConfig] 변경된 설정을 파일에 저장했습니다.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}