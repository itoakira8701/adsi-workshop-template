package com.example.attendance.application;

import com.example.attendance.domain.exception.ResourceNotFoundException;
import com.example.attendance.domain.model.Employee;
import com.example.attendance.domain.model.Role;
import com.example.attendance.domain.repository.EmployeeRepository;
import com.example.attendance.presentation.dto.CreateEmployeeRequest;
import com.example.attendance.presentation.dto.UpdateEmployeeRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Employee> getAll() {
        return employeeRepository.findByActiveTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public Employee getById(Long id) {
        return employeeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("社員が見つかりません: id=" + id));
    }

    @Override
    public Employee create(CreateEmployeeRequest request) {
        if (employeeRepository.findByEmployeeCode(request.employeeCode()).isPresent()) {
            throw new IllegalArgumentException("社員コードが既に使用されています: " + request.employeeCode());
        }
        if (employeeRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("メールアドレスが既に使用されています: " + request.email());
        }

        Employee employee = Employee.builder()
            .employeeCode(request.employeeCode())
            .name(request.name())
            .email(request.email())
            .password(passwordEncoder.encode(request.password()))
            .role(Role.valueOf(request.role()))
            .active(true)
            .build();

        return employeeRepository.save(employee);
    }

    @Override
    public Employee update(Long id, UpdateEmployeeRequest request) {
        Employee employee = getById(id);

        if (!employee.getVersion().equals(request.version())) {
            throw new IllegalStateException("他のユーザーによって更新されています。画面を再読み込みしてください。");
        }

        if (!employee.getEmail().equals(request.email())) {
            employeeRepository.findByEmail(request.email())
                .filter(e -> !e.getId().equals(id))
                .ifPresent(e -> {
                    throw new IllegalArgumentException("メールアドレスが既に使用されています: " + request.email());
                });
        }

        employee.setName(request.name());
        employee.setEmail(request.email());
        employee.setRole(Role.valueOf(request.role()));

        return employeeRepository.save(employee);
    }

    @Override
    public void delete(Long id) {
        Employee employee = getById(id);
        employee.setActive(false);
        employeeRepository.save(employee);
    }
}
