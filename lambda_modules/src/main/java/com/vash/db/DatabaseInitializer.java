package com.vash.db;

import com.vash.lambda.service.DatabaseConnection;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initModulesTable() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
    
            String sql = "CREATE TABLE IF NOT EXISTS modules (" +
                         "id INT PRIMARY KEY," +
                         "status VARCHAR(20) NOT NULL" +
                         ")";
            stmt.executeUpdate(sql);
            System.out.println("✅ Tabla 'modules' creada o ya existía.");
    
        } catch (Exception e) {
            System.err.println("❌ Error al inicializar tabla modules: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
}
