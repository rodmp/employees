package com.invex.employees.service;


// Created 2026-04-13
import com.invex.employees.domain.Employee;
import com.invex.employees.exception.EmployeeNotFoundException;
import com.invex.employees.model.UpdateEmployeeRequest;
import com.invex.employees.repository.EmployeeRepository;
import com.invex.employees.service.impl.UpdateEmployeeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateEmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private UpdateEmployeeService updateEmployeeService;

    @Test
    void updatesWhenFound() {
        Employee existing = Employee.builder().id(1).firstName("old").age(20).build();
        when(employeeRepository.findById(1)).thenReturn(Optional.of(existing));

        UpdateEmployeeRequest req = new UpdateEmployeeRequest(
                1, null, "new", null, null, null, 30, null, null, null);

        Employee result = updateEmployeeService.execute(req);

        assertEquals("new", result.getFirstName());
        assertEquals(30, result.getAge());
        ArgumentCaptor<Employee> captor = ArgumentCaptor.forClass(Employee.class);
        verify(employeeRepository).save(captor.capture());
        assertEquals("new", captor.getValue().getFirstName());
    }

    @Test
    void throwsWhenMissing() {
        when(employeeRepository.findById(9)).thenReturn(Optional.empty());
        UpdateEmployeeRequest req = new UpdateEmployeeRequest(
                9, null, null, null, null, null, null, null, null, null);
        assertThrows(EmployeeNotFoundException.class, () -> updateEmployeeService.execute(req));
    }
}
