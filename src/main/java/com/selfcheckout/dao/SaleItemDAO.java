package com.selfcheckout.dao;

import com.selfcheckout.model.SaleItem;
import com.selfcheckout.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SaleItemDAO {
    public void save(SaleItem saleItem){
        String sql = "INSERT INTO sale_item (sale_id, product_uuid, quantity, unit_price, subtotal) VALUES(?, ?, ?, ?, ?)";
        try(Connection connection = DBConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)){

            ps.setInt(1, saleItem.getSale_id());
            ps.setString(2, saleItem.getProduct().getUuid());
            ps.setInt(3, saleItem.getQuantity());
            ps.setDouble(4, saleItem.getUnitPrice());
            ps.setDouble(5, saleItem.getSubtotal());
            ps.executeUpdate();

        }catch (SQLException e){
            e.printStackTrace();
        }

    }
}
