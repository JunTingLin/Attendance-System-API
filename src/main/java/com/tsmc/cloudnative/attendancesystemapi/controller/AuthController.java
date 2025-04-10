package com.tsmc.cloudnative.attendancesystemapi.controller;

import com.tsmc.cloudnative.attendancesystemapi.common.ApiResponse;
import com.tsmc.cloudnative.attendancesystemapi.dto.LoginRequestDTO;
import com.tsmc.cloudnative.attendancesystemapi.dto.LoginResponseDTO;
import com.tsmc.cloudnative.attendancesystemapi.entity.Employee;
import com.tsmc.cloudnative.attendancesystemapi.security.JwtTokenUtil;
import com.tsmc.cloudnative.attendancesystemapi.service.EmployeeService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private EmployeeService employeeService;



    @PostMapping("/login")
    public ApiResponse<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        try{
            log.info("嘗試登入用戶: {}", loginRequestDTO.getEmployeeCode());

            // 認證
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequestDTO.getEmployeeCode(), loginRequestDTO.getPassword())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtTokenUtil.generateToken(userDetails);

            Employee employee = employeeService.findEmployeeByCode(loginRequestDTO.getEmployeeCode());

            LoginResponseDTO loginResponseDTO = new LoginResponseDTO(
                    token,
                    employee.getEmployeeName(),
                    employee.getEmployeeCode()
            );

            return ApiResponse.success("登入成功", loginResponseDTO);
        }catch (Exception e){
            log.error("登入失敗", e);
            return ApiResponse.error(401, "登入失敗: " + (e.getMessage() != null ? e.getMessage() : "認證服務異常"));
        }
    }
}