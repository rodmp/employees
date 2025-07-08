package com.invex.employees.service;

public interface EmployeeService<T, R> {

    public R execute(T t);
}
