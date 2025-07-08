package com.invex.employees.controller;

import com.invex.employees.domain.Employee;
import com.invex.employees.service.EmployeeService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EmployeesControllerTests {

	@Autowired
	private EmployeeService<Void, List<Employee>> employeeService;

	@Autowired
	private MockMvc mockMvc;

	@Test
	@Order(1)
	void getEmployeess() throws Exception {

		mockMvc.perform(get("/v1/employees"))
				.andExpect(status().isOk());
	}

	@Test
	@Order(2)
	void getEmployeesById() throws Exception {

		mockMvc.perform(get("/v1/employees/1"))
				.andExpect(status().isOk());
	}

	@Test
	@Order(3)
	void getEmployeesByName() throws Exception {

		mockMvc.perform(get("/v1/employees/search?name=jose"))
				.andExpect(status().isOk());
	}

	@Test
	@Order(6)
	void deleteEmployee() throws Exception {

		mockMvc.perform(delete("/v1/employees/1"))
				.andExpect(status().isNoContent());
	}

	@Test
	@Order(4)
	void saveEmployee() throws Exception {

		String body = "{" +
				"    \"firstName\": \"luis\",\n" +
				"    \"middleName\": null,\n" +
				"    \"lastName\": \"contreras\",\n" +
				"    \"secondLastName\": \"muñiz\",\n" +
				"    \"age\": 32,\n" +
				"    \"sex\": \"H\",\n" +
				"    \"birthDay\": \"23-01-2020\",\n" +
				"    \"role\": \"PROGRAMMER\",\n" +
				"    \"enabled\": true\n" +
				"}";

		mockMvc.perform(post("/v1/employees").content(body).header("Content-Type","application/json"))
				.andExpect(status().isOk());
	}

	@Test
	@Order(5)
	void updateEmployee() throws Exception {

		String body = "{" +
				"    \"firstName\": \"luis\",\n" +
				"    \"middleName\": null,\n" +
				"    \"lastName\": \"contreras\",\n" +
				"    \"secondLastName\": \"muñiz\",\n" +
				"    \"age\": 32,\n" +
				"    \"sex\": \"H\",\n" +
				"    \"birthDay\": \"23-01-2020\",\n" +
				"    \"role\": \"PROGRAMMER\",\n" +
				"    \"enabled\": true\n" +
				"}";

		mockMvc.perform(put("/v1/employees/1").content(body).header("Content-Type","application/json"))
				.andExpect(status().isOk());
	}

}
