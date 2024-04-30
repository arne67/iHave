package com.susarne.ihave2.models;

public class RegisterRequestDto {
    private String userName;
    private String password;
    private String name;
    private String emailAddress;


    public RegisterRequestDto(String userName, String password, String name, String emailAddress) {
        this.userName = userName;
        this.password = password;
        this.name = name;
        this.emailAddress = emailAddress;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
