package com.example.group1_projectwork;

public class User {
    private String userID;
    private String userName;
    private String passWord;

    // Constructor without userID (for registration)
    public User(String userName, String passWord) {
        this.userName = userName;
        this.passWord = passWord;
    }

    // Constructor for user with userID
    public User(String userID, String userName, String passWord) {
        this.userID = userID;
        this.userName = userName;
        this.passWord = passWord;
    }

    // Getter and setter for userID
    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    // Getter and setter for username
    public String getUsername() {
        return userName;
    }

    public void setUsername(String userName) {
        this.userName = userName;
    }

    // Getter and setter for password
    public String getPassword() {
        return passWord;
    }

    public void setPassword(String passWord) {
        this.passWord = passWord;
    }

    @Override
    public String toString() {
        return "User{" +
                "userID='" + userID + '\'' +
                ", userName='" + userName + '\'' +
                ", passWord='" + passWord + '\'' +
                '}';
    }
}
