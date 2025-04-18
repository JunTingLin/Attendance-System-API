package com.tsmc.cloudnative.attendancesystemapi.service;

import com.tsmc.cloudnative.attendancesystemapi.dto.LeaveApplicationListDTO;
import com.tsmc.cloudnative.attendancesystemapi.dto.LeaveApplicationResponseDTO;
import com.tsmc.cloudnative.attendancesystemapi.entity.Employee;
import com.tsmc.cloudnative.attendancesystemapi.entity.LeaveApplication;
import com.tsmc.cloudnative.attendancesystemapi.repository.LeaveApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class LeaveApplicationService {

    private final LeaveApplicationRepository leaveApplicationRepository;
    private final EmployeeService employeeService;

    public List<LeaveApplicationListDTO> getEmployeeLeaveApplications(String employeeCode) {
        log.debug("開始查詢員工[{}]的所有請假記錄", employeeCode);

        Employee employee = employeeService.findEmployeeByCode(employeeCode);

        List<LeaveApplication> applications = leaveApplicationRepository
                .findByEmployeeEmployeeIdOrderByApplicationDatetimeDesc(employee.getEmployeeId());

        log.debug("員工[{}]共有{}筆請假記錄", employeeCode, applications.size());

        // 轉換為DTO
        return applications.stream()
                .map(this::convertToListDTO)
                .collect(Collectors.toList());
    }


    public LeaveApplicationResponseDTO getEmployeeLeaveApplicationDetail(String employeeCode, Integer applicationId) {
        log.debug("開始查詢員工[{}]的請假記錄[{}]詳情", employeeCode, applicationId);

        Employee employee = employeeService.findEmployeeByCode(employeeCode);

        LeaveApplication application = leaveApplicationRepository
                .findByApplicationIdAndEmployeeEmployeeId(applicationId, employee.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("請假記錄不存在或無權限查看"));

        log.debug("成功查詢到員工[{}]的請假記錄[{}]", employeeCode, applicationId);

        // 轉換為DTO
        return convertToResponseDTO(application);
    }

    public List<LeaveApplicationListDTO> getManagerSubordinatesLeaveApplications(String managerCode) {
        log.debug("開始查詢主管[{}]底下所有員工的請假記錄", managerCode);

        // 獲取主管資訊
        Employee manager = employeeService.findEmployeeByCode(managerCode);

        // 查詢主管底下所有員工的申請，使用自訂方法
        List<LeaveApplication> applications = leaveApplicationRepository
                .findByEmployeeSupervisorEmployeeIdOrderByApplicationDatetimeDesc(manager.getEmployeeId());

        log.debug("主管[{}]底下的員工共有{}筆請假記錄", managerCode, applications.size());

        // 轉換為DTO
        return applications.stream()
                .map(this::convertToListDTO)
                .collect(Collectors.toList());
    }

    public LeaveApplicationResponseDTO getManagerLeaveApplicationDetail(String managerCode, Integer applicationId) {
        log.debug("開始查詢主管[{}]權限下的請假記錄[{}]詳情", managerCode, applicationId);

        // 獲取主管資訊
        Employee manager = employeeService.findEmployeeByCode(managerCode);

        // 查詢請假記錄，確保是主管底下員工的申請
        LeaveApplication application = leaveApplicationRepository
                .findByApplicationIdAndEmployeeSupervisorEmployeeId(applicationId, manager.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("請假記錄不存在或您無權限查看"));

        log.debug("成功查詢到主管[{}]底下的請假記錄[{}]", managerCode, applicationId);

        // 轉換為DTO
        return convertToResponseDTO(application);
    }


    private LeaveApplicationListDTO convertToListDTO(LeaveApplication application) {
        return new LeaveApplicationListDTO(
                application.getApplicationId(),
                application.getLeaveType().getLeaveTypeName(),
                application.getStartDatetime(),
                application.getEndDatetime(),
                application.getLeaveHours(),
                application.getStatus(),
                application.getApplicationDatetime()
        );
    }


    private LeaveApplicationResponseDTO convertToResponseDTO(LeaveApplication application) {
        String fileUrl = null;
        if (application.getFileName() != null && !application.getFileName().isEmpty()) {
            fileUrl = application.getFilePath();
        }

        return new LeaveApplicationResponseDTO(
                application.getApplicationId(),
                application.getEmployee().getEmployeeId(),
                application.getEmployee().getEmployeeName(),
                application.getLeaveType().getLeaveTypeId(),
                application.getLeaveType().getLeaveTypeName(),
                application.getStartDatetime(),
                application.getEndDatetime(),
                application.getLeaveHours(),
                application.getReason(),
                application.getProxyEmployee().getEmployeeId(),
                application.getProxyEmployee().getEmployeeName(),
                application.getStatus(),
                application.getApplicationDatetime(),
                application.getApproverEmployee() != null ? application.getApproverEmployee().getEmployeeId() : null,
                application.getApproverEmployee() != null ? application.getApproverEmployee().getEmployeeName() : null,
                application.getApprovalReason() != null ? application.getApprovalReason() : null,
                fileUrl,
                application.getFileName() != null ? application.getFileName() : null
        );
    }

}
