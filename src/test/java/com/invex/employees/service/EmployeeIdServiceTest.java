package com.invex.employees.service;


// Created 2026-04-13
import com.invex.employees.domain.Employee;
import com.invex.employees.exception.EmployeeNotFoundException;
import com.invex.employees.repository.EmployeeRepository;
import com.invex.employees.service.impl.EmployeeIdService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeIdServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeIdService employeeIdService;

    @Test
    void returnsEmployeeWhenFound() {
        Employee employee = Employee.builder().id(1).firstName("a").build();
        when(employeeRepository.findById(1)).thenReturn(Optional.of(employee));

        assertEquals(employee, employeeIdService.execute(1));
    }

    @Test
    void throwsWhenMissing() {
        when(employeeRepository.findById(2)).thenReturn(Optional.empty());
        assertThrows(EmployeeNotFoundException.class, () -> employeeIdService.execute(2));
    }
}
