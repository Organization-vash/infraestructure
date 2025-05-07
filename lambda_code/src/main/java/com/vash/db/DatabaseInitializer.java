package com.vash.db;

import com.vash.lambda.service.DatabaseConnection;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initTicketCodeTable() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            String sql = "CREATE TABLE IF NOT EXISTS tickets_code (" +
                    "id SERIAL PRIMARY KEY," +
                    "code VARCHAR(10) NOT NULL," +
                    "created TIMESTAMP NOT NULL," +
                    "service_id INT REFERENCES services(id)," +
                    "customer_id INT," +
                    "attention_id INT UNIQUE" +
                    ")";

            stmt.executeUpdate(sql);
            System.out.println("✅ Tabla 'tickets_code' creada o ya existía.");

        } catch (Exception e) {
            System.err.println("❌ Error al inicializar tabla tickets_code: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
