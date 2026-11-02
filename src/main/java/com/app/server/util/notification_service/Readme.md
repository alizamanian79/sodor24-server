How Use it

notificationService.notify(new NotificationRequest(
NotificationType.EMAIL,
"user@example.com",
"Policy issued",
"Your policy has been issued successfully."
));

notificationService.notify(new NotificationRequest(
NotificationType.SMS,
"09120000000",
null,
"Your verification code is 12345"
));