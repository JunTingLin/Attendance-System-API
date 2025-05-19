package com.tsmc.cloudnative.attendancesystemapi.service;

import com.tsmc.cloudnative.attendancesystemapi.dto.LoginRequestDTO;
import com.tsmc.cloudnative.attendancesystemapi.dto.LoginResponseDTO;
import com.tsmc.cloudnative.attendancesystemapi.entity.Employee;
import com.tsmc.cloudnative.attendancesystemapi.security.JwtTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private EmployeeService employeeService;

    private LoginRequestDTO loginRequest;
    private Employee employee;
    private UserDetails userDetails;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequestDTO();
        loginRequest.setEmployeeCode("EMP001");
        loginRequest.setPassword("password");

        employee = new Employee();
        employee.setEmployeeCode("EMP001");
        employee.setEmployeeName("Alice");

        userDetails = mock(UserDetails.class);
        authentication = mock(Authentication.class);
    }

    @Test
    void authenticateAndGenerateToken_success() {
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtTokenUtil.generateToken(userDetails)).thenReturn("jwt-token");
        when(userDetails.getUsername()).thenReturn("EMP001");
        when(employeeService.findEmployeeByCode("EMP001")).thenReturn(employee);

        LoginResponseDTO result = authService.authenticateAndGenerateToken(loginRequest);

        assertNotNull(result);
        assertEquals("jwt-token", result.getToken());
        assertEquals("Alice", result.getEmployeeName());
        assertEquals("EMP001", result.getEmployeeCode());

        // 驗證傳入正確帳號密碼
        ArgumentCaptor<UsernamePasswordAuthenticationToken> captor =
                ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager).authenticate(captor.capture());
        assertEquals("EMP001", captor.getValue().getPrincipal());
        assertEquals("password", captor.getValue().getCredentials());
    }

    @Test
    void authenticateAndGenerateToken_invalidCredentials_throwsException() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Invalid"));

        assertThrows(BadCredentialsException.class, () ->
                authService.authenticateAndGenerateToken(loginRequest));

        verify(authenticationManager).authenticate(any());
        verifyNoInteractions(jwtTokenUtil);
        verifyNoInteractions(employeeService);
    }

    @Test
    void authenticateAndGenerateToken_employeeNotFound_throwsException() {
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtTokenUtil.generateToken(userDetails)).thenReturn("jwt-token");
        when(employeeService.findEmployeeByCode("EMP001"))
                .thenThrow(new NullPointerException("Employee not found"));

        assertThrows(Exception.class, () ->
                authService.authenticateAndGenerateToken(loginRequest));
    }

    @Test
    void getAuthenticatedUserInfo_success() {
        when(userDetails.getUsername()).thenReturn("EMP001");
        when(userDetails.getUsername()).thenReturn("EMP001");
        when(userDetails.getUsername()).thenReturn("EMP001");
        when(employeeService.findEmployeeByCode("EMP001")).thenReturn(employee);

        LoginResponseDTO result = authService.getAuthenticatedUserInfo(userDetails);

        assertNotNull(result);
        assertNull(result.getToken());
        assertEquals("Alice", result.getEmployeeName());
        assertEquals("EMP001", result.getEmployeeCode());

        verify(employeeService).findEmployeeByCode("EMP001");
    }

    @Test
    void getAuthenticatedUserInfo_employeeNotFound_throwsException() {
        when(userDetails.getUsername()).thenReturn("EMP001");
        when(userDetails.getUsername()).thenReturn("EMP001");
        when(employeeService.findEmployeeByCode("EMP001"))
                .thenThrow(new NullPointerException("Employee not found"));

        assertThrows(Exception.class, () ->
                authService.getAuthenticatedUserInfo(userDetails));
    }
}
