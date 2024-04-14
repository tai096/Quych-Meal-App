package com.example.quychmeal.Models;

public class User {
    private String id;
    private String email;
    private String username;
    private String password;
    private int age;
    private String sex;
    private String avatar;

    public User() {
    }

    public User(String id, String email, String username, String password, int age, String sex, String avatar) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.password = password;
        this.age = age;
        this.sex = sex;
        this.avatar = avatar;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getAge() {
        return age;
    }

    public String getSex() {
        return sex;
    }

    public String getAvatar() {
        return avatar;
    }
}
