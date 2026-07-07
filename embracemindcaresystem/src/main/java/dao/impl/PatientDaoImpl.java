package dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;

import dao.PatientDao;
import model.Patient;

public class PatientDaoImpl implements PatientDao {

    @Override
    public int insert(Patient patient, Connection conn) throws Exception {
        String sql = "INSERT INTO patients(name, id_no, birth_date, phone) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, patient.getName());
            ps.setString(2, patient.getIdNo());
            ps.setDate(3, java.sql.Date.valueOf(patient.getBirthDate()));
            ps.setString(4, patient.getPhone());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new Exception("新增預約者資料失敗");
    }

    @Override
    public Patient findByIdentity(String idNo, LocalDate birthDate, String phone, Connection conn) throws Exception {
        String sql = "SELECT id, name, id_no, birth_date, phone FROM patients "
                + "WHERE id_no = ? AND birth_date = ? AND phone = ? ORDER BY id DESC LIMIT 1";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idNo);
            ps.setDate(2, java.sql.Date.valueOf(birthDate));
            ps.setString(3, phone);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Patient()
                            .setId(rs.getInt("id"))
                            .setName(rs.getString("name"))
                            .setIdNo(rs.getString("id_no"))
                            .setBirthDate(rs.getDate("birth_date").toLocalDate())
                            .setPhone(rs.getString("phone"));
                }
            }
        }
        return null;
    }
}
