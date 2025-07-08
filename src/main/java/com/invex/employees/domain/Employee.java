package com.invex.employees.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "EMPLOYEE")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "MIDDLE_NAME")
    private String middleName;

    @Column(name = "LAST_NAME")
    private String lastName;

    @Column(name = "SECOND_LAST_NAME")
    private String secondLastName;

    @Column(name = "AGE")
    private Integer age;

    @Column(name = "SEX")
    private String sex;

    @Column(name = "BIRTHDAY")
    private LocalDate birthDay;

    @Column(name = "ROLE")
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "SYSTEM_REGISTRATION")
    private LocalDateTime systemRegistration;

    @Column(name = "ENABLED")
    private Boolean enabled;

    @PrePersist
    public void prepersit() {
        this.systemRegistration = LocalDateTime.now();
    }
}
