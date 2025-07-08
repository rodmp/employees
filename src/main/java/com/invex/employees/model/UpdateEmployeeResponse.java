package com.invex.employees.model;

import com.invex.employees.domain.Employee;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateEmployeeResponse {

    private Employee employee;
}
