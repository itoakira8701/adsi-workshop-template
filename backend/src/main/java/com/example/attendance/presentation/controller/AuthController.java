package com.example.attendance.presentation.controller;

import com.example.attendance.domain.model.Employee;
import com.example.attendance.infrastructure.security.CustomUserDetails;
import com.example.attendance.infrastructure.security.JwtTokenProvider;
import com.example.attendance.presentation.dto.EmployeeResponse;
import com.example.attendance.presentation.dto.LoginRequest;
import com.example.attendance.presentation.dto.LoginResponse;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.employeeCode(), request.password())
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String token = tokenProvider.generateToken(userDetails.getUsername());
        Employee employee = userDetails.employee();

        return new LoginResponse(token, EmployeeResponse.from(employee));
    }

    @GetMapping("/me")
    public EmployeeResponse me(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return EmployeeResponse.from(userDetails.employee());
    }
}
