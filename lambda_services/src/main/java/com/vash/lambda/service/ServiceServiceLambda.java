package com.vash.lambda.service;

import com.vash.lambda.model.ServiceDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ServiceServiceLambda {

    public List<ServiceDTO> getAll() {
        List<ServiceDTO> services = new ArrayList<>();
        String sql = "SELECT * FROM services";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ServiceDTO s = new ServiceDTO();
                s.setId(rs.getInt("id"));
                s.setName(rs.getString("name"));
                s.setType(rs.getString("type"));
                s.setDescription(rs.getString("description"));
                services.add(s);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener servicios", e);
        }
        return services;
    }

    public ServiceDTO create(ServiceDTO dto) {
        String sql = "INSERT INTO services (name, type, description) VALUES (?, ?, ?) RETURNING id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, dto.getName());
            stmt.setString(2, dto.getType());
            stmt.setString(3, dto.getDescription());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                dto.setId(rs.getInt("id"));
            }

            return dto;

        } catch (SQLException e) {
            throw new RuntimeException("Error al crear servicio", e);
        }
    }

    public Optional<ServiceDTO> findById(Integer id) {
        String sql = "SELECT * FROM services WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                ServiceDTO dto = new ServiceDTO();
                dto.setId(rs.getInt("id"));
                dto.setName(rs.getString("name"));
                dto.setType(rs.getString("type"));
                dto.setDescription(rs.getString("description"));
                return Optional.of(dto);
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar servicio por ID", e);
        }
    }

    public ServiceDTO update(Integer id, ServiceDTO dto) {
        String sql = "UPDATE services SET name = ?, type = ?, description = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, dto.getName());
            stmt.setString(2, dto.getType());
            stmt.setString(3, dto.getDescription());
            stmt.setInt(4, id);

            stmt.executeUpdate();
            dto.setId(id);
            return dto;

        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar servicio", e);
        }
    }

    public void delete(Integer id) {
        String sql = "DELETE FROM services WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar servicio", e);
        }
    }
}
