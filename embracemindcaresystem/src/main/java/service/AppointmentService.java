package service;

import java.time.LocalDate;
import java.util.List;

import exception.AppException;
import model.AppointmentView;
import model.CounselingFee;
import model.Professional;
import model.Schedule;
import model.ServiceType;

public interface AppointmentService {
    List<ServiceType> getServiceTypes();
    List<Professional> getProfessionals();
    List<Professional> getProfessionalsByRole(String role);
    List<Schedule> getSchedulesByDate(LocalDate date);
    List<Schedule> getSchedulesByServiceType(int serviceTypeId);
    List<Schedule> getSchedulesByProfessional(int professionalId);
    List<Schedule> getAllSchedulesForAdmin();
    Schedule getScheduleById(int scheduleId);
    int reserve(int scheduleId, String patientName, String idNo, LocalDate birthDate, String phone) throws AppException;
    List<AppointmentView> searchAppointments(String idNo, LocalDate birthDate, String phone) throws AppException;
    List<AppointmentView> getAllAppointments();
    List<AppointmentView> getAppointmentsByProfessional(int professionalId) throws AppException;
    void cancelAppointment(int appointmentId) throws AppException;
    void addServiceType(String mainCategory, String name, String description) throws AppException;
    void updateSchedule(Schedule schedule) throws AppException;
    List<CounselingFee> getCounselingFees();
}
