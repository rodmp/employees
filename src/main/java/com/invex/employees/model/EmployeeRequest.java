package com.invex.employees.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.invex.employees.domain.Employee;
import com.invex.employees.domain.Role;
import com.invex.employees.validator.EnumNamePattern;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Builder
public class EmployeeRequest {

    @NotEmpty
    private String firstName;

    private String middleName;

    @NotEmpty
    private String lastName;

    @NotEmpty
    private String secondLastName;

    @NotNull
    private Integer age;

    @NotEmpty
    private String sex;

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate birthDay;

    @NotNull
    @EnumNamePattern(regexp = "PROGRAMMER|SCRUM|ARCHITECT")
    private Role role;

    @NotNull
    private Boolean enabled;

    public Employee mapEmployee(){
        return Employee.builder().age(this.age).birthDay(this.birthDay).enabled(this.enabled).firstName(this.firstName)
                .lastName(this.lastName).middleName(this.middleName).secondLastName(this.secondLastName).role(this.role)
                .sex(this.sex).build();
    }
}
