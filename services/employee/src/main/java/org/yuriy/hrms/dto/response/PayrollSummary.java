package org.yuriy.hrms.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PayrollSummary(LocalDate periodEnd, BigDecimal netSalary, String status) {}
