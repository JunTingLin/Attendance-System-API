package com.tsmc.cloudnative.attendancesystemapi.service;

import com.tsmc.cloudnative.attendancesystemapi.dto.LoginRequestDTO;
import com.tsmc.cloudnative.attendancesystemapi.dto.LoginResponseDTO;
import com.tsmc.cloudnative.attendancesystemapi.entity.Employee;
import com.tsmc.cloudnative.attendancesystemapi.security.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final EmployeeService employeeService;

    public LoginResponseDTO authenticateAndGenerateToken(LoginRequestDTO loginRequestDTO) {
        Authentication authResult = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDTO.getEmployeeCode(), loginRequestDTO.getPassword())
        );

        UserDetails userDetails = (UserDetails) authResult.getPrincipal();
        String token = jwtTokenUtil.generateToken(userDetails);
        Employee employee = employeeService.findEmployeeByCode(userDetails.getUsername());

        return new LoginResponseDTO(token, employee.getEmployeeName(), employee.getEmployeeCode());
    }

    public LoginResponseDTO getAuthenticatedUserInfo(UserDetails userDetails) {

        Employee employee = employeeService.findEmployeeByCode(userDetails.getUsername());

        return new LoginResponseDTO(
                null,
                employee.getEmployeeName(),
                employee.getEmployeeCode()
        );
    }


}
