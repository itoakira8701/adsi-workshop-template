package com.example.attendance.domain.repository;

import com.example.attendance.domain.model.Employee;
import com.example.attendance.domain.model.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    @DisplayName("findByEmployeeCode: シードデータの管理者が取得できる")
    void findByEmployeeCode_existingCode_returnsEmployee() {
        Optional<Employee> result = employeeRepository.findByEmployeeCode("admin");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("管理者");
        assertThat(result.get().getRole()).isEqualTo(Role.HR);
    }

    @Test
    @DisplayName("findByEmployeeCode: 存在しないコードはempty")
    void findByEmployeeCode_nonExisting_returnsEmpty() {
        Optional<Employee> result = employeeRepository.findByEmployeeCode("not-exist");

        assertThat(result).isEmpty();
    }
}
