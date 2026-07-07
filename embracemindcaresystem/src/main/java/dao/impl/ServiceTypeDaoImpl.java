package dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import dao.ServiceTypeDao;
import model.ServiceType;
import util.DbConnection;

public class ServiceTypeDaoImpl implements ServiceTypeDao {

    @Override
    public List<ServiceType> findAll() {
        List<ServiceType> list = new ArrayList<>();
        String sql = "SELECT id, main_category, name, description FROM service_types ORDER BY id";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new ServiceType()
                        .setId(rs.getInt("id"))
                        .setMainCategory(rs.getString("main_category"))
                        .setName(rs.getString("name"))
                        .setDescription(rs.getString("description")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void insert(ServiceType serviceType) throws Exception {
        String sql = "INSERT INTO service_types(main_category, name, description) VALUES (?, ?, ?)";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, serviceType.getMainCategory());
            ps.setString(2, serviceType.getName());
            ps.setString(3, serviceType.getDescription());
            ps.executeUpdate();
        }
    }
}
