package com.company;

import java.time.LocalDate;
import java.time.LocalTime;

public class User {
    private boolean is_admin = false;
    private final String user_name;
    private final String password;
    private String birthday;
    private String name;
    private String surname;
    enum Gender{
        M,
        F;
    }
    private Gender gender;
    private String email;

    public String getEmail() {
        return email;
    }

    public Gender getGender(){
        return gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getPassword() {
        return password;
    }

    public User(String userName, String string, String name, String surname, String user_name, String password) {
        this.user_name = user_name;
        this.password = password;
    }

    public User(String user_name, String password, String birthday, String name, String surname, String gender, String email) {
        this.user_name = user_name;
        this.password = password;
        this.birthday = birthday;
        this.name = name;
        this.surname = surname;
        this.gender = Gender.valueOf(gender);
        this.email = email;
    }

    public User(String user_name, String password, String birthday, String name, String surname, String gender, String email, Boolean is_admin) {
        this.user_name = user_name;
        this.password = password;
        this.birthday = birthday;
        this.name = name;
        this.surname = surname;
        this.gender = Gender.valueOf(gender);
        this.email = email;
        this.is_admin = is_admin;
    }

    public boolean isAdmin(){
        return is_admin;
    }

    public void printUser(){
        System.out.println("----------------Username: " + user_name + "----------------------------------");
        System.out.println("Name: " + name);
        System.out.println("Surname: " + surname);
        System.out.println("Birthdate: " + birthday);
    }
}
