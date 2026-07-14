package com.example.attendance.infrastructure.security;

import com.example.attendance.domain.model.Employee;
import com.example.attendance.domain.repository.EmployeeRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final EmployeeRepository employeeRepository;

    public CustomUserDetailsService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String employeeCode) throws UsernameNotFoundException {
        Employee employee = employeeRepository.findByEmployeeCode(employeeCode)
                .filter(Employee::isActive)
                .orElseThrow(() -> new UsernameNotFoundException("社員が見つかりません: " + employeeCode));
        return new CustomUserDetails(employee);
    }
}
