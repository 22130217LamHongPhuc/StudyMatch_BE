$basePath = "D:\StudyMatch_BE"

$services = @(
    "eureka_server",
    "api-gateway"
    "user_service",
    "profile_service",
#     "social_service",
#     "group_service",
    "chat_service"
#     "ai_service"
)


foreach ($service in $services) {
    Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$basePath\$service'; ./mvnw spring-boot:run"
}