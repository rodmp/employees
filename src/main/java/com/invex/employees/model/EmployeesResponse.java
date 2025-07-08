package com.invex.employees.model;

import com.invex.employees.domain.Employee;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class EmployeesResponse {

    private List<Employee> employeeList;
}
