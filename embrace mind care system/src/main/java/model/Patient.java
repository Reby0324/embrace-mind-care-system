package model;

import java.time.LocalDate;

public class Patient {
    private int id;
    private String name;
    private String idNo;
    private LocalDate birthDate;
    private String phone;

    public int getId() {
        return id;
    }

    public Patient setId(int id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Patient setName(String name) {
        this.name = name;
        return this;
    }

    public String getIdNo() {
        return idNo;
    }

    public Patient setIdNo(String idNo) {
        this.idNo = idNo;
        return this;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public Patient setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public Patient setPhone(String phone) {
        this.phone = phone;
        return this;
    }
}
