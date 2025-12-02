import presentation.controller.*;
import domain.user.User;
import domain.patient.HealthRecord;
import domain.patient.PersonalReport;
import domain.patient.GroupComparisonResult;
import domain.messaging.Message;
import domain.community.CommunityPost;


public class Main {

    public static void main(String[] args) {

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
        User p1 = auth.register("patient1", "1234", "김환자", "PATIENT");
        System.out.println("✅ 환자 등록: " + p1.getName());

        User d1 = auth.register("doctor1", "1234", "이의사", "DOCTOR");
        System.out.println("✅ 의사 등록: " + d1.getName());

        User c1 = auth.register("caregiver1", "1234", "박보호", "CAREGIVER");
        System.out.println("✅ 보호자 등록: " + c1.getName());

        // [추가] 관리자 계정 생성
        User admin = auth.register("관리자", "1234", "시스템관리자", "ADMIN");
        System.out.println("✅ 관리자 등록: " + admin.getName());

        // -------------------------
        // 2. 로그인 테스트
        // -------------------------
        var loginResult = auth.login("patient1", "1234");
        System.out.println("로그인 결과: " + loginResult);


        // -------------------------
        // 3. 환자 건강 기록 입력
        // -------------------------
        HealthRecord rec = patient.addRecord(
                p1.getId(), 130, 85, 110.5,
                "No", "Occasional", "Medium", "고혈압 위험",
                1.75, 65
        );
        System.out.println("건강 기록 생성됨: " + rec.summary());


        // -------------------------
        // 4. 리포트 생성
        // -------------------------
        PersonalReport personal = report.createPersonal(
                p1.getId(),
                "전반적으로 안정적 상태",
                "합병증 위험은 낮음"
        );
        System.out.println("개인 리포트 생성됨: " + personal.summarize());

        GroupComparisonResult group = report.createGroup(
                p1.getId(), "AGE_GROUP_20_30",
                72.3, 65.0,
                "그래프데이터임"
        );
        System.out.println("그룹 비교 리포트 생성됨 (ID=" + group.getId() + ")");


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
        // 6. 메시징 시스템
        // -------------------------
        var thread = message.createThread(p1.getId(), null, d1.getId());
        System.out.println("메시지 스레드 생성됨: thread=" + thread.getId());

        Message msg = message.send(thread.getId(), p1.getId(), "안녕하세요 의사쌤!");
        System.out.println("메시지 전송됨: " + msg.getContent());


        // -------------------------
        // 7. 커뮤니티 시스템
        // -------------------------
        CommunityPost post = community.post(p1.getId(), "오늘 운동 인증!", "조깅 3km 뛰었습니다!");
        System.out.println("커뮤니티 게시물 작성됨: " + post);

        var comment = community.comment(post.getId(), p1.getId(), "댓글도 남겨요!");
        System.out.println("댓글 작성됨");


        // -------------------------
        // 전체 종료
        // -------------------------
        System.out.println("===== All tests finished! =====");
    }
}
