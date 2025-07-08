package com.invex.employees.controller;

import com.invex.employees.domain.Employee;
import com.invex.employees.model.*;
import com.invex.employees.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("v1/employees")
public class EmployeeController {

    private final EmployeeService<Void, List<Employee>> employeeService;

    private final EmployeeService<Integer, Employee> employeeByIdService;

    private final EmployeeService<EmployeeRequest, Integer> saveEmployeeService;

    private final EmployeeService<UpdateEmployeeRequest, Employee> updateEmployeeService;

    private final EmployeeService<Integer, Void> deleteEmployeeService;

    private final EmployeeService<String, Employee> searchEmployeeService;

    @Operation(summary = "Get all employees", description = "Returns a list of all employees")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = List.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<EmployeesResponse> getEmployees() {
        List<Employee> employeeList = employeeService.execute(null);
        return ResponseEntity.ok(EmployeesResponse.builder().employeeList(employeeList).build());
    }

    @Operation(summary = "Get employee by id", description = "Returns employee by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = List.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getEmployeesById(@PathVariable(value = "id", required = true) Integer employeeId) {
        Employee employee = employeeByIdService.execute(employeeId);
        return ResponseEntity.ok(EmployeeResponse.builder().employee(employee).build());
    }

    @Operation(summary = "Save employee", description = "Save employee service")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = List.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<SaveEmployeeResponse> saveEmployee(@RequestBody @Valid EmployeeRequest employee) {
        Integer id = saveEmployeeService.execute(employee);
        return ResponseEntity.ok(SaveEmployeeResponse.builder().id(id).build());
    }

    @Operation(summary = "Update employee", description = "Update employee by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = List.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UpdateEmployeeResponse> updateEmployee(@PathVariable(value = "id", required = true) Integer employeeId,
                                                             @RequestBody @Valid UpdateEmployeeRequest employee) {
        employee.setId(employeeId);
        return ResponseEntity.ok(UpdateEmployeeResponse.builder().employee(updateEmployeeService.execute(employee)).build());
    }

    @Operation(summary = "Delete employee", description = "Delete employee by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = List.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable(value = "id", required = true) Integer employeeId) {
        deleteEmployeeService.execute(employeeId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Search employee", description = "Search employee by name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = List.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/search")
    public ResponseEntity<Employee> searchEmployee(@RequestParam("name") String name) {
        Employee employee = searchEmployeeService.execute(name);
        return ResponseEntity.ok(employee);
    }
}
