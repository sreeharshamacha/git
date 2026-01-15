# Goal: Implement Application Onboarding CRUD API

Create a complete CRUD module for application onboarding, including entity, repository, service, and controller, following the specified database design and audit requirements.

## Proposed Changes

### [Notification-Management-Service]

#### [NEW] [Application.java](file:///c:/WorkspaceCCLM/Notification-Management-Service/src/main/java/com/notification/management/entity/Application.java)
- Define JPA entity with fields: `id` (UUID), `name`, `code`, `department`, `owner`, `email`, `isDelete`, `status`.
- Include audit columns: `createdBy`, `updatedBy`, `createdAt`, `updatedAt`.
- Use JPA annotations for constraints (not null, unique).

#### [NEW] [ApplicationRepository.java](file:///c:/WorkspaceCCLM/Notification-Management-Service/src/main/java/com/notification/management/repository/ApplicationRepository.java)
- Extend `JpaRepository<Application, UUID>`.

#### [NEW] [ApplicationOnBoardingService.java](file:///c:/WorkspaceCCLM/Notification-Management-Service/src/main/java/com/notification/management/service/ApplicationOnBoardingService.java)
- Implement business logic for CRUD operations:
    - Create: Generate UUID, handle audit defaults.
    - Read: Fetch by ID or list all (filtering out deleted ones).
    - Update: Update fields and audit info.
    - Delete: Implement soft delete by setting `isDelete`.

#### [NEW] [ApplicationOnBoardingController.java](file:///c:/WorkspaceCCLM/Notification-Management-Service/src/main/java/com/notification/management/controller/ApplicationOnBoardingController.java)
- Implement REST endpoints:
    - `POST /api/v1/onboarding/add`: Register new application.
    - `GET /api/v1/onboarding/list`: List all active applications.
    - `GET /api/v1/onboarding/get/{id}`: Get application by ID.
    - `POST /api/v1/onboarding/editOnboarding`: Update application details.
    - `POST /api/v1/onboarding/deleteOnboarding`: Soft delete an application.
- Integrate `Optional<Tracer>` for observability.
- Use `ApiResponse` for consistent responses.

#### [NEW] [ApplicationRequest.java](file:///c:/WorkspaceCCLM/Notification-Management-Service/src/main/java/com/notification/management/dto/ApplicationRequest.java)
- DTO for incoming onboarding requests with validation annotations.

## Verification Plan

### Automated Tests
- Create unit/integration tests for CRUD operations.
- Ensure audit columns are populated correctly.

### Manual Verification
- Use Swagger UI (`/swagger-ui.html`) to test all endpoints.
- Verify data persistence in H2 database.
