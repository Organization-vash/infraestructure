package com.vash.db;

import com.vash.lambda.service.DatabaseConnection;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initAgencyTable() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            String sql = "CREATE TABLE IF NOT EXISTS agencies (" +
                         "id SERIAL PRIMARY KEY," +
                         "city VARCHAR(100) NOT NULL," +
                         "seat VARCHAR(100) NOT NULL," +
                         "created_at TIMESTAMP NOT NULL" +
                         ")";

            stmt.executeUpdate(sql);
            System.out.println("✅ Tabla 'agencies' creada o ya existía.");

        } catch (Exception e) {
            System.err.println("❌ Error al inicializar tabla agencies: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
