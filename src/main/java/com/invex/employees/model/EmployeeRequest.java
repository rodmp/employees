package com.invex.employees.model;


// Created 2026-04-13
import com.fasterxml.jackson.annotation.JsonFormat;
import com.invex.employees.domain.Employee;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record EmployeeRequest(
        @NotBlank String firstName,
        String middleName,
        @NotBlank String lastName,
        @NotBlank String secondLastName,
        @NotNull Integer age,
        @NotBlank String sex,
        @DateTimeFormat(pattern = "dd-MM-yyyy")
        @JsonFormat(pattern = "dd-MM-yyyy")
        LocalDate birthDay,
        @NotBlank
        @Size(max = 120)
        String jobTitle,
        @NotNull Boolean enabled
) {

    public Employee mapEmployee() {
        return Employee.builder()
                .age(age)
                .birthDay(birthDay)
                .enabled(enabled)
                .firstName(firstName)
                .lastName(lastName)
                .middleName(middleName)
                .secondLastName(secondLastName)
                .jobTitle(jobTitle)
                .sex(sex)
                .build();
    }
}
