package domain.user;

public class Caregiver extends User {
    public Caregiver() {
        super();
        this.role = "CAREGIVER";
    }

    public Caregiver(Long id, String loginId, String password, String name, String phone, String email) {
        super(id, loginId, password, name, "CAREGIVER", phone, email);
    }
}