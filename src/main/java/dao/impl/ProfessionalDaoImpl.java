package dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import dao.ProfessionalDao;
import model.Professional;
import util.DbConnection;

public class ProfessionalDaoImpl implements ProfessionalDao {

    @Override
    public List<Professional> findAll() {
        return query("", null);
    }

    @Override
    public List<Professional> findByRole(String role) {
        return query("WHERE p.role = ? ", role);
    }

    @Override
    public List<Professional> findByServiceType(int serviceTypeId) {
        return query("WHERE p.service_type_id = ? ", serviceTypeId);
    }

    private List<Professional> query(String where, Object param) {
        List<Professional> list = new ArrayList<>();
        String sql = "SELECT p.id, p.code, p.name, p.role, p.service_type_id, st.name AS service_type_name "
                + "FROM professionals p "
                + "JOIN service_types st ON p.service_type_id = st.id "
                + where
                + "ORDER BY p.role, p.id";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (param instanceof String) {
                ps.setString(1, (String) param);
            } else if (param instanceof Integer) {
                ps.setInt(1, (Integer) param);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Professional()
                            .setId(rs.getInt("id"))
                            .setCode(rs.getString("code"))
                            .setName(rs.getString("name"))
                            .setRole(rs.getString("role"))
                            .setServiceTypeId(rs.getInt("service_type_id"))
                            .setServiceTypeName(rs.getString("service_type_name")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
