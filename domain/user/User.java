package domain.user;

public class User {
    private Long id;
    private String loginId;
    private String password; // (추후 해싱된 값)
    private String name;

    protected String role;

    private String phone;
    private String email;

    private int birthYear;

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


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLoginId() { return loginId; }
    public void setLoginId(String loginId) { this.loginId = loginId; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRole() { return role; }



    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(int birthYear) {
        this.birthYear = birthYear;
    }
}