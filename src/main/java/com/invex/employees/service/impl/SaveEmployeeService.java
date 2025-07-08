package com.invex.employees.service.impl;

import com.invex.employees.domain.Employee;
import com.invex.employees.model.EmployeeRequest;
import com.invex.employees.repository.EmployeeRepository;
import com.invex.employees.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SaveEmployeeService implements EmployeeService<EmployeeRequest, Integer> {

    private final EmployeeRepository employeeRepository;

    @Override
    public Integer execute(EmployeeRequest employeeRequest) {
        Employee employee = employeeRepository.save(employeeRequest.mapEmployee());
        return employee.getId();
    }
}
