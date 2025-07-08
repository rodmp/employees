package com.invex.employees.service;

import com.invex.employees.exception.EmployeeNotFoundException;
import com.invex.employees.repository.EmployeeRepository;
import com.invex.employees.service.impl.DeleteEmployeeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
class DeleteEmployeeServiceTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DeleteEmployeeService deleteEmployeeService;

    @Test
    void deleteEmployee() {
        Assertions.assertThrows(EmployeeNotFoundException.class, () -> deleteEmployeeService.execute(1));
    }
}
