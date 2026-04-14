package com.invex.employees.repository;


// Created 2026-04-13
import com.invex.employees.domain.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    Optional<Employee> findById(Integer id);

    @Query("""
            SELECT e FROM Employee e WHERE
            LOWER(e.firstName) LIKE LOWER(CONCAT('%', :name, '%'))
            OR LOWER(COALESCE(e.middleName, '')) LIKE LOWER(CONCAT('%', :name, '%'))
            OR LOWER(COALESCE(e.lastName, '')) LIKE LOWER(CONCAT('%', :name, '%'))
            OR LOWER(COALESCE(e.secondLastName, '')) LIKE LOWER(CONCAT('%', :name, '%'))
            OR LOWER(COALESCE(e.jobTitle, '')) LIKE LOWER(CONCAT('%', :name, '%'))
            """)
    List<Employee> searchByNameContaining(@Param("name") String name);
}
