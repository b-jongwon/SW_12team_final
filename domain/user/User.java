package domain.user;

public class User {
    private Long id;
    private String loginId;
    private String password; // (추후 해싱된 값)
    private String name;

    // [변경 1] private -> protected (자식 클래스인 Doctor, Patient가 설정할 수 있게)
    protected String role;

    private String phone;
    private String email;

    public User() {}

    public User(Long id, String loginId, String password, String name,
                String role, String phone, String email) {
        this.id = id;
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.role = role;
        this.phone = phone;
        this.email = email;
    }

    // [중요] checkPassword, changePassword 등 비즈니스 로직은 유지
    public boolean checkPassword(String pw) {
        // (임시 평문 비교, 추후 해싱 로직 적용)
        return this.password != null && this.password.equals(pw);
    }

    public void changePassword(String newPw) {
        this.password = newPw;
    }

    public void updateContactInfo(String newPhone, String newEmail) {
        this.phone = newPhone;
        this.email = newEmail;
    }

    // --- Getters & Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLoginId() { return loginId; }
    public void setLoginId(String loginId) { this.loginId = loginId; }

    public String getPassword() { return password; }
    // setPassword는 유지 (비밀번호 변경 등 필요)
    public void setPassword(String password) { this.password = password; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRole() { return role; }

    // [변경 2] 🚨 setRole() 메서드 삭제!
    // 이제 역할은 생성될 때(new Doctor 등) 결정되며, 중간에 바꿀 수 없습니다.
    // public void setRole(String role) { this.role = role; } (삭제됨)

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}