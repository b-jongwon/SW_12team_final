package domain.service;

import data.repository.UserRepository;
import domain.user.*; // User, Doctor, Patient 등 모두 임포트

import java.util.Optional;

public class AuthService {

    private final UserRepository userRepository = new UserRepository();

    /**
     * 통합 회원가입 메서드
     * 입력받은 role에 따라 Doctor, Patient, Caregiver, Admin 객체를 구체적으로 생성합니다.
     */
    public User registerUser(String loginId, String password, String name, String role, String phone, String email) {

        // 1. 비밀번호 처리 (임시로 평문, 추후 해싱 적용 시 여기서 BCrypt 사용)
        String finalPw = password;

        // 2. 역할(Role)에 따라 구체적인 자식 클래스 객체 생성 (Factory 패턴 적용)
        User user;
        switch (role) {
            case "DOCTOR":
                // 의사 객체 생성 (면허번호, 병원명은 추후 입력받거나 기본값 "N/A" 설정)
                user = new Doctor(null, loginId, finalPw, name, phone, email, "N/A", "N/A");
                break;
            case "PATIENT":
                // 환자 객체 생성
                user = new Patient(null, loginId, finalPw, name, phone, email);
                break;
            case "CAREGIVER":
                // 간병인 객체 생성
                user = new Caregiver(null, loginId, finalPw, name, phone, email);
                break;
            case "ADMIN":
                // 관리자 객체 생성
                user = new Admin(null, loginId, finalPw, name, phone, email);
                break;
            default:
                // 예외: 알 수 없는 역할인 경우 기본 User로 생성
                user = new User(null, loginId, finalPw, name, role, phone, email);
                break;
        }

        // 3. 리포지토리에 저장 (JsonUtil이 알아서 알맞은 타입으로 저장함)
        return userRepository.saveNewUser(user);
    }

    /**
     * 로그인 메서드
     * 저장된 User 객체를 찾아 비밀번호를 확인합니다.
     * (JsonUtil의 Deserializer 덕분에 Doctor 객체는 Doctor 타입으로 복원됩니다)
     */
    public Optional<User> login(String loginId, String password) {
        Optional<User> userOpt = userRepository.findByLoginId(loginId);

        // checkPassword 메서드는 User 클래스(또는 자식 클래스)에 정의된 로직을 따름
        if (userOpt.isPresent() && userOpt.get().checkPassword(password)) {
            return userOpt;
        }
        return Optional.empty();
    }
}