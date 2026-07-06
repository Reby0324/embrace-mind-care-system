package model;

public class ServiceType {
    private int id;
    private String mainCategory;
    private String name;
    private String description;

    public ServiceType() {
    }

    public ServiceType(int id, String mainCategory, String name, String description) {
        this.id = id;
        this.mainCategory = mainCategory;
        this.name = name;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public ServiceType setId(int id) {
        this.id = id;
        return this;
    }

    public String getMainCategory() {
        return mainCategory;
    }

    public ServiceType setMainCategory(String mainCategory) {
        this.mainCategory = mainCategory;
        return this;
    }

    public String getName() {
        return name;
    }

    public ServiceType setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public ServiceType setDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public String toString() {
        if (id == 0) {
            return name;
        }
        return name + "｜" + mainCategory;
    }
}
