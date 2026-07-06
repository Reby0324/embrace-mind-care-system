package dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import dao.CounselingFeeDao;
import model.CounselingFee;
import util.DbConnection;

public class CounselingFeeDaoImpl implements CounselingFeeDao {

    @Override
    public List<CounselingFee> findAll() {
        List<CounselingFee> list = new ArrayList<>();

        String sql = "SELECT id, service_name, duration_minutes, fee_min, fee_max "
                   + "FROM counseling_fees "
                   + "ORDER BY id";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                CounselingFee fee = new CounselingFee();

                fee.setId(rs.getInt("id"));
                fee.setServiceName(rs.getString("service_name"));
                fee.setDurationMinutes(rs.getInt("duration_minutes"));
                fee.setFeeMin(rs.getInt("fee_min"));
                fee.setFeeMax(rs.getInt("fee_max"));

                list.add(fee);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}