package dao;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;
import model.Schedule;

public interface ScheduleDao {
    List<Schedule> findByDate(LocalDate date);
    List<Schedule> findByServiceType(int serviceTypeId);
    List<Schedule> findByProfessional(int professionalId);
    Schedule findById(int scheduleId);
    Schedule findByIdForUpdate(int scheduleId, Connection conn) throws Exception;
    void increaseBookedCount(int scheduleId, Connection conn) throws Exception;
    void decreaseBookedCount(int scheduleId, Connection conn) throws Exception;
}
