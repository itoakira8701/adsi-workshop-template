package com.example.attendance.application;

import com.example.attendance.domain.model.Employee;
import com.example.attendance.presentation.dto.CreateEmployeeRequest;
import com.example.attendance.presentation.dto.UpdateEmployeeRequest;

import java.util.List;

public interface EmployeeService {

    List<Employee> getAll();

    Employee getById(Long id);

    Employee create(CreateEmployeeRequest request);

    Employee update(Long id, UpdateEmployeeRequest request);

    void delete(Long id);
}
