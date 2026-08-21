package com.selfcheckout.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
            "jdbc:mysql://192.168.10.44:3306/self_checkout";

    private static final String USER = "selfcheckout";
    private static final String PASSWORD = "senha123";

    public static Connection getConnection() {
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(
                    URL,
                    USER,
                    PASSWORD
            );

            System.out.println("Conectado à base de dados!");

        } catch (SQLException e) {
            System.out.println("Erro ao conectar à base de dados!");
            e.printStackTrace();
        }

        return connection;
    }
}