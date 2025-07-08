package com.invex.employees.repository;

import com.invex.employees.domain.Employee;
import com.invex.employees.domain.Role;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

@DataJpaTest
@ActiveProfiles("test")
class EmployeeRepositoryTest {

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

    @Test
    void deleteById(){
        Employee employee = employeeRepository.findById(1).get();
        employeeRepository.delete(employee);
        Assertions.assertTrue(employeeRepository.findAll().isEmpty());
    }

    @Test
    void save(){
        Employee employee = Employee.builder().age(33).sex("H").role(Role.ARCHITEC).middleName("First")
                .secondLastName("Second").lastName("last").birthDay(LocalDate.now()).enabled(true).build();
        employeeRepository.save(employee);
        Assertions.assertNotNull(employee.getId());
    }

    @Test
    void update(){
        Employee employee = employeeRepository.findById(1).get();
        employee.setSex("M");
        employeeRepository.save(employee);
        Assertions.assertNotNull(employee.getId());
    }
}
