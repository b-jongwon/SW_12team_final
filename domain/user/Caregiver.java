package domain.user;

public class Caregiver extends User {
    public Caregiver() {
        super();
        // [수정] setRole("CAREGIVER"); -> this.role = "CAREGIVER";
        this.role = "CAREGIVER";
    }

    public Caregiver(Long id, String loginId, String password, String name, String phone, String email) {
        super(id, loginId, password, name, "CAREGIVER", phone, email);
    }
}