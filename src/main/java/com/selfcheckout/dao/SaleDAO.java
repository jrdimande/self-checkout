package com.selfcheckout.dao;

import com.selfcheckout.model.Sale;
import com.selfcheckout.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SaleDAO {

    public int save(Sale sale) {

        String sql = "INSERT INTO sale(customer_uuid, total) VALUES(?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     sql,
                     PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, sale.getCustomer_uuid());
            ps.setDouble(2, sale.getTotal());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {

                if (rs.next()) {
                    int generatedID = rs.getInt(1);
                    sale.setId(generatedID);

                    return generatedID;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }
}
