package com.grocery.exception;

public class UserException extends RuntimeException{

    public UserException() {

    }


    public UserException(String msg) {
        super(msg);
    }

}
