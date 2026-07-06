package dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import dao.AppointmentDao;
import model.AppointmentView;
import util.DbConnection;

public class AppointmentDaoImpl implements AppointmentDao {

    @Override
    public int insert(int patientId, int scheduleId, int appointmentNo, Connection conn) throws Exception {
        String sql = "INSERT INTO appointments(patient_id, schedule_id, appointment_no, status, created_at) "
                + "VALUES (?, ?, ?, '預約完成', NOW())";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, patientId);
            ps.setInt(2, scheduleId);
            ps.setInt(3, appointmentNo);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new Exception("新增預約紀錄失敗");
    }

    @Override
    public List<AppointmentView> search(String idNo, LocalDate birthDate, String phone) {
        List<AppointmentView> list = new ArrayList<>();
        String sql = baseSql()
                + "WHERE pt.id_no = ? AND pt.birth_date = ? AND pt.phone = ? "
                + "ORDER BY s.appointment_date DESC, s.start_time DESC";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idNo);
            ps.setDate(2, java.sql.Date.valueOf(birthDate));
            ps.setString(3, phone);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(toView(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public AppointmentView findById(int appointmentId, Connection conn) throws Exception {
        String sql = baseSql() + "WHERE a.id = ? FOR UPDATE";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, appointmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return toView(rs);
                }
            }
        }
        return null;
    }

    @Override
    public void cancel(int appointmentId, Connection conn) throws Exception {
        String sql = "UPDATE appointments SET status = '已取消', cancelled_at = NOW() "
                + "WHERE id = ? AND status = '預約完成'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, appointmentId);
            ps.executeUpdate();
        }
    }

    private String baseSql() {
        return "SELECT a.id AS appointment_id, a.schedule_id, a.appointment_no, a.status, "
                + "pt.name AS patient_name, pt.id_no, pt.phone, "
                + "s.appointment_date, s.session, s.appointment_type, s.location, s.start_time, s.end_time, "
                + "st.name AS service_type_name, p.name AS professional_name, p.role "
                + "FROM appointments a "
                + "JOIN patients pt ON a.patient_id = pt.id "
                + "JOIN schedules s ON a.schedule_id = s.id "
                + "JOIN professionals p ON s.professional_id = p.id "
                + "JOIN service_types st ON p.service_type_id = st.id ";
    }

    private AppointmentView toView(ResultSet rs) throws Exception {
        return new AppointmentView()
                .setAppointmentId(rs.getInt("appointment_id"))
                .setScheduleId(rs.getInt("schedule_id"))
                .setPatientName(rs.getString("patient_name"))
                .setIdNo(rs.getString("id_no"))
                .setPhone(rs.getString("phone"))
                .setAppointmentDate(rs.getDate("appointment_date").toLocalDate())
                .setSession(rs.getString("session"))
                .setServiceTypeName(rs.getString("service_type_name"))
                .setAppointmentType(rs.getString("appointment_type"))
                .setProfessionalName(rs.getString("professional_name"))
                .setRole(rs.getString("role"))
                .setLocation(rs.getString("location"))
                .setStartTime(rs.getTime("start_time").toLocalTime())
                .setEndTime(rs.getTime("end_time").toLocalTime())
                .setAppointmentNo(rs.getInt("appointment_no"))
                .setStatus(rs.getString("status"));
    }
}
