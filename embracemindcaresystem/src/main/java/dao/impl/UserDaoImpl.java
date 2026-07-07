package dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import dao.UserDao;
import model.User;
import util.DbConnection;

public class UserDaoImpl implements UserDao {

    @Override
    public User login(String username, String password) {
        String sql = "SELECT id, username, role, display_name, professional_id "
                + "FROM users "
                + "WHERE username = ? AND password = ? AND active = 1";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Integer professionalId = null;
                    int value = rs.getInt("professional_id");
                    if (!rs.wasNull()) {
                        professionalId = value;
                    }

                    return new User()
                            .setId(rs.getInt("id"))
                            .setUsername(rs.getString("username"))
                            .setRole(rs.getString("role"))
                            .setDisplayName(rs.getString("display_name"))
                            .setProfessionalId(professionalId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
