package com.invex.employees.service.impl;

import com.invex.employees.domain.Employee;
import com.invex.employees.exception.EmployeeNotFoundException;
import com.invex.employees.repository.EmployeeRepository;
import com.invex.employees.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchEmployeeService implements EmployeeService<String, Employee> {

    private final EmployeeRepository employeeRepository;

    @Override
    public Employee execute(String name) {
        return employeeRepository.findByFirstName(name).orElseThrow(() -> new EmployeeNotFoundException("Employee not found"));
    }
}
