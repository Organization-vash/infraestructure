package com.vash.db;

import com.vash.lambda.service.DatabaseConnection;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initUserTable() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            String sql = "CREATE TABLE IF NOT EXISTS users (" +
                    "id SERIAL PRIMARY KEY," +
                    "document_type VARCHAR(10)," +
                    "number_doc INT," +
                    "name VARCHAR(100)," +
                    "last_name VARCHAR(100)," +
                    "module_id INT," +
                    "email VARCHAR(100)," +
                    "password VARCHAR(100)," +
                    "role VARCHAR(50)," +
                    "username VARCHAR(50))";

            stmt.executeUpdate(sql);
            System.out.println("✅ Tabla 'users' creada o ya existía.");

        } catch (Exception e) {
            System.err.println("❌ Error al inicializar tabla: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
