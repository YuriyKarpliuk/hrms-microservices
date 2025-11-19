package org.yuriy.notificationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.yuriy.notificationservice.entity.UserNotification;

import java.util.List;

public interface UserNotificationRepository extends JpaRepository<UserNotification, Long> {
    List<UserNotification> findByEmployeeIdOrderByCreatedAtDesc(Long employeeId);

    List<UserNotification> findByEmployeeIdAndReadFalseOrderByCreatedAtDesc(Long employeeId);
}
