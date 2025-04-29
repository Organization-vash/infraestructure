package com.vash.lambda.service;

import com.vash.lambda.model.UserDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserServiceLambda {

    private List<UserDTO> users = new ArrayList<>();

    public List<UserDTO> getAll() {
        return users;
    }

    public UserDTO createUser(UserDTO userDTO) {
        userDTO.setId(users.size() + 1); // ID automático
        users.add(userDTO);
        return userDTO;
    }

    public Optional<UserDTO> findById(Integer id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    public UserDTO updateUser(Integer id, UserDTO updatedUser) {
        UserDTO existingUser = findById(id).orElseThrow(() -> new RuntimeException("User not found"));

        existingUser.setName(updatedUser.getName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setDocumentType(updatedUser.getDocumentType());
        existingUser.setNumberDoc(updatedUser.getNumberDoc());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setPassword(updatedUser.getPassword());
        existingUser.setRole(updatedUser.getRole());
        existingUser.setUsername(updatedUser.getUsername());

        return existingUser;
    }

    public void delete(Integer id) {
        users.removeIf(user -> user.getId().equals(id));
    }

    public List<UserDTO> findByNumberDocOrName(Integer numberDoc, String name) {
        List<UserDTO> result = new ArrayList<>();
        for (UserDTO user : users) {
            if ((numberDoc != null && user.getNumberDoc().equals(numberDoc)) ||
                (name != null && user.getName().equalsIgnoreCase(name))) {
                result.add(user);
            }
        }
        return result;
    }
}
