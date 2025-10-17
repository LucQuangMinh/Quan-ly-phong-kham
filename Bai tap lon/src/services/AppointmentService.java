package services;

import dao.AppointmentDAO;
import dao.NotificationDAO;
import models.Appointment;
import models.Notification;

import java.time.LocalDateTime;

public class AppointmentService {

    private AppointmentDAO appointmentDAO = new AppointmentDAO();
    private NotificationDAO notificationDAO = new NotificationDAO();

    // Create new appointment (called when patient books a doctor)
    public boolean createAppointment(Appointment appointment) {
        boolean success = appointmentDAO.create(appointment);
        if (success) {
            // Send notification to patient
            notificationDAO.create(new Notification(
                    appointment.getPatientId(),
                    "You have successfully booked an appointment with doctor ID " + appointment.getDoctorId(),
                    "Appointment created successfully",
                    "patient"
            ));

            // Send notification to doctor
            notificationDAO.create(new Notification(
                    appointment.getDoctorId(),
                    "A patient has booked an appointment with you at " + appointment.getAppointmentTime(),
                    "New appointment request",
                    "doctor"
            ));
        }
        return success;
    }

    // Doctor confirms the appointment
    public boolean confirmAppointment(int appointmentId, int doctorId) {
        Appointment appointment = appointmentDAO.findById(appointmentId);
        if (appointment == null) {
            System.out.println("Appointment not found.");
            return false;
        }

        // Check for time conflict
        if (appointmentDAO.isDoctorBusy(doctorId, appointment.getAppointmentTime())) {
            System.out.println("Doctor is busy at this time. Cannot confirm appointment.");
            return false;
        }

        // Update status to confirmed
        boolean updated = appointmentDAO.updateStatus(appointmentId, "confirmed");
        if (updated) {
            // Notify both doctor and patient
            notificationDAO.create(new Notification(
                    appointment.getPatientId(),
                    "Your appointment has been confirmed by the doctor.",
                    "Appointment confirmed",
                    "patient"
            ));

            notificationDAO.create(new Notification(
                    doctorId,
                    "You have confirmed the appointment with patient ID " + appointment.getPatientId(),
                    "Appointment confirmed successfully",
                    "doctor"
            ));
        }
        return updated;
    }

    // Doctor rejects the appointment
    public boolean rejectAppointment(int appointmentId, int doctorId, String reason) {
        Appointment appointment = appointmentDAO.findById(appointmentId);
        if (appointment == null) {
            System.out.println("Appointment not found.");
            return false;
        }

        boolean updated = appointmentDAO.updateStatus(appointmentId, "cancelled");
        if (updated) {
            // Notify both doctor and patient
            notificationDAO.create(new Notification(
                    appointment.getPatientId(),
                    "Your appointment was rejected by the doctor. Reason: " + reason,
                    "Appointment rejected",
                    "patient"
            ));

            notificationDAO.create(new Notification(
                    doctorId,
                    "You have rejected the appointment with patient ID " + appointment.getPatientId(),
                    "Appointment cancelled",
                    "doctor"
            ));
        }
        return updated;
    }

    // Mark appointment as completed (doctor side)
    public boolean completeAppointment(int appointmentId, int doctorId) {
        Appointment appointment = appointmentDAO.findById(appointmentId);
        if (appointment == null) {
            System.out.println("Appointment not found.");
            return false;
        }

        boolean updated = appointmentDAO.updateStatus(appointmentId, "completed");
        if (updated) {
            // Notify patient
            notificationDAO.create(new Notification(
                    appointment.getPatientId(),
                    "Your appointment has been completed successfully.",
                    "Appointment completed",
                    "patient"
            ));
        }
        return updated;
    }

    // Patient cancels the appointment
    public boolean cancelAppointment(int appointmentId, int patientId, String reason) {
        Appointment appointment = appointmentDAO.findById(appointmentId);
        if (appointment == null) {
            System.out.println("Appointment not found.");
            return false;
        }

        boolean updated = appointmentDAO.updateStatus(appointmentId, "cancelled");
        if (updated) {
            // Notify doctor
            notificationDAO.create(new Notification(
                    appointment.getDoctorId(),
                    "The appointment was cancelled by the patient. Reason: " + reason,
                    "Appointment cancelled",
                    "doctor"
            ));

            // Notify patient
            notificationDAO.create(new Notification(
                    patientId,
                    "You have cancelled your appointment successfully.",
                    "Appointment cancelled",
                    "patient"
            ));
        }
        return updated;
    }
}
