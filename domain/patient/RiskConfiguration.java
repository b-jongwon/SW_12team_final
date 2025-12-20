package domain.patient;

import infra.JsonUtil;
import java.io.File;

public class RiskConfiguration {

    private static final String CONFIG_FILE = "data/risk_config.json";

    // 1. [기본값] 전 국민 공통 기준 (베이스라인)
    // 파일에서 불러온 값이 여기에 저장됩니다.
    public static double BP_SYSTOLIC_THRESHOLD = 140.0;
    public static double BP_DIASTOLIC_THRESHOLD = 90.0;
    public static double SUGAR_THRESHOLD = 126.0;
    public static double BMI_THRESHOLD = 25.0;
    public static double CHOLESTEROL_THRESHOLD = 200.0;

    // --------------------------------------------------------
    // [New] 환자 맞춤형 기준을 담을 객체 (DTO)
    // --------------------------------------------------------
    public static class PersonalCriteria {
        public double maxSys;
        public double maxDia;
        public double maxSugar;
        public double maxBmi;

        public PersonalCriteria(double sys, double dia, double sugar, double bmi) {
            this.maxSys = sys;
            this.maxDia = dia;
            this.maxSugar = sugar;
            this.maxBmi = bmi;
        }
    }

    // --------------------------------------------------------
    // ★ [핵심] 나이와 성별에 따라 기준을 동적으로 계산해주는 메서드 ★
    // --------------------------------------------------------
    public static PersonalCriteria getPersonalizedCriteria(int age, String gender) {
        // 1. 기본 설정값에서 시작
        double targetSys = BP_SYSTOLIC_THRESHOLD;
        double targetDia = BP_DIASTOLIC_THRESHOLD;
        double targetSugar = SUGAR_THRESHOLD;
        double targetBmi = BMI_THRESHOLD;

        // 2. 나이에 따른 보정 (예: 고령자는 혈압 관리를 너무 타이트하게 안 함)
        if (age >= 80) {
            targetSys += 20; // 80세 이상은 160까지 허용 (예시)
            targetDia += 5;
        } else if (age >= 65) {
            targetSys += 10; // 65세 이상은 150까지 허용 (예시)
        }

        // 3. 성별/나이에 따른 BMI 보정 (예: 노인은 너무 마르면 안 좋음)
        if (age >= 65) {
            targetBmi += 2.0; // 노인은 BMI 27까지도 정상으로 봄
        }

        // 4. 성별에 따른 보정 (예: 남성이 복부비만 기준이 좀 더 관대하다면 등등)
        // 필요하면 추가: if ("Male".equals(gender)) { ... }

        return new PersonalCriteria(targetSys, targetDia, targetSugar, targetBmi);
    }

    // ... (아래 load, save, ConfigData 클래스는 기존 코드 그대로 유지) ...

    private static class ConfigData {
        double sys, dia, sugar, bmi, chol;
        public ConfigData(double s, double d, double su, double b, double c) {
            sys=s; dia=d; sugar=su; bmi=b; chol=c;
        }
    }

    public static void load() {
        File file = new File(CONFIG_FILE);
        if (!file.exists()) return; // 파일 없으면 기본값 사용
        try {
            ConfigData data = JsonUtil.readJson(CONFIG_FILE, ConfigData.class);
            if (data != null) {
                BP_SYSTOLIC_THRESHOLD = data.sys;
                BP_DIASTOLIC_THRESHOLD = data.dia;
                SUGAR_THRESHOLD = data.sugar;
                BMI_THRESHOLD = data.bmi;
                CHOLESTEROL_THRESHOLD = data.chol;
                System.out.println("✅ 설정을 불러왔습니다.");
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void save() {
        try {
            ConfigData data = new ConfigData(BP_SYSTOLIC_THRESHOLD, BP_DIASTOLIC_THRESHOLD, SUGAR_THRESHOLD, BMI_THRESHOLD, CHOLESTEROL_THRESHOLD);
            JsonUtil.writeJson(CONFIG_FILE, data);
        } catch (Exception e) { e.printStackTrace(); }
    }
}