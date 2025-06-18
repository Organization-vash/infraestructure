package com.vash.lambda.service;

import com.vash.lambda.model.AgencyDTO;
import com.vash.db.DatabaseInitializer;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AgencyServiceLambda {

    public AgencyServiceLambda() {
        DatabaseInitializer.initAgencyTable();
    }

    public AgencyDTO createAgency(AgencyDTO agency) throws SQLException {
        String sql = "INSERT INTO agencies (city, seat, created_at) VALUES (?, ?, ?) RETURNING id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, agency.getCity());
            stmt.setString(2, agency.getSeat());
            stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                agency.setId(rs.getInt("id"));
                agency.setCreatedAt(LocalDateTime.now().toString());
                return agency;
            } else {
                throw new SQLException("No se pudo crear la agencia.");
            }
        }
    }

    public List<AgencyDTO> getAll() throws SQLException {
        List<AgencyDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM agencies";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                AgencyDTO agency = new AgencyDTO();
                agency.setId(rs.getInt("id"));
                agency.setCity(rs.getString("city"));
                agency.setSeat(rs.getString("seat"));
                agency.setCreatedAt(rs.getTimestamp("created_at").toString());
                list.add(agency);
            }
        }
        return list;
    }

    public AgencyDTO updateAgency(int id, AgencyDTO updated) throws SQLException {
        String sql = "UPDATE agencies SET city = ?, seat = ?, updated_at = ? WHERE id = ?";
    
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setString(1, updated.getCity());
            stmt.setString(2, updated.getSeat());
            stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(4, id);
    
            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new SQLException("No se encontró la agencia con id: " + id);
            }
    
            updated.setId(id);
            updated.setCreatedAt(LocalDateTime.now().toString());
            return updated;
        }
    }
    

    public AgencyDTO findById(int id) {
        String sql = "SELECT * FROM agencies WHERE id = ?";
    
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
    
            if (rs.next()) {
                AgencyDTO dto = new AgencyDTO();
                dto.setId(rs.getInt("id"));
                dto.setCity(rs.getString("city"));
                dto.setSeat(rs.getString("seat"));
                dto.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime().toString());
                return dto;
            }
    
            return null;
    
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar agencia por ID", e);
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM agencies WHERE id = ?";
    
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setInt(1, id);
            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new SQLException("No se encontró la agencia con id: " + id);
            }
        }
    }
    
}
