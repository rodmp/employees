package com.invex.employees.controller;

// Created 2026-04-13
import com.invex.employees.constant.ApiPaths;
import com.invex.employees.domain.Employee;
import com.invex.employees.model.*;
import com.invex.employees.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.invex.employees.constant.ApiPaths.TRANSACTION_ID_HEADER;

@Tag(name = "Employees")
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPaths.EMPLOYEES)
public class EmployeeController {

    /** Misma clave que pone {@code RequestMdcFilter}; va en el patrón de consola y aquí explícito para greps. */
    private static final String MDC_TRANSACTION_ID = "http.transaction_id";

    private static final String TX_ID_DESC =
            "Id de correlación. Si no se envía, el servidor genera uno y lo devuelve en el header de respuesta.";

    private final EmployeeService<Void, List<Employee>> listEmployees;
    private final EmployeeService<Integer, Employee> getEmployeeById;
    private final EmployeeService<EmployeeBatchRequest, SaveEmployeesResponse> createEmployees;
    private final EmployeeService<UpdateEmployeeRequest, Employee> updateEmployee;
    private final EmployeeService<Integer, Void> deleteEmployee;
    private final EmployeeService<String, List<Employee>> searchByName;

    @Operation(
            summary = "List employees",
            parameters = @Parameter(name = TRANSACTION_ID_HEADER, in = ParameterIn.HEADER, required = false,
                    description = TX_ID_DESC))
    @GetMapping
    public ResponseEntity<EmployeesResponse> list() {
        List<Employee> rows = listEmployees.execute(null);
        log.info("GET /employees, devueltos={} | {}={}", rows.size(), TRANSACTION_ID_HEADER, correlationId());
        return ResponseEntity.ok(new EmployeesResponse(rows));
    }

    @Operation(
            summary = "Get employee by id",
            parameters = @Parameter(name = TRANSACTION_ID_HEADER, in = ParameterIn.HEADER, required = false,
                    description = TX_ID_DESC))
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getById(@PathVariable Integer id) {
        Employee employee = getEmployeeById.execute(id);
        log.info("GET /employees/{} | {}={}", id, TRANSACTION_ID_HEADER, correlationId());
        return ResponseEntity.ok(new EmployeeResponse(employee));
    }

    @Operation(
            summary = "Create one or more employees",
            parameters = @Parameter(name = TRANSACTION_ID_HEADER, in = ParameterIn.HEADER, required = false,
                    description = TX_ID_DESC))
    @PostMapping
    public ResponseEntity<SaveEmployeesResponse> create(@RequestBody @Valid EmployeeBatchRequest batch) {
        int n = batch.employees().size();
        log.info("POST /employees, {} a insertar | {}={}", n, TRANSACTION_ID_HEADER, correlationId());
        SaveEmployeesResponse created = createEmployees.execute(batch);
        log.info("POST /employees, ids={} | {}={}", created.ids(), TRANSACTION_ID_HEADER, correlationId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Update employee (partial)",
            parameters = @Parameter(name = TRANSACTION_ID_HEADER, in = ParameterIn.HEADER, required = false,
                    description = TX_ID_DESC))
    @PutMapping("/{id}")
    public ResponseEntity<UpdateEmployeeResponse> update(@PathVariable Integer id,
                                                         @RequestBody UpdateEmployeeRequest body) {
        Employee updated = updateEmployee.execute(body.withId(id));
        log.info("PUT /employees/{} | {}={}", id, TRANSACTION_ID_HEADER, correlationId());
        return ResponseEntity.ok(new UpdateEmployeeResponse(updated));
    }

    @Operation(
            summary = "Delete employee",
            parameters = @Parameter(name = TRANSACTION_ID_HEADER, in = ParameterIn.HEADER, required = false,
                    description = TX_ID_DESC))
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        deleteEmployee.execute(id);
        log.info("DELETE /employees/{} | {}={}", id, TRANSACTION_ID_HEADER, correlationId());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Search by name (partial)",
            parameters = @Parameter(name = TRANSACTION_ID_HEADER, in = ParameterIn.HEADER, required = false,
                    description = TX_ID_DESC))
    @GetMapping("/search")
    public ResponseEntity<EmployeesResponse> search(@RequestParam("name") @NotBlank String name) {
        log.info("GET /employees/search, len={} | {}={}", name.trim().length(), TRANSACTION_ID_HEADER, correlationId());
        List<Employee> matches = searchByName.execute(name);
        log.info("GET /employees/search, n={} | {}={}", matches.size(), TRANSACTION_ID_HEADER, correlationId());
        return ResponseEntity.ok(new EmployeesResponse(matches));
    }

    private String correlationId() {
        String id = MDC.get(MDC_TRANSACTION_ID);
        return id != null ? id : "-";
    }
}
