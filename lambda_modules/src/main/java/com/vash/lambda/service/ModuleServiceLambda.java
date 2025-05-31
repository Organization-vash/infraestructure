package com.vash.lambda.service;

import com.vash.lambda.model.ModuleDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ModuleServiceLambda {

    public List<ModuleDTO> getAll() {
        List<ModuleDTO> list = new ArrayList<>();
        String sql = "SELECT id, status FROM modules";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ModuleDTO dto = new ModuleDTO();
                dto.setId(rs.getInt("id"));
                dto.setStatus(rs.getString("status"));
                list.add(dto);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener módulos", e);
        }

        return list;
    }

    public ModuleDTO getById(int id) {
        String sql = "SELECT id, status FROM modules WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                ModuleDTO dto = new ModuleDTO();
                dto.setId(rs.getInt("id"));
                dto.setStatus(rs.getString("status"));
                return dto;
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar módulo", e);
        }
    }

    public ModuleDTO create(ModuleDTO dto) {
        String sql = "INSERT INTO modules (id, status) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, dto.getId());
            stmt.setString(2, "INACTIVE");

            stmt.executeUpdate();
            dto.setStatus("INACTIVE");
            return dto;

        } catch (SQLException e) {
            throw new RuntimeException("Error al crear módulo", e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM modules WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("No se encontró el módulo con ID " + id);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar módulo", e);
        }
    }
}
