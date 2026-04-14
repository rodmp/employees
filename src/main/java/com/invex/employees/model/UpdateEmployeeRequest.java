package com.invex.employees.model;


// Created 2026-04-13
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.invex.employees.domain.Employee;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record UpdateEmployeeRequest(
        @JsonIgnore
        Integer id,
        String middleName,
        String firstName,
        String secondLastName,
        String lastName,
        String sex,
        Integer age,
        String jobTitle,
        @DateTimeFormat(pattern = "dd-MM-yyyy")
        @JsonFormat(pattern = "dd-MM-yyyy")
        LocalDate birthDay,
        Boolean enabled
) {

    public UpdateEmployeeRequest withId(Integer newId) {
        return new UpdateEmployeeRequest(newId, middleName, firstName, secondLastName, lastName, sex, age, jobTitle, birthDay, enabled);
    }

    public void mapUpdateEmployee(Employee employee) {
        if (age != null) {
            employee.setAge(age);
        }
        if (enabled != null) {
            employee.setEnabled(enabled);
        }
        if (jobTitle != null) {
            employee.setJobTitle(jobTitle);
        }
        if (sex != null) {
            employee.setSex(sex);
        }
        if (firstName != null) {
            employee.setFirstName(firstName);
        }
        if (lastName != null) {
            employee.setLastName(lastName);
        }
        if (middleName != null) {
            employee.setMiddleName(middleName);
        }
        if (secondLastName != null) {
            employee.setSecondLastName(secondLastName);
        }
        if (birthDay != null) {
            employee.setBirthDay(birthDay);
        }
    }
}
