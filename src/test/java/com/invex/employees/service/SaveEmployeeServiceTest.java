package com.invex.employees.service;


// Created 2026-04-13
import com.invex.employees.domain.Employee;
import com.invex.employees.model.EmployeeBatchRequest;
import com.invex.employees.model.EmployeeRequest;
import com.invex.employees.model.SaveEmployeesResponse;
import com.invex.employees.repository.EmployeeRepository;
import com.invex.employees.service.impl.SaveEmployeeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SaveEmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private SaveEmployeeService saveEmployeeService;

    @Test
    void savesBatchAndReturnsIds() {
        Employee persisted = Employee.builder().id(7).firstName("n").build();
        when(employeeRepository.save(any(Employee.class))).thenReturn(persisted);

        EmployeeRequest one = new EmployeeRequest(
                "a",
                null,
                "b",
                "c",
                30,
                "H",
                LocalDate.of(1990, 1, 1),
                "Dev",
                true);
        EmployeeBatchRequest batch = new EmployeeBatchRequest(List.of(one, one));

        SaveEmployeesResponse response = saveEmployeeService.execute(batch);

        assertEquals(List.of(7, 7), response.ids());
        ArgumentCaptor<Employee> captor = ArgumentCaptor.forClass(Employee.class);
        verify(employeeRepository, times(2)).save(captor.capture());
    }
}
