package com.vash.lambda.service;

import com.vash.lambda.model.CodeDTO;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CodeServiceLambda {

    public List<CodeDTO> getAll() {
        List<CodeDTO> result = new ArrayList<>();
        String sql = """
            SELECT tc.id, tc.code, tc.created, tc.customer_name, s.name AS service_name, tc.attended
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
                dto.setCustomerName(rs.getString("customer_name"));
                dto.setServiceName(rs.getString("service_name"));
                dto.setCreated(rs.getTimestamp("created").toLocalDateTime().toString());
                dto.setAttended(rs.getBoolean("attended"));
                result.add(dto);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener tickets", e);
        }

        return result;
    }

    public CodeDTO getById(int id) {
        String sql = """
            SELECT tc.id, tc.code, tc.created, tc.customer_name, s.name AS service_name, tc.attended
            FROM tickets_code tc
            JOIN services s ON tc.service_id = s.id
            WHERE tc.id = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                CodeDTO dto = new CodeDTO();
                dto.setId(rs.getInt("id"));
                dto.setCode(rs.getString("code"));
                dto.setCustomerName(rs.getString("customer_name"));
                dto.setServiceName(rs.getString("service_name"));
                dto.setCreated(rs.getTimestamp("created").toLocalDateTime().toString());
                dto.setAttended(rs.getBoolean("attended"));
                return dto;
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener ticket por ID", e);
        }
    }

    private String generateCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int index = (int) (Math.random() * chars.length());
            code.append(chars.charAt(index));
        }
        return code.toString();
    }

    public CodeDTO create(CodeDTO dto) {
        String sql = """
            INSERT INTO tickets_code (code, created, service_id, customer_name, attended)
            VALUES (?, ?, ?, ?, false)
            RETURNING id
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            LocalDateTime now = LocalDateTime.now();
            String generatedCode = generateCode();

            stmt.setString(1, generatedCode);
            stmt.setTimestamp(2, Timestamp.valueOf(now));
            stmt.setInt(3, getServiceIdByName(conn, dto.getServiceName()));
            stmt.setString(4, dto.getCustomerName());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                dto.setId(rs.getInt("id"));
                dto.setCreated(now.toString());
                dto.setCode(generatedCode);
                dto.setAttended(false);
            }

            return dto;

        } catch (SQLException e) {
            throw new RuntimeException("Error al crear ticket", e);
        }
    }

    public CodeDTO update(Integer id, CodeDTO dto) {
        String sql = "UPDATE tickets_code SET code = ?, service_id = ?, attended = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, dto.getCode());
            stmt.setInt(2, getServiceIdByName(conn, dto.getServiceName()));
            stmt.setBoolean(3, dto.getAttended() != null ? dto.getAttended() : false);
            stmt.setInt(4, id);

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("No se encontró el ticket con ID " + id);
            }

            dto.setId(id);

            if (Boolean.TRUE.equals(dto.getAttended())) {
                System.out.println("{\"level\":\"INFO\",\"message\":\"Ticket atendido\",\"code\":\"" + dto.getCode() + "\"}");
            }

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
