package com.invex.employees.exception;

import lombok.Setter;

@Setter
public class EmployeeNotFoundException extends RuntimeException{

    private final String message;

    public EmployeeNotFoundException(String message) {
        super(message);
        this.message = message;
    }
}
