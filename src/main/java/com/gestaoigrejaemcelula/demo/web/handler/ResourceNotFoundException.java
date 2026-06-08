package com.gestaoigrejaemcelula.demo.web.handler;



public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}