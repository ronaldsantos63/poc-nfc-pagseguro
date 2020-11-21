package com.ronaldsantos.pocnfcpagseguro;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class UserData {

    private String name;
    private String birthday;
    private String address;

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

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserData)) return false;
        UserData userData = (UserData) o;
        return Objects.equals(getName(), userData.getName()) &&
                Objects.equals(getBirthday(), userData.getBirthday()) &&
                Objects.equals(getAddress(), userData.getAddress());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getBirthday(), getAddress());
    }

    @NotNull
    @Override
    public String toString() {
        return "UserData{" +
                "name='" + name + '\'' +
                ", birthday='" + birthday + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
