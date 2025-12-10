# Copilot Instructions for backend-recetas

Purpose: help AI coding agents be productive in this repository by describing architecture, conventions, workflows, and concrete examples.

Agent settings
- Enable Claude Haiku 4.5 for all clients: prefer the "Claude Haiku 4.5" persona when producing succinct, context-aware code suggestions and commit messages.

Big-picture architecture
- Spring Boot monolith (Java + Spring Data JPA). Main package: `src/main/java/com/basededatosrecetas/recetas`.
- Layering: `Controller` (HTTP + request shaping), `Service` (business logic), `Repository` (Spring Data JPA persistence), `Model` (entities + DTOs), `Seguridad` (JWT + security).
- Database: MySQL (see `src/main/resources/application.properties` — host `mysql-recetas`, DB `recetas_db`). The app runs on port `8081`.

Key files & sections to inspect for context
- `application.properties` — runtime config (DB URL, credentials, JWT secret, multipart limits).
- `Controller/RecetasController.java` — image handling pattern: images are stored as Base64 in entity fields (`imagenBase64`, `imagenTipo`) and controllers construct data URLs (`data:<tipo>;base64,<base64>`) for responses.
- `Seguridad/SecurityConfig.java`, `Seguridad/JwtRequestFilter.java`, `Seguridad/JwtUtil.java` — JWT auth flow and where tokens are validated and created.
- `Model/PerfilDTO.java` and `Model/PerfilUpdateDTO.java` — project uses DTOs for profile endpoints.

Project conventions & patterns
- Controllers return `ResponseEntity` and often post-process entities for response (e.g., set `imagenUrl` derived from Base64). Preserve this pattern when modifying controllers.
- Services encapsulate business rules and call Repositories. Avoid putting persistence logic in controllers.
- Repositories are Spring Data JPA interfaces located under `Repository/` — prefer query methods there before adding custom SQL.
- Multipart uploads: limits set in `application.properties` (`50MB`), and uploaded images are typically encoded to Base64 and stored on the entity.
- Security: JWT secret is hard-coded in `application.properties` for development; prefer moving secrets to env vars in production. Tests and local runs assume the properties file values.

Developer workflows (commands)
- Run locally (Windows cmd):
  - Start app: `mvnw.cmd spring-boot:run` (or `./mvnw spring-boot:run` on *nix)
  - Run tests: `mvnw.cmd test`
  - Package: `mvnw.cmd package` then `java -jar target/*.jar`
- Docker / Compose:
  - `docker-compose up --build` (uses `mysql-recetas` DB host configured in `application.properties`)

Integration points and runtime notes
- MySQL service expected at host `mysql-recetas` (check `docker-compose.yml` for service name). If running locally without Docker, update `application.properties` or set corresponding environment variables.
- JWT tokens: expiration is `jwt.expiration` (ms) in `application.properties`. Look at `JwtUtil` for token claims and signing.

What to change vs what to preserve
- Preserve: controller response shaping (data URL creation for images), package structure, and use of DTOs for public endpoints.
- Change cautiously: `application.properties` contains credentials and JWT secret — prefer environment overrides. If updating secrets, update deployment and CI/CD accordingly.

Examples (concrete snippets to follow existing patterns)
- When returning `Recetas` to clients, ensure `imagenUrl` is set if `imagenBase64` exists (see `RecetasController.java`).
- Add business logic in `Service/RecetasService.java` rather than controller; unit-test the service.

If you need more context
- Search for patterns in `src/main/java/com/basededatosrecetas/recetas/Controller` and `Service`.
- If you want to add a migration or change DB schema, prefer using JPA `ddl-auto` settings or add explicit migration tooling (none present currently).

Please review and tell me which sections need more detail (examples, commands, or files to highlight).
