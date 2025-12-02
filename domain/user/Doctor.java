package domain.user;

public class Doctor extends User {
    private String licenseNo;
    private String hospitalName;

    public Doctor() {
        super();
        // [수정] setRole("DOCTOR"); 대신 직접 할당
        this.role = "DOCTOR";
    }

    public Doctor(Long id, String loginId, String password, String name, String phone, String email, String licenseNo, String hospitalName) {
        // [수정] 부모 생성자에 "DOCTOR"를 직접 전달하거나, super 호출 후 this.role 설정
        super(id, loginId, password, name, "DOCTOR", phone, email);
        this.licenseNo = licenseNo;
        this.hospitalName = hospitalName;
    }

    public String getLicenseNo() { return licenseNo; }
    public void setLicenseNo(String licenseNo) { this.licenseNo = licenseNo; }
    public String getHospitalName() { return hospitalName; }
    public void setHospitalName(String hospitalName) { this.hospitalName = hospitalName; }
}