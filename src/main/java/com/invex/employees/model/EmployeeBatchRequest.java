package com.invex.employees.model;


// Created 2026-04-13
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record EmployeeBatchRequest(
        @NotEmpty(message = "employees must contain at least one item")
        List<@Valid EmployeeRequest> employees
) {
}
