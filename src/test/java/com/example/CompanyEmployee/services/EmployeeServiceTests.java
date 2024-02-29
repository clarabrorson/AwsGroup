package com.example.CompanyEmployee.services;

import com.example.CompanyEmployee.models.Company;
import com.example.CompanyEmployee.models.Employee;
import com.example.CompanyEmployee.models.User;
import com.example.CompanyEmployee.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class EmployeeServiceTests {
    @Mock
    private EmployeeRepository employeeRepository;
    private EmployeeService employeeService;
    @Mock
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        employeeService = new EmployeeService(employeeRepository, userService);
    }

    @Test
    void getEmployeeByName() {
        String expectedEmployeeName = "Employee";
        Employee employee = new Employee();
        employee.setFirstName(expectedEmployeeName);
        when(employeeRepository.findByFirstName(expectedEmployeeName)).thenReturn(Optional.of(employee));

        String actualEmployeeName = employeeService.getEmployeeByName(expectedEmployeeName);

        assertEquals(expectedEmployeeName, actualEmployeeName);
    }

    @Test
    void getAllEmployees() {
        // Mocking user with a company
        Company company = new Company();
        company.setId(1L);
        company.setCompanyName("Example Company");

        User user = new User();
        user.setCompany(company); // Set the company directly to the user

        when(userService.getCurrentUser()).thenReturn(user);

        // Mocking behavior of employeeRepository
        Employee employee1 = new Employee();
        employee1.setId(1L);
        employee1.setFirstName("John");
        employee1.setLastName("Doe");
        employee1.setCompany(company); // Set the company for the employee

        // Assuming there are some employees associated with the company
        List<Employee> employees = Collections.singletonList(employee1);
        when(employeeRepository.findByCompanyCompanyName(company.getCompanyName())).thenReturn(employees);

        ResponseEntity<?> response = employeeService.getEmployeesByCurrentCompany();

        assertEquals(ResponseEntity.ok(employees), response);
    }


    @Test
    void addEmployee() {
        Employee employee = new Employee();
        when(employeeRepository.findByFirstName(any())).thenReturn(Optional.empty());

        ResponseEntity<?> response = employeeService.addEmployee(employee);

        assertEquals(ResponseEntity.ok("Employee added successfully"), response);
        verify(employeeRepository, times(1)).save(employee);
    }

    @Test
    void updateEmployee() {
        Long id = 1L;
        Employee employee = new Employee();
        when(employeeRepository.existsById(id)).thenReturn(true);

        ResponseEntity<?> response = employeeService.updateEmployee(id, employee);

        assertEquals(ResponseEntity.ok("Employee updated successfully"), response);
        verify(employeeRepository, times(1)).save(employee);
    }

    @Test
    void deleteEmployee() {
        Long id = 1L;
        when(employeeRepository.existsById(id)).thenReturn(true);

        ResponseEntity<?> response = employeeService.deleteEmployee(id);

        assertEquals(ResponseEntity.ok("Employee deleted successfully"), response);
        verify(employeeRepository, times(1)).deleteById(id);
    }

}
