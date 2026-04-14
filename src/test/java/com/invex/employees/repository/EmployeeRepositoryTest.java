package com.invex.employees.repository;


// Created 2026-04-13
import com.invex.employees.domain.Employee;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

@DataJpaTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    @Order(1)
    void getEmployees() {
        List<Employee> employeeList = employeeRepository.findAll();
        Assertions.assertFalse(employeeList.isEmpty());
    }

    @Test
    @Order(2)
    void getEmployeeById() {
        Employee employee = employeeRepository.findById(1).get();
        Assertions.assertNotNull(employee);
    }

    @Test
    @Order(3)
    void searchEmployeesByPartialName() {
        List<Employee> found = employeeRepository.searchByNameContaining("jos");
        Assertions.assertEquals(1, found.size());
        Assertions.assertEquals("jose", found.get(0).getFirstName());
    }

    @Test
    @Order(6)
    void deleteById() {
        Employee employee = employeeRepository.findById(1).get();
        employeeRepository.delete(employee);
        Assertions.assertTrue(employeeRepository.findAll().isEmpty());
    }

    @Test
    @Order(4)
    void save() {
        Employee employee = Employee.builder()
                .firstName("x")
                .lastName("y")
                .secondLastName("z")
                .age(33)
                .sex("H")
                .jobTitle("Architect")
                .middleName("First")
                .birthDay(LocalDate.now())
                .enabled(true)
                .build();
        employeeRepository.save(employee);
        Assertions.assertNotNull(employee.getId());
    }

    @Test
    @Order(5)
    void update() {
        Employee employee = employeeRepository.findById(1).get();
        employee.setSex("M");
        employeeRepository.save(employee);
        Assertions.assertNotNull(employee.getId());
    }
}
