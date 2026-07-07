package model;

public class User {
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_PROFESSIONAL = "PROFESSIONAL";
    public static final String ROLE_USER = "USER";

    private int id;
    private String username;
    private String role;
    private String displayName;
    private Integer professionalId;

    public int getId() {
        return id;
    }

    public User setId(int id) {
        this.id = id;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public User setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getRole() {
        return role;
    }

    public User setRole(String role) {
        this.role = role;
        return this;
    }

    public String getDisplayName() {
        return displayName;
    }

    public User setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public Integer getProfessionalId() {
        return professionalId;
    }

    public User setProfessionalId(Integer professionalId) {
        this.professionalId = professionalId;
        return this;
    }

    public boolean isAdmin() {
        return ROLE_ADMIN.equals(role);
    }

    public boolean isProfessional() {
        return ROLE_PROFESSIONAL.equals(role);
    }

    public boolean isGeneralUser() {
        return ROLE_USER.equals(role);
    }

    public String getRoleText() {
        if (isAdmin()) {
            return "管理員";
        }
        if (isProfessional()) {
            return "醫師 / 心理師";
        }
        return "一般使用者";
    }
}
