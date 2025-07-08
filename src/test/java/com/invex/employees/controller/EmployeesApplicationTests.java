package com.invex.employees.controller;

import com.invex.employees.domain.Employee;
import com.invex.employees.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
class EmployeesApplicationTests {

	@Autowired
	private EmployeeService<Void, List<Employee>> employeeService;

	@Autowired
	private MockMvc mockMvc;

	@Test
	void getEmployeess() throws Exception {

		mockMvc.perform(get("/v1/employees"))
				.andExpect(status().isOk());
	}

}
