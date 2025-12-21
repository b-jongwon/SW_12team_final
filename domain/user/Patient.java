package domain.user;

public class Patient extends User {
    public Patient() {
        super();
        this.role = "PATIENT";
    }

    public Patient(Long id, String loginId, String password, String name, String phone, String email) {
        super(id, loginId, password, name, "PATIENT", phone, email);
    }
}