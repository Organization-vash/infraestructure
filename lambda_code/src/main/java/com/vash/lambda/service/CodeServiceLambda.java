package com.vash.lambda.service;

import com.vash.lambda.model.CodeDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CodeServiceLambda {

    public List<CodeDTO> getAll() {
        List<CodeDTO> result = new ArrayList<>();
        String sql = """
            SELECT tc.id, tc.code, tc.created, tc.customer_id, s.name AS service_name
            FROM tickets_code tc
            JOIN services s ON tc.service_id = s.id
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                CodeDTO dto = new CodeDTO();
                dto.setId(rs.getInt("id"));
                dto.setCode(rs.getString("code"));
                dto.setCustomerName("ID: " + rs.getInt("customer_id"));
                dto.setServiceName(rs.getString("service_name"));
                dto.setCreated(rs.getTimestamp("created").toLocalDateTime().toString());
                result.add(dto);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener tickets", e);
        }

        return result;
    }

    public CodeDTO create(CodeDTO dto) {
        String sql = """
            INSERT INTO tickets_code (code, created, service_id, customer_id)
            VALUES (?, ?, ?, ?)
            RETURNING id
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, dto.getCode());
            stmt.setTimestamp(2, Timestamp.valueOf(dto.getCreated()));
            stmt.setInt(3, getServiceIdByName(conn, dto.getServiceName()));
            stmt.setInt(4, Integer.parseInt(dto.getCustomerName()));  // customerName en realidad es el ID

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                dto.setId(rs.getInt("id"));
            }

            return dto;

        } catch (SQLException e) {
            throw new RuntimeException("Error al crear ticket", e);
        }
    }

    public CodeDTO update(Integer id, CodeDTO dto) {
        String sql = "UPDATE tickets_code SET code = ?, service_id = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, dto.getCode());
            stmt.setInt(2, getServiceIdByName(conn, dto.getServiceName()));
            stmt.setInt(3, id);

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("No se encontró el ticket con ID " + id);
            }

            dto.setId(id);
            return dto;

        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar ticket", e);
        }
    }

    public void delete(Integer id) {
        String sql = "DELETE FROM tickets_code WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("No se encontró el ticket con ID " + id);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar ticket", e);
        }
    }

    private int getServiceIdByName(Connection conn, String serviceName) throws SQLException {
        String sql = "SELECT id FROM services WHERE name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, serviceName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("id");
            throw new SQLException("Servicio no encontrado: " + serviceName);
        }
    }
}
