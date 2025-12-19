import domain.community.CommunityPost;
import domain.messaging.Message;
import domain.patient.GroupComparisonResult;
import domain.patient.HealthRecord;
import domain.patient.PersonalReport;
import domain.user.User;
import presentation.controller.*;

import java.io.File;
import java.util.Random;

public class Main {

    public static void main(String[] args) {
        domain.patient.RiskConfiguration.load();

        clearAllData();
        System.out.println("===== Stroke Prevention System Test Start =====");

        // --- Controllers ---
        AuthController auth = new AuthController();
        PatientController patient = new PatientController();
        ReportController report = new ReportController();
        AssignmentController assignment = new AssignmentController();
        MessagingController message = new MessagingController();
        CommunityController community = new CommunityController();

        // -------------------------
        // 1. 회원가입
        // -------------------------
        User p1 = auth.register("환자1", "1234", "환자1", "PATIENT");
        System.out.println("✅ 환자 등록: " + p1.getName());

        User d1 = auth.register("의사1", "1234", "의사1", "DOCTOR");
        System.out.println("✅ 의사 등록: " + d1.getName());

        User c1 = auth.register("보호자1", "1234", "보호자1", "CAREGIVER");
        System.out.println("✅ 보호자 등록: " + c1.getName());

        // [추가] 관리자 계정 생성
        User admin = auth.register("관리자", "1234", "시스템관리자", "ADMIN");
        System.out.println("✅ 관리자 등록: " + admin.getName());

        System.out.println("\n[System] 그룹 비교 분석을 위한 가상 데이터 생성 중...");
        generateDummyData(auth, patient);
        System.out.println("[System] 가상 데이터 10건 생성 완료.\n");

        // -------------------------
        // 5. 배정 / 리마인더 / 규칙
        // -------------------------
        var assign = assignment.assign(p1.getId(), d1.getId(), null);
        System.out.println("환자 배정 완료: doctor=" + assign.getDoctorId());

        var reminder = assignment.createReminder(p1.getId(), "bloodPressure", "daily", "혈압 측정하세요");
        System.out.println("리마인더 등록됨");

        var rule = assignment.createRule(p1.getId(), "BP_HIGH", "혈압 경고 알림");
        System.out.println("규칙 등록됨");



        // -------------------------
        // 전체 종료
        // -------------------------
        System.out.println("===== All tests finished! =====");
    }

    // [NEW] 가상 데이터 생성 메서드
    private static void generateDummyData(AuthController auth, PatientController patientCtrl) {
        Random random = new Random();

        // 10명의 추가 환자를 만들고 랜덤 건강 기록을 입력
        for (int i = 1; i <= 10; i++) {
            // 1. 환자 계정 생성
            String name = "비교군환자" + i;
            User dummyUser = auth.register("patient" + i, "1234", name, "PATIENT");

            // 2. 랜덤 수치 생성 (정상 범위 ~ 위험 범위 섞음)
            // 수축기 혈압: 100 ~ 160
            int sys = 100 + random.nextInt(61);
            // 이완기 혈압: 60 ~ 100
            int dia = 60 + random.nextInt(41);
            // 혈당: 70 ~ 200
            double sugar = 70 + random.nextInt(131);
            // BMI용 키/몸무게
            double height = 1.6 + (random.nextDouble() * 0.3); // 1.6m ~ 1.9m
            double weight = 50 + random.nextInt(50);           // 50kg ~ 100kg

            // 흡연/음주 여부 랜덤
            String smoking = random.nextBoolean() ? "Yes" : "No";
            String drinking = random.nextBoolean() ? "Frequent" : "None";

            // 3. 건강 기록 저장 (검사에 반영될 수치들)
            patientCtrl.addRecord(
                    dummyUser.getId(),
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
            return; // 폴더가 없으면 삭제할 것도 없음
        }

        File[] files = dataDir.listFiles();
        if (files != null) {
            for (File file : files) {
                // json 파일과 id 관리용 txt 파일만 골라서 삭제
                if (file.getName().endsWith(".json") || file.getName().endsWith(".txt")) {
                    file.delete();
                }
            }
        }
        System.out.println("/ [System] 기존 데이터 파일들을 모두 삭제했습니다.");
    }
}
