package com.invex.employees.service.impl;

import com.invex.employees.domain.Employee;
import com.invex.employees.exception.EmployeeNotFoundException;
import com.invex.employees.repository.EmployeeRepository;
import com.invex.employees.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteEmployeeService implements EmployeeService<Integer, Void> {

    private final EmployeeRepository employeeRepository;

    @Override
    public Void execute(Integer id) {
        Employee employee = employeeRepository.findById(id).orElseThrow(() -> new EmployeeNotFoundException("Employee not found"));
        employeeRepository.delete(employee);
        return null;
    }
}
