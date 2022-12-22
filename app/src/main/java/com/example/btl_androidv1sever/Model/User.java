package com.example.btl_androidv1sever.Model;

public class User {
    private String Name,Password,Phone,IsWork;

    public User(String name, String password) {
        Name = name;
        Password = password;
    }

    public User() {
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getIsWork() {
        return IsWork;
    }

    public void setIsWork(String isWork) {
        IsWork = isWork;
    }
}
