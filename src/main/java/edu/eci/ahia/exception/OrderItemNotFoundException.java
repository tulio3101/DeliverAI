package edu.eci.ahia.exception;

public class OrderItemNotFoundException extends RuntimeException {
   
    public OrderItemNotFoundException(String message) {
        super(message);
    }

}
