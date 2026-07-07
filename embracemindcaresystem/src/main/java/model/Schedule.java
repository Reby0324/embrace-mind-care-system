package model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Schedule {
    private int id;
    private int professionalId;
    private String professionalCode;
    private String professionalName;
    private String role;
    private int serviceTypeId;
    private String serviceTypeName;
    private String mainCategory;
    private LocalDate appointmentDate;
    private String session;
    private String appointmentType;
    private String location;
    private LocalTime startTime;
    private LocalTime endTime;
    private int quota;
    private int bookedCount;
    private boolean active;

    public int getId() {
        return id;
    }

    public Schedule setId(int id) {
        this.id = id;
        return this;
    }

    public int getProfessionalId() {
        return professionalId;
    }

    public Schedule setProfessionalId(int professionalId) {
        this.professionalId = professionalId;
        return this;
    }

    public String getProfessionalCode() {
        return professionalCode;
    }

    public Schedule setProfessionalCode(String professionalCode) {
        this.professionalCode = professionalCode;
        return this;
    }

    public String getProfessionalName() {
        return professionalName;
    }

    public Schedule setProfessionalName(String professionalName) {
        this.professionalName = professionalName;
        return this;
    }

    public String getRole() {
        return role;
    }

    public Schedule setRole(String role) {
        this.role = role;
        return this;
    }

    public int getServiceTypeId() {
        return serviceTypeId;
    }

    public Schedule setServiceTypeId(int serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
        return this;
    }

    public String getServiceTypeName() {
        return serviceTypeName;
    }

    public Schedule setServiceTypeName(String serviceTypeName) {
        this.serviceTypeName = serviceTypeName;
        return this;
    }

    public String getMainCategory() {
        return mainCategory;
    }

    public Schedule setMainCategory(String mainCategory) {
        this.mainCategory = mainCategory;
        return this;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public Schedule setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
        return this;
    }

    public String getSession() {
        return session;
    }

    public Schedule setSession(String session) {
        this.session = session;
        return this;
    }

    public String getAppointmentType() {
        return appointmentType;
    }

    public Schedule setAppointmentType(String appointmentType) {
        this.appointmentType = appointmentType;
        return this;
    }

    public String getLocation() {
        return location;
    }

    public Schedule setLocation(String location) {
        this.location = location;
        return this;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public Schedule setStartTime(LocalTime startTime) {
        this.startTime = startTime;
        return this;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public Schedule setEndTime(LocalTime endTime) {
        this.endTime = endTime;
        return this;
    }

    public int getQuota() {
        return quota;
    }

    public Schedule setQuota(int quota) {
        this.quota = quota;
        return this;
    }

    public int getBookedCount() {
        return bookedCount;
    }

    public Schedule setBookedCount(int bookedCount) {
        this.bookedCount = bookedCount;
        return this;
    }

    public boolean isActive() {
        return active;
    }

    public Schedule setActive(boolean active) {
        this.active = active;
        return this;
    }

    public int getRemainCount() {
        return Math.max(0, quota - bookedCount);
    }

    public String getDisplayProfessional() {
        return professionalName + "（" + role + "）";
    }
}
