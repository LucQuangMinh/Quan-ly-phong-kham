package services;

import dao.AppointmentDAO;
import dao.NotificationDAO;
import models.Appointment;
import models.Notification;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

public class AppointmentService {

    private AppointmentDAO appointmentDAO = new AppointmentDAO();
    private NotificationDAO notificationDAO = new NotificationDAO();

    // 🔹 Tạo lịch hẹn mới (bệnh nhân đặt lịch)
    public boolean createAppointment(Appointment appointment) {
        LocalDateTime appointmentDateTime = LocalDateTime.of(
                appointment.getDate(),
                appointment.getTime()
        );

        boolean success = appointmentDAO.create(appointment);
        if (success) {
            // ✅ Gửi thông báo cho bệnh nhân: Order (PatientId, Title, Message, userType)
            notificationDAO.create(new Notification(
                    appointment.getPatientId(),
                    "Đặt lịch thành công",
                    "Bạn đã đặt lịch hẹn thành công với bác sĩ ID " + appointment.getDoctorId() + " vào " + appointmentDateTime,
                    "patient"
            ));

            // ✅ Gửi thông báo cho bác sĩ: Order (DoctorId, Title, Message, userType)
            notificationDAO.create(new Notification(
                    appointment.getDoctorId(),
                    "Yêu cầu lịch hẹn mới",
                    "Bệnh nhân ID " + appointment.getPatientId() + " đã đặt lịch hẹn với bạn vào " + appointmentDateTime,
                    "doctor"
            ));
        }
        return success;
    }

    // 🔹 Bác sĩ xác nhận lịch hẹn
    public boolean confirmAppointment(int appointmentId, int doctorId) {
        Appointment appointment = appointmentDAO.findById(appointmentId);
        if (appointment == null) {
            System.out.println("Appointment not found.");
            return false;
        }
        
        LocalDate date = appointment.getDate();
        LocalTime time = appointment.getTime();
        
        if (date == null || time == null) {
            System.out.println("Appointment time is invalid.");
            return false;
        }

        if (appointmentDAO.isDoctorBusy(doctorId, date, time)) {
            System.out.println("Doctor is busy at this time. Cannot confirm appointment.");
            return false;
        }

        boolean updated = appointmentDAO.updateStatus(appointmentId, "confirmed");
        if (updated) {
            // ✅ Gửi thông báo xác nhận cho bệnh nhân
            notificationDAO.create(new Notification(
                    appointment.getPatientId(),
                    "Xác nhận lịch hẹn",
                    "Lịch hẹn của bạn đã được bác sĩ xác nhận vào " + date + " lúc " + time,
                    "patient"
            ));

            // ✅ Gửi thông báo cho bác sĩ
            notificationDAO.create(new Notification(
                    doctorId,
                    "Xác nhận thành công",
                    "Bạn đã xác nhận lịch hẹn với bệnh nhân ID " + appointment.getPatientId(),
                    "doctor"
            ));
        }
        return updated;
    }

    // 🔹 Bác sĩ từ chối lịch hẹn
    public boolean rejectAppointment(int appointmentId, int doctorId, String reason) {
        Appointment appointment = appointmentDAO.findById(appointmentId);
        if (appointment == null) {
            System.out.println("Appointment not found.");
            return false;
        }

        boolean updated = appointmentDAO.updateStatus(appointmentId, "cancelled");
        if (updated) {
            // ✅ Gửi thông báo từ chối cho bệnh nhân
            notificationDAO.create(new Notification(
                    appointment.getPatientId(),
                    "Từ chối lịch hẹn",
                    "Lịch hẹn của bạn bị bác sĩ từ chối. Lý do: " + reason,
                    "patient"
            ));

            // ✅ Gửi thông báo cho bác sĩ
            notificationDAO.create(new Notification(
                    doctorId,
                    "Từ chối thành công",
                    "Bạn đã từ chối lịch hẹn với bệnh nhân ID " + appointment.getPatientId(),
                    "doctor"
            ));
        }
        return updated;
    }

    // 🔹 Bác sĩ hoàn thành lịch hẹn
    public boolean completeAppointment(int appointmentId, int doctorId) {
        Appointment appointment = appointmentDAO.findById(appointmentId);
        if (appointment == null) {
            System.out.println("Appointment not found.");
            return false;
        }

        boolean updated = appointmentDAO.updateStatus(appointmentId, "completed");
        if (updated) {
            // ✅ Gửi thông báo hoàn thành cho bệnh nhân
            notificationDAO.create(new Notification(
                    appointment.getPatientId(),
                    "Hoàn thành lịch hẹn",
                    "Lịch hẹn của bạn đã hoàn thành thành công.",
                    "patient"
            ));
        }
        return updated;
    }

    // 🔹 Bệnh nhân huỷ lịch hẹn
    public boolean cancelAppointment(int appointmentId, int patientId, String reason) {
        Appointment appointment = appointmentDAO.findById(appointmentId);
        if (appointment == null) {
            System.out.println("Appointment not found.");
            return false;
        }

        boolean updated = appointmentDAO.updateStatus(appointmentId, "cancelled");
        if (updated) {
            // ✅ Gửi thông báo hủy cho bác sĩ
            notificationDAO.create(new Notification(
                    appointment.getDoctorId(),
                    "Huỷ lịch hẹn",
                    "Bệnh nhân đã huỷ lịch hẹn. Lý do: " + reason,
                    "doctor"
            ));

            // ✅ Gửi thông báo hủy cho bệnh nhân
            notificationDAO.create(new Notification(
                    patientId,
                    "Huỷ lịch hẹn",
                    "Bạn đã huỷ lịch hẹn thành công.",
                    "patient"
            ));
        }
        return updated;
    }
}