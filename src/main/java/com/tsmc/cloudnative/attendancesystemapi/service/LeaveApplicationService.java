package com.tsmc.cloudnative.attendancesystemapi.service;

import com.tsmc.cloudnative.attendancesystemapi.dto.LeaveApplicationListDTO;
import com.tsmc.cloudnative.attendancesystemapi.dto.LeaveApplicationRequestDTO;
import com.tsmc.cloudnative.attendancesystemapi.dto.LeaveApplicationResponseDTO;
import com.tsmc.cloudnative.attendancesystemapi.entity.Employee;
import com.tsmc.cloudnative.attendancesystemapi.entity.EmployeeLeaveBalance;
import com.tsmc.cloudnative.attendancesystemapi.entity.LeaveApplication;
import com.tsmc.cloudnative.attendancesystemapi.entity.LeaveType;
import com.tsmc.cloudnative.attendancesystemapi.repository.EmployeeRepository;
import com.tsmc.cloudnative.attendancesystemapi.repository.LeaveApplicationRepository;
import com.tsmc.cloudnative.attendancesystemapi.repository.LeaveTypeRepository;
import com.tsmc.cloudnative.attendancesystemapi.repository.EmployeeLeaveBalanceRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class LeaveApplicationService {

    private final LeaveApplicationRepository leaveApplicationRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final EmployeeLeaveBalanceRepository employeeLeaveBalanceRepository;
    private final EmployeeService employeeService;
    private final NotificationService notificationService;

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
                application.getApplicationDatetime(),
                application.getReason(),
                application.getApprovalReason(),
                application.getProxyEmployee().getEmployeeCode(),
                application.getProxyEmployee().getEmployeeName(),
                application.getApprovalDatetime(),
                application.getFilePath(),
                application.getFileName()
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
                application.getProxyEmployee().getEmployeeCode(),
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

    private void validateLeaveApplicationRequest(Employee employee, LeaveApplicationRequestDTO dto) {
        // 驗證假別是否存在
        LeaveType leaveType = leaveTypeRepository.findById(dto.getLeaveTypeId())
                .orElseThrow(() -> new IllegalArgumentException("無效的假別ID"));

        // 驗證是否需要上傳附件
        if (Boolean.TRUE.equals(leaveType.getAttachmentRequired())) {
            if (dto.getFilePath() == null || dto.getFilePath().isBlank()) {
                throw new IllegalArgumentException("此假別需上傳附件");
            }
        }

        // 驗證開始時間需早於結束時間
        if (!dto.getStartDateTime().isBefore(dto.getEndDateTime())) {
            throw new IllegalArgumentException("開始時間需早於結束時間");
        }

        // 檢查請假時數是否超過餘額
        int currentYear = LocalDateTime.now().getYear();
        EmployeeLeaveBalance balance = employeeLeaveBalanceRepository
                .findByEmployeeEmployeeIdAndLeaveTypeLeaveTypeIdAndLeaveYear(
                        employee.getEmployeeId(), dto.getLeaveTypeId(), currentYear)
                .orElseThrow(() -> new IllegalArgumentException("查無該假別的請假餘額"));

        if (dto.getLeaveHours() > balance.getRemainingHours()) {
            throw new IllegalArgumentException("請假時數超過可用餘額");
        }
    }

    private void applyLeaveValues(LeaveApplication application,
                                  Employee employee,
                                  LeaveApplicationRequestDTO dto,
                                  boolean isNewApplication) {
        LeaveType leaveType = leaveTypeRepository.findById(dto.getLeaveTypeId())
                .orElseThrow(() -> new IllegalArgumentException("無效的假別ID"));

        application.setEmployee(employee);
        application.setLeaveType(leaveType);
        application.setStartDatetime(dto.getStartDateTime());
        application.setEndDatetime(dto.getEndDateTime());
        application.setLeaveHours(dto.getLeaveHours());
        application.setReason(dto.getReason());
        application.setFilePath(dto.getFilePath());
        application.setFileName(dto.getFileName());

        if (isNewApplication) {
            application.setApplicationDatetime(LocalDateTime.now());
            application.setStatus("待審核");
        } else {
            application.setStatus(dto.getStatus()); // 更新才允許更改狀態
        }

        // 取得代理人
        if (dto.getProxyEmployeeCode() == null || dto.getProxyEmployeeCode().isBlank()) {
            throw new IllegalArgumentException("請填寫代理人");
        } else {
            Employee proxy = employeeService.findEmployeeByCode(dto.getProxyEmployeeCode());
            application.setProxyEmployee(proxy);
        }
    }



    public LeaveApplicationResponseDTO  applyLeave(String employeeCode, LeaveApplicationRequestDTO requestDTO){
        log.debug("員工[{}]新增請假紀錄", employeeCode);
        Employee employee = employeeService.findEmployeeByCode(employeeCode);

        validateLeaveApplicationRequest(employee, requestDTO);

        LeaveApplication application = new LeaveApplication();
        applyLeaveValues(application, employee, requestDTO, true);

        LeaveApplication saved = leaveApplicationRepository.save(application);
        return convertToResponseDTO(saved);

    }

    public LeaveApplicationResponseDTO updateEmployeeLeaveApplication(
            String employeeCode, Integer applicationId, LeaveApplicationRequestDTO updateDTO) {

        log.debug("員工[{}]請求更新請假紀錄[{}]", employeeCode, applicationId);

        // 驗證員工身分
        Employee employee = employeeService.findEmployeeByCode(employeeCode);

        // 查詢請假紀錄是否存在並屬於該員工
        LeaveApplication application = leaveApplicationRepository
                .findByApplicationIdAndEmployeeEmployeeId(applicationId, employee.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("請假記錄不存在或無權限修改"));

        // 僅允許修改尚未審核的紀錄
        if (!"待審核".equalsIgnoreCase(application.getStatus())) {
            throw new IllegalStateException("該筆請假記錄已被審核，無法修改");
        }

        validateLeaveApplicationRequest(employee, updateDTO);
        applyLeaveValues(application, employee, updateDTO, false);

        // 儲存更新後的資料
        leaveApplicationRepository.save(application);

        log.debug("員工[{}]的請假記錄[{}]更新成功", employeeCode, applicationId);

        return convertToResponseDTO(application);
    }

    public LeaveApplicationResponseDTO approveLeaveApplication(Integer leaveId, String approvalReason) {
        LeaveApplicationResponseDTO responseDTO = updateLeaveApplicationStatus(leaveId, "已核准", approvalReason);
        // 自動發送通知
        try {
            notificationService.notifyLeaveStatus(leaveId);
            log.info("請假核准通知已發送，ID: {}", leaveId);
        } catch (Exception e) {
            log.warn("請假核准通知發送失敗，ID: {}，原因: {}", leaveId, e.getMessage());
            // 不影響核准流程，所以只記錄不拋出
        }
        return responseDTO;
    }

    public LeaveApplicationResponseDTO rejectLeaveApplication(Integer leaveId, String approvalReason) {
        LeaveApplicationResponseDTO responseDTO = updateLeaveApplicationStatus(leaveId, "已駁回", approvalReason);
        // 自動發送通知
        try {
            notificationService.notifyLeaveStatus(leaveId);
            log.info("請假駁回通知已發送，ID: {}", leaveId);
        } catch (Exception e) {
            log.warn("請假駁回通知發送失敗，ID: {}，原因: {}", leaveId, e.getMessage());
            // 不影響駁回流程，所以只記錄不拋出
        }
        return responseDTO;
    }

    private LeaveApplicationResponseDTO updateLeaveApplicationStatus(Integer leaveId, String status, String approvalReason) {
        LeaveApplication leaveApplication = leaveApplicationRepository.findById(leaveId)
                .orElseThrow(() -> {
                    log.error("沒有找到請假申請：{} ", leaveId);
                    return new IllegalArgumentException("沒有找到請假申請");
                });

        leaveApplication.setStatus(status);
        leaveApplication.setApprovalReason(approvalReason);
        leaveApplication.setApprovalDatetime(LocalDateTime.now());

        LeaveApplication updatedLeaveApplication = leaveApplicationRepository.save(leaveApplication);
        log.info("請假申請：{} 狀態已更新為 {}", leaveId, status);

        return convertToResponseDTO(updatedLeaveApplication);
    }

}
