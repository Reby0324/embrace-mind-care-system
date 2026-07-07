package model;

import java.time.LocalDateTime;

public class Appointment {
    private int id;
    private int patientId;
    private int scheduleId;
    private int appointmentNo;
    private String status;
    private LocalDateTime createdAt;

    public int getId() {
        return id;
    }

    public Appointment setId(int id) {
        this.id = id;
        return this;
    }

    public int getPatientId() {
        return patientId;
    }

    public Appointment setPatientId(int patientId) {
        this.patientId = patientId;
        return this;
    }

    public int getScheduleId() {
        return scheduleId;
    }

    public Appointment setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
        return this;
    }

    public int getAppointmentNo() {
        return appointmentNo;
    }

    public Appointment setAppointmentNo(int appointmentNo) {
        this.appointmentNo = appointmentNo;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public Appointment setStatus(String status) {
        this.status = status;
        return this;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Appointment setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }
}
