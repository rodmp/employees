package com.invex.employees.controller;


// Created 2026-04-13
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestPropertySource(properties = "logging.level.com.invex.employees.config.RequestMdcFilter=DEBUG")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EmployeesControllerTests {

    /** Mismo usuario que en src/test/resources/application.properties */
    private static RequestPostProcessor asTestUser() {
        return httpBasic("test", "test");
    }

    @Autowired
    private MockMvc mockMvc;

    @Test
	@Order(1)
	void listEmployees() throws Exception {
        mockMvc.perform(get("/employees").with(asTestUser()))
                .andExpect(status().isOk());
    }

    @Test
    @Order(2)
    void getEmployeesById() throws Exception {
        mockMvc.perform(get("/employees/1").with(asTestUser()))
                .andExpect(status().isOk());
    }

    @Test
    @Order(3)
    void getEmployeesByPartialName() throws Exception {
        mockMvc.perform(get("/employees/search?name=jos").with(asTestUser()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeList", hasSize(1)));
    }

    @Test
    @Order(4)
    void saveEmployeesBatch() throws Exception {
        String body = """
                {"employees":[
                  {"firstName":"luis","middleName":null,"lastName":"contreras","secondLastName":"muñiz","age":32,"sex":"H","birthDay":"23-01-2020","jobTitle":"Programmer","enabled":true},
                  {"firstName":"ana","middleName":"maria","lastName":"lopez","secondLastName":"ruiz","age":28,"sex":"M","birthDay":"01-05-1998","jobTitle":"Scrum Master","enabled":true}
                ]}""";

        mockMvc.perform(post("/employees").with(asTestUser()).content(body).header("Content-Type", "application/json"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ids", hasSize(2)));
    }

    @Test
    @Order(5)
    void updateEmployeePartially() throws Exception {
        String body = "{\"age\":41}";

        mockMvc.perform(put("/employees/1").with(asTestUser()).content(body).header("Content-Type", "application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employee.age").value(41));
    }

    @Test
    @Order(6)
    void deleteEmployee() throws Exception {
        mockMvc.perform(delete("/employees/1").with(asTestUser()))
                .andExpect(status().isNoContent());
    }

    @Test
    @Order(7)
    void getEmployeeByIdNotFound() throws Exception {
        mockMvc.perform(get("/employees/99999").with(asTestUser()))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(8)
    void postInvalidJsonReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/employees").with(asTestUser())
                        .content("{invalid json")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(9)
    void postEmptyEmployeesReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/employees").with(asTestUser())
                        .content("{\"employees\":[]}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(10)
    void putNotFound() throws Exception {
        mockMvc.perform(put("/employees/99999").with(asTestUser())
                        .content("{\"age\":1}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(11)
    void employeesWithoutCredentialsReturns401() throws Exception {
        mockMvc.perform(get("/employees"))
                .andExpect(status().isUnauthorized());
    }
}
