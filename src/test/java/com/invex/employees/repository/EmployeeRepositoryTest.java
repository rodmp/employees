package com.invex.employees.repository;

import com.invex.employees.domain.Employee;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@DataJpaTest
@ActiveProfiles("test")
public class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    void getEmployees(){
        List<Employee> employeeList = employeeRepository.findAll();
        Assertions.assertFalse(employeeList.isEmpty());
    }

    @Test
    void getEmployeeById(){
        Employee employee = employeeRepository.findById(1).get();
        Assertions.assertNotNull(employee);
    }

    @Test
    void getEmployeeByName(){
        Employee employee = employeeRepository.findByFirstName("jose").get();
        Assertions.assertNotNull(employee);
    }
}
