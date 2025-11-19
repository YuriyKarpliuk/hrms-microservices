package org.yuriy.notificationservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.yuriy.notificationservice.dto.response.EmployeeBasicResponse;
import org.yuriy.notificationservice.dto.response.OrganizationResponse;
import org.yuriy.notificationservice.entity.UserNotification;
import org.yuriy.notificationservice.repository.UserNotificationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BirthdayNotificationService {

    private final EmployeeClient employeeClient;
    private final UserNotificationRepository userNotificationRepository;
    private final WebSocketNotificationService wsService;
    private final OrganizationClient organizationClient;

    public void sendBirthdayNotifications() {

        List<Long> organizationIds =
                organizationClient.getAllOrganizations().stream()
                        .map(OrganizationResponse::id)
                        .toList();

        for (Long orgId : organizationIds) {

            List<EmployeeBasicResponse> birthdays =
                    employeeClient.getTodayBirthdaysForOrg(orgId);

            if (birthdays.isEmpty())
                continue;

            List<EmployeeBasicResponse> employees =
                    employeeClient.getAllEmployeesOfOrg(orgId);

            for (EmployeeBasicResponse birthdayPerson : birthdays) {

                String fullName = birthdayPerson.firstName() + " " + birthdayPerson.lastName();

                String message = "  Today is " + fullName + "'s birthday! Make sure to congratulate them.";

                for (EmployeeBasicResponse target : employees) {

                    UserNotification notif = userNotificationRepository.save(
                            UserNotification.builder()
                                    .employeeId(target.id())
                                    .title(" Birthday in your organization")
                                    .message(message)
                                    .read(false)
                                    .build()
                    );

                    wsService.sendToUser(target.id(), notif);
                }
            }
        }
    }

}
