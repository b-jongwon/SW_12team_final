package domain.user;

public class Admin extends User {
    public Admin() {
        super();
        // [수정] setRole("ADMIN"); -> this.role = "ADMIN";
        this.role = "ADMIN";
    }

    public Admin(Long id, String loginId, String password, String name, String phone, String email) {
        super(id, loginId, password, name, "ADMIN", phone, email);
    }
}