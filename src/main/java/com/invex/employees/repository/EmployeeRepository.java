package com.invex.employees.repository;

import com.invex.employees.domain.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    Optional<Employee> findById(Integer id);

    Optional<Employee> findByFirstName(String firstName);
}
