package com.invex.employees.service.impl;

import com.invex.employees.domain.Employee;
import com.invex.employees.exception.EmployeeNotFoundException;
import com.invex.employees.model.UpdateEmployeeRequest;
import com.invex.employees.repository.EmployeeRepository;
import com.invex.employees.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateEmployeeService implements EmployeeService<UpdateEmployeeRequest, Employee> {

    private final EmployeeRepository employeeRepository;

    @Override
    public Employee execute(UpdateEmployeeRequest updateEmployeeRequest) {
        Employee employee = employeeRepository.findById(updateEmployeeRequest.getId())
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found"));
        updateEmployeeRequest.mapUpdateEmployee(employee);
        employeeRepository.save(employee);

        return employee;
    }
}
