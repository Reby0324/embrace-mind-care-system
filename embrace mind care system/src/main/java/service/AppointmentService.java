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
    Schedule getScheduleById(int scheduleId);
    int reserve(int scheduleId, String patientName, String idNo, LocalDate birthDate, String phone) throws AppException;
    List<AppointmentView> searchAppointments(String idNo, LocalDate birthDate, String phone) throws AppException;
    void cancelAppointment(int appointmentId) throws AppException;
    List<CounselingFee> getCounselingFees();
}
