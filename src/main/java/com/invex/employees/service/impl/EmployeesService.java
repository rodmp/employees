package com.invex.employees.service.impl;

import com.invex.employees.domain.Employee;
import com.invex.employees.repository.EmployeeRepository;
import com.invex.employees.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeesService implements EmployeeService<Void, List<Employee>> {

    private final EmployeeRepository employeeRepository;

    @Override
    public List<Employee> execute(Void unused) {
        return employeeRepository.findAll();
    }
}
