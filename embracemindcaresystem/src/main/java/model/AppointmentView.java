package model;

import java.time.LocalDate;
import java.time.LocalTime;

public class AppointmentView {
    private int appointmentId;
    private int scheduleId;
    private String patientName;
    private String idNo;
    private String phone;
    private LocalDate appointmentDate;
    private String session;
    private String serviceTypeName;
    private String appointmentType;
    private String professionalName;
    private String role;
    private String location;
    private LocalTime startTime;
    private LocalTime endTime;
    private int appointmentNo;
    private String status;

    public int getAppointmentId() { return appointmentId; }
    public AppointmentView setAppointmentId(int appointmentId) { this.appointmentId = appointmentId; return this; }

    public int getScheduleId() { return scheduleId; }
    public AppointmentView setScheduleId(int scheduleId) { this.scheduleId = scheduleId; return this; }

    public String getPatientName() { return patientName; }
    public AppointmentView setPatientName(String patientName) { this.patientName = patientName; return this; }

    public String getIdNo() { return idNo; }
    public AppointmentView setIdNo(String idNo) { this.idNo = idNo; return this; }

    public String getPhone() { return phone; }
    public AppointmentView setPhone(String phone) { this.phone = phone; return this; }

    public LocalDate getAppointmentDate() { return appointmentDate; }
    public AppointmentView setAppointmentDate(LocalDate appointmentDate) { this.appointmentDate = appointmentDate; return this; }

    public String getSession() { return session; }
    public AppointmentView setSession(String session) { this.session = session; return this; }

    public String getServiceTypeName() { return serviceTypeName; }
    public AppointmentView setServiceTypeName(String serviceTypeName) { this.serviceTypeName = serviceTypeName; return this; }

    public String getAppointmentType() { return appointmentType; }
    public AppointmentView setAppointmentType(String appointmentType) { this.appointmentType = appointmentType; return this; }

    public String getProfessionalName() { return professionalName; }
    public AppointmentView setProfessionalName(String professionalName) { this.professionalName = professionalName; return this; }

    public String getRole() { return role; }
    public AppointmentView setRole(String role) { this.role = role; return this; }

    public String getLocation() { return location; }
    public AppointmentView setLocation(String location) { this.location = location; return this; }

    public LocalTime getStartTime() { return startTime; }
    public AppointmentView setStartTime(LocalTime startTime) { this.startTime = startTime; return this; }

    public LocalTime getEndTime() { return endTime; }
    public AppointmentView setEndTime(LocalTime endTime) { this.endTime = endTime; return this; }

    public int getAppointmentNo() { return appointmentNo; }
    public AppointmentView setAppointmentNo(int appointmentNo) { this.appointmentNo = appointmentNo; return this; }

    public String getStatus() { return status; }
    public AppointmentView setStatus(String status) { this.status = status; return this; }
}
