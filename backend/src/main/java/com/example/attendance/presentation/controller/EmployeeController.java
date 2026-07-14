package com.example.attendance.presentation.controller;

import com.example.attendance.application.EmployeeService;
import com.example.attendance.domain.model.Employee;
import com.example.attendance.presentation.dto.CreateEmployeeRequest;
import com.example.attendance.presentation.dto.EmployeeResponse;
import com.example.attendance.presentation.dto.UpdateEmployeeRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
@PreAuthorize("hasRole('HR')")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public List<EmployeeResponse> getAll() {
        return employeeService.getAll().stream()
            .map(EmployeeResponse::from)
            .toList();
    }

    @GetMapping("/{id}")
    public EmployeeResponse getById(@PathVariable Long id) {
        return EmployeeResponse.from(employeeService.getById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeResponse create(@Valid @RequestBody CreateEmployeeRequest request) {
        Employee employee = employeeService.create(request);
        return EmployeeResponse.from(employee);
    }

    @PutMapping("/{id}")
    public EmployeeResponse update(@PathVariable Long id, @Valid @RequestBody UpdateEmployeeRequest request) {
        Employee employee = employeeService.update(id, request);
        return EmployeeResponse.from(employee);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        employeeService.delete(id);
    }
}
