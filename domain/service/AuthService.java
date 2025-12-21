package domain.service;

import data.repository.UserRepository;
import domain.user.*; // User, Doctor, Patient 등 모두 임포트

import java.util.Optional;

public class AuthService {

    private final UserRepository userRepository = new UserRepository();


    public User registerUser(String loginId, String password, String name, String role, String phone, String email) {

        // 1. 비밀번호 처리
        String finalPw = password;


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

        // 3. 리포지토리에 저장
        return userRepository.saveNewUser(user);
    }


    public Optional<User> login(String loginId, String password) {
        Optional<User> userOpt = userRepository.findByLoginId(loginId);

        // checkPassword 메서드는 User 클래스(또는 자식 클래스)에 정의된 로직을 따름
        if (userOpt.isPresent() && userOpt.get().checkPassword(password)) {
            return userOpt;
        }
        return Optional.empty();
    }
}