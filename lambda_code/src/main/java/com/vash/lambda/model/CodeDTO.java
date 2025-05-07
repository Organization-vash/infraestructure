package com.vash.lambda.model;

public class CodeDTO {
    private Integer id;
    private String code;
    private String customerName;
    private String serviceName;
    private String created;

    public CodeDTO() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
    
    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }
}
