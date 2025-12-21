package domain.user;

public class Doctor extends User {
    private String licenseNo;
    private String hospitalName;

    public Doctor() {
        super();
        this.role = "DOCTOR";
    }

    public Doctor(Long id, String loginId, String password, String name, String phone, String email, String licenseNo, String hospitalName) {
        super(id, loginId, password, name, "DOCTOR", phone, email);
        this.licenseNo = licenseNo;
        this.hospitalName = hospitalName;
    }

    public String getLicenseNo() { return licenseNo; }
    public void setLicenseNo(String licenseNo) { this.licenseNo = licenseNo; }
    public String getHospitalName() { return hospitalName; }
    public void setHospitalName(String hospitalName) { this.hospitalName = hospitalName; }
}