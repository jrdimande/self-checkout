package com.selfcheckout.dao;

import com.selfcheckout.model.Payment;
import com.selfcheckout.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PaymentDAO {
    public void save(Payment payment){
        String sql = "INSERT INTO payment(sale_id, amount, payment_method) VALUES(?, ?, ?)";

        try(Connection connection = DBConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setInt(1, payment.getSale_id());
            ps.setDouble(2, payment.getAmount());
            ps.setString(3, payment.getPaymentMethod());
            ps.executeUpdate();

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

}
