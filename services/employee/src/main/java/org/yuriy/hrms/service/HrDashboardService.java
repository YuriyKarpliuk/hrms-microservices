package org.yuriy.hrms.service;

import org.yuriy.hrms.dto.response.HrDashboardResponse;

public interface HrDashboardService {
    HrDashboardResponse getDashboard(Long hrId);
}
