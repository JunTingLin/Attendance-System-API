package com.tsmc.cloudnative.attendancesystemapi.security;

import com.tsmc.cloudnative.attendancesystemapi.entity.Employee;
import com.tsmc.cloudnative.attendancesystemapi.entity.EmployeeRole;
import com.tsmc.cloudnative.attendancesystemapi.repository.EmployeeRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public UserDetails loadUserByUsername(String employeeCode) throws UsernameNotFoundException {
        try{
            Employee employee = employeeRepository.findByEmployeeCode(employeeCode)
                    .orElseThrow(() -> new UsernameNotFoundException("員工編號不存在: " + employeeCode));

            log.info("找到員工: ID={}, 姓名={}", employee.getEmployeeId(), employee.getEmployeeName());

            List<SimpleGrantedAuthority> authorities = employee.getEmployeeRoles().stream()
                    .map(EmployeeRole::getRole)
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                    .collect(Collectors.toList());

            return new User(employee.getEmployeeCode(), employee.getPassword(), authorities);
        }catch (Exception e){
            log.error("加載用戶時發生錯誤", e);
            throw e;
        }

    }
}