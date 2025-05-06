package com.vash.lambda.model;

public class UserDTO {
    private Integer id;
    private String documentType;
    private Integer numberDoc;
    private String name;
    private String lastName;
    private Integer moduleId;
    private String email;
    private String password;
    private String role;
    private String username;

    public UserDTO() {}

    // Getters y Setters manuales

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public Integer getNumberDoc() {
        return numberDoc;
    }

    public void setNumberDoc(Integer numberDoc) {
        this.numberDoc = numberDoc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getModuleId() {
        return moduleId;
    }

    public void setModuleId(Integer moduleId) {
        this.moduleId = moduleId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
