package model;

public class Professional {
    private int id;
    private String code;
    private String name;
    private String role;
    private int serviceTypeId;
    private String serviceTypeName;

    public Professional() {
    }

    public int getId() {
        return id;
    }

    public Professional setId(int id) {
        this.id = id;
        return this;
    }

    public String getCode() {
        return code;
    }

    public Professional setCode(String code) {
        this.code = code;
        return this;
    }

    public String getName() {
        return name;
    }

    public Professional setName(String name) {
        this.name = name;
        return this;
    }

    public String getRole() {
        return role;
    }

    public Professional setRole(String role) {
        this.role = role;
        return this;
    }

    public int getServiceTypeId() {
        return serviceTypeId;
    }

    public Professional setServiceTypeId(int serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
        return this;
    }

    public String getServiceTypeName() {
        return serviceTypeName;
    }

    public Professional setServiceTypeName(String serviceTypeName) {
        this.serviceTypeName = serviceTypeName;
        return this;
    }

    @Override
    public String toString() {
        if (id == 0) {
            return name;
        }
        return code + " " + name + "（" + role + "）";
    }
}
