package com.invex.employees.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.invex.employees.domain.Employee;
import com.invex.employees.domain.Role;
import com.invex.employees.validator.EnumNamePattern;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class UpdateEmployeeRequest {

    @JsonIgnore
    private Integer id;

    private String middleName;

    @NotEmpty
    private String firstName;

    @NotEmpty
    private String secondLastName;

    @NotEmpty
    private String lastName;

    @NotEmpty
    private String sex;

    @NotNull
    private Integer age;

    @NotNull
    @EnumNamePattern(regexp = "PROGRAMMER|SCRUM|ARCHITECT")
    private Role role;

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate birthDay;

    @NotNull
    private Boolean enabled;

    public void mapUpdateEmployee(Employee employee) {
        employee.setAge(this.age);
        employee.setEnabled(this.enabled);
        employee.setRole(this.role);
        employee.setSex(this.sex);
        employee.setFirstName(this.firstName);
        employee.setLastName(this.lastName);
        employee.setMiddleName(this.middleName);
        employee.setSecondLastName(this.secondLastName);
        employee.setBirthDay(this.birthDay);
    }
}
