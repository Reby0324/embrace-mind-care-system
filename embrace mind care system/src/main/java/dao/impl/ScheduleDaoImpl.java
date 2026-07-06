package dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import dao.ScheduleDao;
import model.Schedule;
import util.DbConnection;

public class ScheduleDaoImpl implements ScheduleDao {

    private static final String BASE_SELECT =
            "SELECT s.id, s.professional_id, s.appointment_date, s.session, "
                    + "s.appointment_type, s.location, s.start_time, s.end_time, "
                    + "s.quota, s.booked_count, s.active, "
                    + "p.code AS professional_code, p.name AS professional_name, p.role, "
                    + "st.id AS service_type_id, st.name AS service_type_name, st.main_category "
                    + "FROM schedules s "
                    + "JOIN professionals p ON s.professional_id = p.id "
                    + "JOIN service_types st ON p.service_type_id = st.id ";

    @Override
    public List<Schedule> findByDate(LocalDate date) {
        List<Schedule> list = new ArrayList<>();
        String sql = BASE_SELECT
                + "WHERE s.active = 1 AND s.appointment_date = ? "
                + "ORDER BY FIELD(s.session, '上午', '下午', '夜診'), s.start_time, st.id";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(toSchedule(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Schedule> findByServiceType(int serviceTypeId) {
        List<Schedule> list = new ArrayList<>();
        String sql = BASE_SELECT
                + "WHERE s.active = 1 AND st.id = ? "
                + "ORDER BY s.appointment_date, FIELD(s.session, '上午', '下午', '夜診'), s.start_time";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, serviceTypeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(toSchedule(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Schedule> findByProfessional(int professionalId) {
        List<Schedule> list = new ArrayList<>();
        String sql = BASE_SELECT
                + "WHERE s.active = 1 AND p.id = ? "
                + "ORDER BY s.appointment_date, FIELD(s.session, '上午', '下午', '夜診'), s.start_time";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, professionalId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(toSchedule(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Schedule findById(int scheduleId) {
        String sql = BASE_SELECT + "WHERE s.id = ?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, scheduleId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return toSchedule(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Schedule findByIdForUpdate(int scheduleId, Connection conn) throws Exception {
        String sql = BASE_SELECT + "WHERE s.id = ? FOR UPDATE";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, scheduleId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return toSchedule(rs);
                }
            }
        }
        return null;
    }

    @Override
    public void increaseBookedCount(int scheduleId, Connection conn) throws Exception {
        String sql = "UPDATE schedules SET booked_count = booked_count + 1 WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, scheduleId);
            ps.executeUpdate();
        }
    }

    @Override
    public void decreaseBookedCount(int scheduleId, Connection conn) throws Exception {
        String sql = "UPDATE schedules SET booked_count = GREATEST(booked_count - 1, 0) WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, scheduleId);
            ps.executeUpdate();
        }
    }

    private Schedule toSchedule(ResultSet rs) throws Exception {
        return new Schedule()
                .setId(rs.getInt("id"))
                .setProfessionalId(rs.getInt("professional_id"))
                .setAppointmentDate(rs.getDate("appointment_date").toLocalDate())
                .setSession(rs.getString("session"))
                .setAppointmentType(rs.getString("appointment_type"))
                .setLocation(rs.getString("location"))
                .setStartTime(rs.getTime("start_time").toLocalTime())
                .setEndTime(rs.getTime("end_time").toLocalTime())
                .setQuota(rs.getInt("quota"))
                .setBookedCount(rs.getInt("booked_count"))
                .setActive(rs.getInt("active") == 1)
                .setProfessionalCode(rs.getString("professional_code"))
                .setProfessionalName(rs.getString("professional_name"))
                .setRole(rs.getString("role"))
                .setServiceTypeId(rs.getInt("service_type_id"))
                .setServiceTypeName(rs.getString("service_type_name"))
                .setMainCategory(rs.getString("main_category"));
    }
}
