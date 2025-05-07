package com.vash.db;

import com.vash.lambda.service.DatabaseConnection;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initServiceTable() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            String sql = "CREATE TABLE IF NOT EXISTS services (" +
                    "id SERIAL PRIMARY KEY," +
                    "name VARCHAR(25) NOT NULL," +
                    "type VARCHAR(9) NOT NULL," +
                    "description TEXT NOT NULL" +
                    ")";

            stmt.executeUpdate(sql);
            System.out.println("✅ Tabla 'services' creada o ya existía.");

        } catch (Exception e) {
            System.err.println("❌ Error al inicializar tabla 'services': " + e.getMessage());
            e.printStackTrace();
        }
    }
}
