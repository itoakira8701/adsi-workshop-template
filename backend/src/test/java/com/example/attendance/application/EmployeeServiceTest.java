package com.example.attendance.application;

import com.example.attendance.domain.model.Employee;
import com.example.attendance.domain.model.Role;
import com.example.attendance.domain.repository.EmployeeRepository;
import com.example.attendance.presentation.dto.CreateEmployeeRequest;
import com.example.attendance.presentation.dto.UpdateEmployeeRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        employeeService = new EmployeeServiceImpl(employeeRepository, passwordEncoder);
    }

    @Test
    @DisplayName("create: パスワードがBCryptハッシュ化されて保存される")
    void create_validRequest_hashesPassword() {
        var request = new CreateEmployeeRequest("emp001", "田中太郎", "tanaka@example.com", "password123", "EMPLOYEE");
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$hashedvalue");
        when(employeeRepository.findByEmployeeCode("emp001")).thenReturn(Optional.empty());
        when(employeeRepository.findByEmail("tanaka@example.com")).thenReturn(Optional.empty());
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> {
            Employee e = invocation.getArgument(0);
            e.setId(1L);
            return e;
        });

        Employee result = employeeService.create(request);

        assertThat(result.getPassword()).isEqualTo("$2a$10$hashedvalue");
        assertThat(result.getEmployeeCode()).isEqualTo("emp001");
        assertThat(result.getRole()).isEqualTo(Role.EMPLOYEE);
    }

    @Test
    @DisplayName("create: 社員コード重複時にIllegalArgumentException")
    void create_duplicateCode_throwsException() {
        var request = new CreateEmployeeRequest("emp001", "田中太郎", "tanaka@example.com", "password123", "EMPLOYEE");
        when(employeeRepository.findByEmployeeCode("emp001")).thenReturn(Optional.of(new Employee()));

        assertThatThrownBy(() -> employeeService.create(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("社員コード");
    }

    @Test
    @DisplayName("create: メール重複時にIllegalArgumentException")
    void create_duplicateEmail_throwsException() {
        var request = new CreateEmployeeRequest("emp002", "佐藤花子", "tanaka@example.com", "password123", "EMPLOYEE");
        when(employeeRepository.findByEmployeeCode("emp002")).thenReturn(Optional.empty());
        when(employeeRepository.findByEmail("tanaka@example.com")).thenReturn(Optional.of(new Employee()));

        assertThatThrownBy(() -> employeeService.create(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("メールアドレス");
    }

    @Test
    @DisplayName("update: version不一致時にIllegalStateException")
    void update_versionMismatch_throwsException() {
        var existing = Employee.builder().id(1L).name("田中").email("t@ex.com").role(Role.EMPLOYEE).version(2L).build();
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(existing));

        var request = new UpdateEmployeeRequest("田中更新", "t@ex.com", "EMPLOYEE", 1L);

        assertThatThrownBy(() -> employeeService.update(1L, request))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("更新");
    }

    @Test
    @DisplayName("delete: active=falseに更新される")
    void delete_existingEmployee_setsInactive() {
        var existing = Employee.builder().id(1L).active(true).build();
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(employeeRepository.save(any(Employee.class))).thenAnswer(i -> i.getArgument(0));

        employeeService.delete(1L);

        verify(employeeRepository).save(any(Employee.class));
        assertThat(existing.isActive()).isFalse();
    }
}
