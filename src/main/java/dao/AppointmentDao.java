package dao;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;
import model.AppointmentView;

public interface AppointmentDao {
    int insert(int patientId, int scheduleId, int appointmentNo, Connection conn) throws Exception;
    List<AppointmentView> search(String idNo, LocalDate birthDate, String phone);
    List<AppointmentView> findAll();
    List<AppointmentView> findByProfessional(int professionalId);
    AppointmentView findById(int appointmentId, Connection conn) throws Exception;
    void cancel(int appointmentId, Connection conn) throws Exception;
}
