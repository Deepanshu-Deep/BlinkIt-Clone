package com.grocery.model;


public class ResetPasswordRequest {

    private String email;
    private String phoneNumber;
    private String newPassword;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

//    public void setMobile(String phoneNumber) {
//        this.phoneNumber = phoneNumber;
//    }

    public String getNewPassword() {

        return newPassword;
    }

//    public void setNewPassword(String newPassword) {
//        this.newPassword = newPassword;
//    }

}
