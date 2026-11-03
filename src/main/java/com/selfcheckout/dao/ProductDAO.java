package com.selfcheckout.dao;

import com.selfcheckout.model.Product;
import com.selfcheckout.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    // Procurar produto pelo UID
    public Product findByUuid(String uuid) {

        String sql = """
            SELECT uuid, name, price
            FROM product
            WHERE UPPER(TRIM(uuid)) = UPPER(TRIM(?))
            """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, uuid.trim());

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return new Product(
                            rs.getString("uuid"),
                            rs.getString("name"),
                            rs.getDouble("price")
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    // Listar todos os produtos
    public List<Product> findAll() {

        List<Product> products = new ArrayList<>();

        String sql = "SELECT uuid, name, price FROM product";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                products.add(new Product(
                        rs.getString("uuid"),
                        rs.getString("name"),
                        rs.getDouble("price")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }


    // Registar produto
    public void save(Product product) {

        String sql = """
            INSERT INTO product(uuid, name, price)
            VALUES (?, ?, ?)
            """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, product.getUuid());
            ps.setString(2, product.getName());
            ps.setDouble(3, product.getPrice());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Actualizar produto
    public void update(Product product) {

        String sql = """
            UPDATE product
            SET name = ?, price = ?
            WHERE uuid = ?
            """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, product.getName());
            ps.setDouble(2, product.getPrice());
            ps.setString(3, product.getUuid());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Eliminar produto
    public void delete(String uuid) {

        String sql = "DELETE FROM product WHERE uuid = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, uuid);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Consultar stock disponível
    public int getStock(String uuid) {

        String sql = """
            SELECT stock
            FROM product
            WHERE uuid = ?
            """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, uuid);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt("stock");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }


    // Diminuir stock após uma compra
    public boolean decreaseStock(String uuid, int quantity) {

        String sql = """
            UPDATE product
            SET stock = stock - ?
            WHERE uuid = ?
            AND stock >= ?
            """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, quantity);
            ps.setString(2, uuid);
            ps.setInt(3, quantity);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}