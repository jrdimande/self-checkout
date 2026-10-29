package com.selfcheckout.dao;

import com.selfcheckout.model.Product;
import com.selfcheckout.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {


    public Product findByUuid(String uuid) {

        String sql = """
        SELECT uuid, name, price
        FROM product
        WHERE UPPER(TRIM(uuid)) = UPPER(TRIM(?))
        """;

        System.out.println("========== PRODUCT DAO ==========");
        System.out.println("UID recebido: [" + uuid + "]");
        System.out.println("Comprimento do UID: " + uuid.length());

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            System.out.println(
                    "Base de dados: "
                            + connection.getCatalog()
            );

            System.out.println(
                    "Servidor MySQL: "
                            + connection.getMetaData().getURL()
            );

            ps.setString(1, uuid);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    Product product = new Product(
                            rs.getString("uuid"),
                            rs.getString("name"),
                            rs.getDouble("price")
                    );

                    System.out.println("PRODUTO ENCONTRADO!");
                    System.out.println("UUID na BD: [" + product.getUuid() + "]");
                    System.out.println("Nome: " + product.getName());
                    System.out.println("Preço: " + product.getPrice());
                    System.out.println("=================================");

                    return product;
                }

                System.out.println(
                        "A consulta executou, mas não encontrou o produto."
                );
            }

        } catch (SQLException e) {

            System.err.println("ERRO SQL AO PROCURAR O PRODUTO:");
            e.printStackTrace();
        }

        System.out.println("=================================");
        return null;
    }

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

    public void save(Product product) {
        String sql = "INSERT INTO product(uuid, name, price) VALUES (?, ?, ?)";

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

    public void update(Product product) {
        String sql = "UPDATE product SET name = ?, price = ? WHERE uuid = ?";

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
}