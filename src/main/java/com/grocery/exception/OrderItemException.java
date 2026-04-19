package com.grocery.exception;

public class OrderItemException extends RuntimeException{

    public OrderItemException() {

    }


    public OrderItemException(String msg) {
        super(msg);
    }

}
