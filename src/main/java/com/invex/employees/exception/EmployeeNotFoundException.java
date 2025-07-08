package com.invex.employees.exception;

import lombok.Setter;

@Setter
public class EmployeeNotFoundException extends RuntimeException{

    private String message;

    public EmployeeNotFoundException(String message) {
        super(message);
    }
}
