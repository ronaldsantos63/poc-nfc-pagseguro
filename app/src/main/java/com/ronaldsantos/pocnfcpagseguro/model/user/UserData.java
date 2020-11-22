package com.ronaldsantos.pocnfcpagseguro.model.user;

import java.util.Objects;

public class UserData {

    private String name;
    private String birthday;
    private String address;
    private String motherName = "";
    private String fatherName = "";
    private String cellPhone = "";

    public UserData() {
    }

    public UserData(String name, String birthday, String address) {
        this.name = name;
        this.birthday = birthday;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public UserData setName(String name) {
        this.name = name;
        return this;
    }

    public String getBirthday() {
        return birthday;
    }

    public UserData setBirthday(String birthday) {
        this.birthday = birthday;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public UserData setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getMotherName() {
        return motherName;
    }

    public UserData setMotherName(String motherName) {
        this.motherName = motherName;
        return this;
    }

    public String getFatherName() {
        return fatherName;
    }

    public UserData setFatherName(String fatherName) {
        this.fatherName = fatherName;
        return this;
    }

    public String getCellPhone() {
        return cellPhone;
    }

    public UserData setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserData)) return false;
        UserData userData = (UserData) o;
        return getName().equals(userData.getName()) &&
                getBirthday().equals(userData.getBirthday()) &&
                getAddress().equals(userData.getAddress()) &&
                getMotherName().equals(userData.getMotherName()) &&
                getFatherName().equals(userData.getFatherName()) &&
                getCellPhone().equals(userData.getCellPhone());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getBirthday(), getAddress(), getMotherName(), getFatherName(), getCellPhone());
    }

    @Override
    public String toString() {
        return "UserData{" +
                "name='" + name + '\'' +
                ", birthday='" + birthday + '\'' +
                ", address='" + address + '\'' +
                ", motherName='" + motherName + '\'' +
                ", fatherName='" + fatherName + '\'' +
                ", cellPhone='" + cellPhone + '\'' +
                '}';
    }
}
