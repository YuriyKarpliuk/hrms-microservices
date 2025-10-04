package org.yuriy.notificationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.yuriy.notificationservice.entity.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
