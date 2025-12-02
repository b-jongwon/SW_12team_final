
package domain.user;

public class User {
    private Long id;
    private String loginId;
    private String password;
    private String name;
    private String role;
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

    public boolean checkPassword(String pw) {
        return this.password != null && this.password.equals(pw);
    }

    public void changePassword(String newPw) {
        this.password = newPw;
    }

    public void updateContactInfo(String newPhone, String newEmail) {
        this.phone = newPhone;
        this.email = newEmail;
    }

    public Long getId() {
        return id;
    }
}
