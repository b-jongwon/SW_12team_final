import domain.user.User;
import presentation.controller.*;

import java.io.File;
import java.util.Random;

public class Main {

    public static void main(String[] args) {
        // 1. 설정 및 기존 데이터 초기화
        domain.patient.RiskConfiguration.load();
        clearAllData();
        System.out.println("===== [System] 시연용 초기 데이터 생성을 시작합니다 =====");

        // --- Controllers ---
        AuthController auth = new AuthController();
        PatientController patient = new PatientController();
        AssignmentController assignment = new AssignmentController();
        AdminController adminCtrl = new AdminController();

        // -------------------------
        // 1. 핵심 사용자 등록
        // -------------------------
        User p1 = auth.register("환자1", "1234", "김철수(환자1)", "PATIENT");
        User p2 = auth.register("환자2", "1234", "이영희(환자2)", "PATIENT");

        User d1 = auth.register("의사1", "1234", "김닥터(의사1)", "DOCTOR");
        User d2 = auth.register("의사2", "1234", "이닥투(의사2)", "DOCTOR");

        User c1 = auth.register("보호자1", "1234", "박가족(보호자1)", "CAREGIVER");
        User c2  = auth.register("보호자2", "1234", "이가족(보호자2)", "CAREGIVER");

        User admin = auth.register("관리자", "1234", "시스템관리자", "ADMIN");

        System.out.println("✅ 핵심 사용자 등록 완료");

        // -------------------------
        // 2. 관계 배정
        // -------------------------
        try {
            assignment.assign(p1.getId(), d1.getId(), null);
            assignment.assign(p1.getId(), null, c1.getId());
            System.out.println("✅ 의사/보호자 <-> 환자 연결 완료");
        } catch (Exception e) {
            System.out.println("⚠️ 배정 중 오류: " + e.getMessage());
        }

        // -------------------------
        // 3. 비교군 데이터 생성 (50명) - 나이/성별 포함
        // -------------------------
        System.out.println("📊 또래 비교용 가상 데이터 50건 생성 중...");
        generateDummyData(auth, patient, 50);
        System.out.println("✅ 가상 데이터 생성 완료");

        // -------------------------
        // 4. 환자1, 환자2 초기 건강 기록 입력 (수정됨: 나이, 성별 추가)
        // -------------------------

        // [환자1] 52세 남성, 고위험군
        patient.addRecord(
                p1.getId(),
                52, "Male",        // [추가된 부분] 나이, 성별
                150, 95, 180.0,    // 혈압, 혈당
                "Yes", "Frequent", "Low",
                "가족력 있음", 175, 85.0
        );



        System.out.println("✅ 환자1(고위험), 환자2(정상) 초기 기록 입력 완료");

        // -------------------------
        // 5. 공지사항 및 기본 설정
        // -------------------------
        adminCtrl.postAnnouncement("시스템 점검 안내", "12월 25일 크리스마스 시스템 점검 예정입니다.");
        adminCtrl.postAnnouncement("겨울철 뇌졸중 예방 수칙", "기온이 급격히 떨어지는 새벽 운동을 자제하세요.");

        System.out.println("===== [System] 모든 데이터 준비가 완료되었습니다. =====");
    }

    // [가상 데이터 생성기] - 나이와 성별 랜덤 생성 추가
    private static void generateDummyData(AuthController auth, PatientController patientCtrl, int count) {
        Random random = new Random();

        for (int i = 1; i <= count; i++) {
            // 1. 유령 회원 생성
            String name = "Dummy" + i;
            User dummyUser = auth.register("dummy" + i, "1234", name, "PATIENT");

            // 2. 랜덤 데이터 생성

            // [NEW] 나이 (20세 ~ 79세)
            int age = 20 + random.nextInt(60);

            // [NEW] 성별
            String gender = random.nextBoolean() ? "Male" : "Female";

            // 혈압/혈당 (30% 확률로 고위험군)
            int sys, dia;
            double sugar;
            if (random.nextInt(10) < 3) {
                sys = 140 + random.nextInt(40);
                dia = 90 + random.nextInt(30);
                sugar = 130 + random.nextInt(100);
            } else {
                sys = 100 + random.nextInt(39);
                dia = 60 + random.nextInt(29);
                sugar = 70 + random.nextInt(59);
            }

            double height = 1.6 + (random.nextDouble() * 0.3);
            double weight = 50 + random.nextInt(60);
            String smoking = random.nextBoolean() ? "Yes" : "No";
            String drinking = random.nextBoolean() ? "Frequent" : "None";

            // 3. 기록 저장 (변경된 파라미터 적용)
            patientCtrl.addRecord(
                    dummyUser.getId(),
                    age, gender,  // [추가됨]
                    sys, dia, sugar,
                    smoking, drinking, "Medium",
                    "Dummy Data",
                    height, weight
            );
        }
    }

    public static void clearAllData() {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdir();
            return;
        }
        File[] files = dataDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().endsWith(".json") || file.getName().endsWith(".txt")) {
                    file.delete();
                }
            }
        }
        System.out.println("🧹 기존 데이터 삭제 완료");
    }
}