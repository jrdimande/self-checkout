package com.selfcheckout.dao;

import com.selfcheckout.model.Customer;
import com.selfcheckout.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerDAO {
    public void save(Customer customer){
        String sql = "INSERT INTO customer(uuid, name) VAlUES(?, ?)";

        try(Connection connection = DBConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1, customer.getUuid());
            ps.setString(2, customer.getName());
            ps.executeUpdate();

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public Customer findByUuid(String uuid){
        String sql = "SELECT uuid, name, balance FROM  customer WHERE uuid = ?";

        try(Connection connection = DBConnection.getConnection();
        PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1, uuid);

            ResultSet rs = ps.executeQuery();

            if (rs.next()){
                return new Customer(rs.getString("uuid"),
                        rs.getString("name"), rs.getDouble("balance"));
            }

        }catch (SQLException e){
            e.printStackTrace();
        }

        return null;

    }
}
