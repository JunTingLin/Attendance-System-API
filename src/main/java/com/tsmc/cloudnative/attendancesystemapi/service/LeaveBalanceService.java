package com.tsmc.cloudnative.attendancesystemapi.service;


import com.tsmc.cloudnative.attendancesystemapi.dto.LeaveBalanceDTO;
import com.tsmc.cloudnative.attendancesystemapi.entity.Employee;
import com.tsmc.cloudnative.attendancesystemapi.entity.EmployeeLeaveBalance;
import com.tsmc.cloudnative.attendancesystemapi.repository.EmployeeLeaveBalanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class LeaveBalanceService {

    private final EmployeeLeaveBalanceRepository employeeLeaveBalanceRepository;
    private final EmployeeService employeeService;


    public List<LeaveBalanceDTO> getEmployeeLeaveBalances(String employeeCode) {
        return getEmployeeLeaveBalancesForYear(employeeCode, LocalDate.now().getYear());
    }


    public List<LeaveBalanceDTO> getEmployeeLeaveBalancesForYear(String employeeCode, Integer year) {
        log.debug("開始查詢員工[{}]在{}年的所有請假餘額", employeeCode, year);


        Employee employee = employeeService.findEmployeeByCode(employeeCode);

        List<EmployeeLeaveBalance> balances = employeeLeaveBalanceRepository
                .findByEmployeeEmployeeIdAndLeaveYearOrderByLeaveTypeLeaveTypeId(employee.getEmployeeId(), year);

        log.debug("員工[{}]在{}年共有{}種請假類型的餘額記錄", employeeCode, year, balances.size());

        // 轉換為DTO
        return balances.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    private LeaveBalanceDTO convertToDTO(EmployeeLeaveBalance balance) {
        return new LeaveBalanceDTO(
                balance.getBalanceId(),
                balance.getEmployee().getEmployeeId(),
                balance.getEmployee().getEmployeeName(),
                balance.getLeaveType().getLeaveTypeId(),
                balance.getLeaveType().getLeaveTypeName(),
                balance.getLeaveYear(),
                balance.getTotalHours(),
                balance.getUsedHours(),
                balance.getRemainingHours()
        );
    }
}
