package org.yuriy.hrms.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.yuriy.hrms.dto.request.HrTaskRequest;
import org.yuriy.hrms.dto.request.HrTaskStatusUpdateRequest;
import org.yuriy.hrms.dto.response.HrTaskResponse;
import org.yuriy.hrms.service.HrTaskService;

import java.util.List;


@RestController
@RequestMapping("/api/v1/hr/tasks")
@RequiredArgsConstructor
public class HrTaskController {

    private final HrTaskService service;

    @PostMapping("/{hrId}")
    public ResponseEntity<HrTaskResponse> create(
            @PathVariable Long hrId,
            @RequestBody HrTaskRequest req
    ) {
        return ResponseEntity.ok(service.createTask(hrId, req));
    }

    @GetMapping("/mine/{hrId}")
    public ResponseEntity<List<HrTaskResponse>> myTasks(
            @PathVariable Long hrId
    ) {
        return ResponseEntity.ok(service.getTasksForHr(hrId));
    }

    @GetMapping("/employee/{id}")
    public ResponseEntity<List<HrTaskResponse>> tasksForEmployee(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(service.getTasksForEmployee(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HrTaskResponse> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping("/{id}/done")
    public ResponseEntity<HrTaskResponse> complete(@PathVariable Long id) {
        return ResponseEntity.ok(service.markCompleted(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<HrTaskResponse> updateStatus(
            @PathVariable Long id,
            @RequestBody HrTaskStatusUpdateRequest req
    ) {
        return ResponseEntity.ok(service.updateStatus(id, req));
    }
}
