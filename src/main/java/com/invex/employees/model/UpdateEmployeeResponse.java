package com.invex.employees.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.invex.employees.domain.Employee;
import com.invex.employees.domain.Role;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class UpdateEmployeeResponse {

    private Employee employee;
}
