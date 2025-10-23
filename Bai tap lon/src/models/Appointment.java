package models;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

public class Appointment {
    private int id;
    private int patientId;
    private int doctorId;
    private LocalDate date;
    private LocalTime time;
    private String notes;
    private String status;
    private String roomNumber;
    private String cancelReason;
    private String patientName; // optional - dùng khi JOIN với bảng user

    public Appointment() {}

    public Appointment(int id, int patientId, int doctorId, LocalDate date, LocalTime time,
                       String notes, String status, String roomNumber) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.date = date;
        this.time = time;
        this.notes = notes;
        this.status = status;
        this.roomNumber = roomNumber;
    }

    // ====== Getters & Setters ======
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }

    public int getDoctorId() { return doctorId; }
    public void setDoctorId(int doctorId) { this.doctorId = doctorId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getTime() { return time; }
    public void setTime(LocalTime time) { this.time = time; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public String getCancelReason() { return cancelReason; }
    public void setCancelReason(String cancelReason) { this.cancelReason = cancelReason; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    // ✅ Thêm phương thức chuyển LocalDate + LocalTime → LocalDateTime
    public LocalDateTime getAppointmentTime() {
        if (date == null || time == null) return null;
        return LocalDateTime.of(date, time);
    }

    // ✅ Hỗ trợ đặt cả LocalDateTime vào cùng lúc (dành cho DAO)
    public void setAppointmentTime(LocalDateTime dateTime) {
        if (dateTime != null) {
            this.date = dateTime.toLocalDate();
            this.time = dateTime.toLocalTime();
        }
    }
}
