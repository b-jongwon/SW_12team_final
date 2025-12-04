package ui;

import domain.medical.DoctorNote;
import domain.patient.HealthRecord;
import presentation.controller.DoctorController;
import presentation.controller.CaregiverController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PatientDetailDialog extends JDialog {

    public PatientDetailDialog(JFrame parent, String patientName, Long patientId, Object controller) {
        super(parent, patientName + "님의 상세 건강 정보", true); // 모달 창
        setSize(800, 600);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(2, 1)); // 위: 건강기록, 아래: 의사소견

        // ----------------------------------------------------
        // 1. 건강 기록 히스토리 (위쪽)
        // ----------------------------------------------------
        String[] recordCols = {"측정일시", "혈압(수/이)", "혈당", "위험요인", "BMI"};
        DefaultTableModel recordModel = new DefaultTableModel(recordCols, 0);
        JTable recordTable = new JTable(recordModel);
        JScrollPane recordScroll = new JScrollPane(recordTable);
        recordScroll.setBorder(BorderFactory.createTitledBorder("📋 건강 기록 내역"));

        add(recordScroll);

        // ----------------------------------------------------
        // 2. 의사 소견 히스토리 (아래쪽)
        // ----------------------------------------------------
        String[] noteCols = {"작성일", "소견 내용"};
        DefaultTableModel noteModel = new DefaultTableModel(noteCols, 0);
        JTable noteTable = new JTable(noteModel);
        JScrollPane noteScroll = new JScrollPane(noteTable);
        noteScroll.setBorder(BorderFactory.createTitledBorder("👨‍⚕️ 의사 선생님 소견 기록"));

        add(noteScroll);

        // ----------------------------------------------------
        // 데이터 로드 로직 (Controller 타입에 따라 분기)
        // ----------------------------------------------------
        List<HealthRecord> records = null;
        List<DoctorNote> notes = null;

        if (controller instanceof DoctorController) {
            DoctorController dc = (DoctorController) controller;
            records = dc.getPatientRecords(patientId); // 컨트롤러에 이 메서드 추가 필요!
            notes = dc.getPatientNotes(patientId);     // 컨트롤러에 이 메서드 추가 필요!
        } else if (controller instanceof CaregiverController) {
            CaregiverController cc = (CaregiverController) controller;
            records = cc.getPatientRecords(patientId); // 컨트롤러에 이 메서드 추가 필요!
            notes = cc.getPatientNotes(patientId);     // 컨트롤러에 이 메서드 추가 필요!
        }

        // 테이블 채우기
        if (records != null) {
            for (HealthRecord r : records) {
                String bp = r.getSystolicBp() + "/" + r.getDiastolicBp();
                recordModel.addRow(new Object[]{
                        r.getMeasuredAt(), bp, r.getBloodSugar(),
                        r.getMainRiskFactors(), String.format("%.1f", r.getBmi())
                });
            }
        }

        if (notes != null) {
            for (DoctorNote n : notes) {
                noteModel.addRow(new Object[]{n.getCreatedAt(), n.getContent()});
            }
        }
    }
}