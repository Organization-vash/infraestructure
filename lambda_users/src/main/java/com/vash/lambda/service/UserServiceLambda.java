package com.vash.lambda.service;

import com.vash.lambda.model.UserDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserServiceLambda {

    private final List<UserDTO> users = new ArrayList<>();
    private int lastId = 0;

    public List<UserDTO> getAll() {
        return users;
    }

    public UserDTO createUser(UserDTO userDTO) {
        userDTO.setId(++lastId);
        users.add(userDTO);
        return userDTO;
    }

    public Optional<UserDTO> findById(Integer id) {
        return users.stream()
            .filter(user -> user.getId() != null && user.getId().equals(id))
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
        users.removeIf(user -> user.getId() != null && user.getId().equals(id));
    }

    public List<UserDTO> findByNumberDocOrName(Integer numberDoc, String name) {
        List<UserDTO> result = new ArrayList<>();
        for (UserDTO user : users) {
            boolean matchDoc = numberDoc != null && user.getNumberDoc() != null && user.getNumberDoc().equals(numberDoc);
            boolean matchName = name != null && user.getName() != null && user.getName().equalsIgnoreCase(name);

            if (matchDoc || matchName) {
                result.add(user);
            }
        }
        return result;
    }
}
