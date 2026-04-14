package com.invex.employees.model;


// Created 2026-04-13
import com.invex.employees.domain.Employee;

import java.util.List;

public record EmployeesResponse(List<Employee> employeeList) {
}
