package com.invex.employees.service.impl;


// Created 2026-04-13
import com.invex.employees.domain.Employee;
import com.invex.employees.model.EmployeeBatchRequest;
import com.invex.employees.model.EmployeeRequest;
import com.invex.employees.model.SaveEmployeesResponse;
import com.invex.employees.repository.EmployeeRepository;
import com.invex.employees.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SaveEmployeeService implements EmployeeService<EmployeeBatchRequest, SaveEmployeesResponse> {

    private final EmployeeRepository employeeRepository;

    @Override
    public SaveEmployeesResponse execute(EmployeeBatchRequest batch) {
        List<Integer> ids = batch.employees().stream()
                .map(EmployeeRequest::mapEmployee)
                .map(employeeRepository::save)
                .map(Employee::getId)
                .toList();
        return new SaveEmployeesResponse(ids);
    }
}
