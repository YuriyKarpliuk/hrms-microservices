package org.yuriy.payrollservice.dto.mapper;

import org.springframework.stereotype.Component;
import org.yuriy.payrollservice.dto.request.PayrollCreateRequest;
import org.yuriy.payrollservice.dto.response.EmployeeBasicResponse;
import org.yuriy.payrollservice.dto.response.PayrollResponse;
import org.yuriy.payrollservice.dto.response.PayrollWithEmployeeResponse;
import org.yuriy.payrollservice.entity.Payroll;
import org.yuriy.payrollservice.entity.PayrollStatus;

import java.math.BigDecimal;


@Component
public class PayrollMapper {

    public Payroll toEntity(PayrollCreateRequest r) {
        BigDecimal bonus = r.bonus() != null ? r.bonus() : BigDecimal.ZERO;
        BigDecimal deductions = r.deductions() != null ? r.deductions() : BigDecimal.ZERO;
        BigDecimal net = r.baseSalary().add(bonus).subtract(deductions);

        return Payroll.builder().employeeId(r.employeeId()).periodStart(r.periodStart()).periodEnd(r.periodEnd())
                .baseSalary(r.baseSalary()).bonus(bonus).deductions(deductions).netSalary(net)
                .status(PayrollStatus.PENDING).build();
    }

    public PayrollResponse toResponse(Payroll p) {
        return new PayrollResponse(p.getId(), p.getEmployeeId(), p.getPeriodStart(), p.getPeriodEnd(),
                p.getBaseSalary(), p.getBonus(), p.getDeductions(), p.getNetSalary(), p.getStatus());
    }

    public PayrollWithEmployeeResponse toWithEmployeeResponse(Payroll payroll, EmployeeBasicResponse emp) {
        return new PayrollWithEmployeeResponse(
                payroll.getId(),
                payroll.getEmployeeId(),
                emp.firstName(),
                emp.lastName(),
                emp.position(),
                emp.email(),
                payroll.getPeriodStart(),
                payroll.getPeriodEnd(),
                payroll.getBaseSalary(),
                payroll.getBonus(),
                payroll.getDeductions(),
                payroll.getNetSalary(),
                payroll.getStatus()
        );
    }
}
