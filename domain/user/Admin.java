package domain.user;

public class Admin extends User {
    public Admin() {
        super();
        this.role = "ADMIN";
    }

    public Admin(Long id, String loginId, String password, String name, String phone, String email) {
        super(id, loginId, password, name, "ADMIN", phone, email);
    }
}