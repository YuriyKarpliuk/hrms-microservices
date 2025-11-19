package org.yuriy.hrms.service;

import org.yuriy.hrms.dto.request.HrTaskRequest;
import org.yuriy.hrms.dto.response.HrTaskResponse;

import java.util.List;

import org.yuriy.hrms.dto.request.HrTaskStatusUpdateRequest;

public interface HrTaskService {

     HrTaskResponse createTask(Long createdBy, HrTaskRequest req);

     List<HrTaskResponse> getTasksForHr(Long hrId);

     List<HrTaskResponse> getTasksForEmployee(Long empId);

     HrTaskResponse markCompleted(Long id);

     HrTaskResponse updateStatus(Long id, HrTaskStatusUpdateRequest req);

     HrTaskResponse getById(Long id);
}

