package com.vash.lambda.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.vash.lambda.model.UserDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserServiceLambda {

    private final List<UserDTO> users = new ArrayList<>();
    private int lastId = 0;

    public List<UserDTO> getAll() {
        List<UserDTO> userList = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM users")) {

            while (rs.next()) {
                UserDTO user = new UserDTO();
                user.setId(rs.getInt("id"));
                user.setDocumentType(rs.getString("document_type"));
                user.setNumberDoc(rs.getInt("number_doc"));
                user.setName(rs.getString("name"));
                user.setLastName(rs.getString("last_name"));
                user.setModuleId(rs.getInt("module_id"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                user.setUsername(rs.getString("username"));
                userList.add(user);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener usuarios", e);
        }
        return userList;
    }

    public UserDTO createUser(UserDTO userDTO) {
        String sql = "INSERT INTO users (document_type, number_doc, name, last_name, module_id, email, password, role, username) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userDTO.getDocumentType());
            stmt.setInt(2, userDTO.getNumberDoc());
            stmt.setString(3, userDTO.getName());
            stmt.setString(4, userDTO.getLastName());
            stmt.setInt(5, userDTO.getModuleId());
            stmt.setString(6, userDTO.getEmail());
            stmt.setString(7, userDTO.getPassword());
            stmt.setString(8, userDTO.getRole());
            stmt.setString(9, userDTO.getUsername());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                userDTO.setId(rs.getInt("id"));
            }

            return userDTO;

        } catch (Exception e) {
            throw new RuntimeException("Error al crear usuario", e);
        }
    }

    public Optional<UserDTO> findById(Integer id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                UserDTO user = new UserDTO();
                user.setId(rs.getInt("id"));
                user.setDocumentType(rs.getString("document_type"));
                user.setNumberDoc(rs.getInt("number_doc"));
                user.setName(rs.getString("name"));
                user.setLastName(rs.getString("last_name"));
                user.setModuleId(rs.getInt("module_id"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                user.setUsername(rs.getString("username"));
                return Optional.of(user);
            }

            return Optional.empty();
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar usuario por ID", e);
        }
    }

    public UserDTO updateUser(Integer id, UserDTO user) {
        String sql = "UPDATE users SET document_type = ?, number_doc = ?, name = ?, last_name = ?, module_id = ?, email = ?, password = ?, role = ?, username = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getDocumentType());
            stmt.setInt(2, user.getNumberDoc());
            stmt.setString(3, user.getName());
            stmt.setString(4, user.getLastName());
            stmt.setInt(5, user.getModuleId());
            stmt.setString(6, user.getEmail());
            stmt.setString(7, user.getPassword());
            stmt.setString(8, user.getRole());
            stmt.setString(9, user.getUsername());
            stmt.setInt(10, id);

            stmt.executeUpdate();
            user.setId(id);
            return user;

        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar usuario", e);
        }
    }

    public void delete(Integer id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar usuario", e);
        }
    }

    public List<UserDTO> findByNumberDocOrName(Integer numberDoc, String name) {
        List<UserDTO> result = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE number_doc = ? OR LOWER(name) = LOWER(?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, numberDoc != null ? numberDoc : -1);  // -1 si null
            stmt.setString(2, name != null ? name : "");

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                UserDTO user = new UserDTO();
                user.setId(rs.getInt("id"));
                user.setDocumentType(rs.getString("document_type"));
                user.setNumberDoc(rs.getInt("number_doc"));
                user.setName(rs.getString("name"));
                user.setLastName(rs.getString("last_name"));
                user.setModuleId(rs.getInt("module_id"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                user.setUsername(rs.getString("username"));
                result.add(user);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error al buscar usuario por nombre o número de documento", e);
        }
        return result;
    }
}
