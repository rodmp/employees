package com.invex.employees.service;


// Created 2026-04-13
public interface EmployeeService<T, R> {

    R execute(T input);
}
