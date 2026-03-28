<div align="center">
  <img src="assets/nyx-logo.png" alt="nyx logo" width="300"/>

**A realistic intentionally vulnerable web application for security analysis.**
</div>

---

## WARNING

This repository contains an intentionally vulnerable, AI-generated Spring Boot application for local educational security testing only.

It is unsafe by design and must never be deployed, exposed to the internet, or connected to real credentials, real users, or real third-party systems.

## AI Disclaimer

This repository was generated with AI assistance. It may contain mistakes, unstable behavior, and flaws beyond the intentional vulnerabilities documented below. Use it only in isolated local environments.

## Stack

- Java 17+
- Spring Boot
- Spring MVC + Thymeleaf
- Spring Data JPA / Hibernate
- PostgreSQL
- Maven
- HTTP session-based auth with custom application logic
- Multipart file uploads
- JUnit + Spring Boot Test + MockMvc

## What The App Includes

- Registration, login, logout, and session-backed auth
- Users, roles, workspaces, memberships, projects, comments, and attachments
- Profile/settings pages
- Admin dashboard and impersonation tooling
- Search, preview, export, and bulk-action API endpoints
- Seeded demo data and integration tests

## Local Setup

1. Copy `.env.example` to `.env` if you want to override defaults.
2. Start PostgreSQL locally:

```bash
docker compose up -d
```

3. Run the app:

```bash
mvn spring-boot:run
```

Alternative:

```bash
bash scripts/run-local.sh
```

4. Open [http://localhost:8080](http://localhost:8080).

## Demo Data

Seeded users all use password `password123`.

- `admin@demo.local`
- `alex@acme.local`
- `beth@beta.local`
- `sam@support.local`

Seeded invite tokens:

- `invite-acme-demo-token`
- `invite-beta-demo-token`

## Running Tests

```bash
mvn -Dmaven.repo.local=.m2 test
```

## Refreshing Catalog Context

If code shifts and you want numbered source context again:

```bash
bash scripts/refresh-vuln-context.sh
```

## Vulnerability Catalog

### VULN-001: Hardcoded fake secrets in repository config
- Category: secret exposure
- Severity: low
- Affected files: `.env.example:5-11`, `src/main/resources/application.yml:37-38`
- Description: Fake support and webhook secrets are committed in plain text.
- Why it is vulnerable: Even though the values are fake, the pattern normalizes shipping secrets in source control and config defaults.
- Preconditions: Anyone who can read the repository can retrieve them.
- Remediation: Remove secrets from tracked files and inject them only from secure runtime secret storage.

### VULN-002: Insecure session cookie flags
- Category: session
- Severity: medium
- Affected files: `src/main/resources/application.yml:27-32`
- Description: The session cookie is explicitly configured without `HttpOnly` or `Secure`.
- Why it is vulnerable: Client-side script access and plaintext transport make session theft easier.
- Preconditions: A victim needs to browse the app in an environment where XSS or network interception is possible.
- Remediation: Set `HttpOnly=true`, `Secure=true`, and use HTTPS-only deployment settings.

### VULN-003: Overly permissive CORS on all API routes
- Category: cors
- Severity: medium
- Affected files: `src/main/java/com/example/insecurecollab/config/WebConfig.java:12-18`
- Description: Every API route accepts all origins, methods, and headers.
- Why it is vulnerable: It broadens cross-origin interaction with the API and removes expected browser-origin boundaries.
- Preconditions: An attacker can cause a user to visit an external site that issues requests to the local app.
- Remediation: Restrict origins, methods, and headers to the minimum required set.

### VULN-004: Weak password hashing with MD5
- Category: crypto
- Severity: high
- Affected files: `src/main/java/com/example/insecurecollab/util/InsecurePasswordUtil.java:12-27`
- Description: Passwords are hashed with unsalted MD5.
- Why it is vulnerable: MD5 is fast and obsolete, making offline cracking trivial.
- Preconditions: An attacker obtains the password hash database or logs.
- Remediation: Use a modern password hasher such as BCrypt, SCrypt, or Argon2 with per-password salt.

### VULN-005: Registration trusts caller-controlled role and admin flags
- Category: authz
- Severity: critical
- Affected files: `src/main/java/com/example/insecurecollab/controller/AuthController.java:31-40`, `src/main/java/com/example/insecurecollab/service/AuthService.java:30-39`
- Description: New users can request arbitrary roles and direct admin access during self-registration.
- Why it is vulnerable: Privileged attributes come straight from request parameters with no approval or policy check.
- Preconditions: Unauthenticated access to the registration form or endpoint.
- Remediation: Ignore privileged role fields on public registration and assign only a safe default role server-side.

### VULN-006: Login flow trusts pre-auth session state and user-controlled redirect targets
- Category: redirect/session
- Severity: medium
- Affected files: `src/main/java/com/example/insecurecollab/controller/AuthController.java:43-64`, `src/main/java/com/example/insecurecollab/service/AuthService.java:23-27`
- Description: The app stores redirect targets in the existing session and never rotates the session on login.
- Why it is vulnerable: This combines open redirect behavior with session fixation risk because attacker-influenced pre-login state survives authentication.
- Preconditions: The attacker can set `next` before login or force reuse of a known session.
- Remediation: Validate redirects against an internal allowlist and regenerate the session after successful login.

### VULN-007: Invite join flow skips identity binding and reuse protections
- Category: invitation/authz
- Severity: high
- Affected files: `src/main/java/com/example/insecurecollab/controller/AuthController.java:83-87`, `src/main/java/com/example/insecurecollab/service/WorkspaceService.java:62-75`
- Description: Any logged-in user can claim any valid invite token regardless of email hint, and tokens remain reusable.
- Why it is vulnerable: The join logic checks only expiry, then assigns the invited role and records the claimer without invalidating the token.
- Preconditions: A user obtains or guesses a valid invite token.
- Remediation: Bind invites to intended recipients, invalidate them after redemption, and verify the caller is authorized to claim them.

### VULN-008: Workspace invite creation lacks backend authorization
- Category: authz
- Severity: high
- Affected files: `src/main/java/com/example/insecurecollab/controller/DashboardController.java:70-75`
- Description: Any authenticated user who can hit the route can mint invites for any workspace ID.
- Why it is vulnerable: The controller never verifies membership or admin/owner status before calling the invite service.
- Preconditions: The attacker knows or guesses a workspace ID.
- Remediation: Enforce membership and role checks in the controller and service before creating invites.

### VULN-009: Project creation allows attachment to unauthorized workspaces
- Category: authz
- Severity: high
- Affected files: `src/main/java/com/example/insecurecollab/controller/ProjectController.java:52-63`, `src/main/java/com/example/insecurecollab/service/ProjectService.java:51-63`
- Description: A logged-in user can create a project inside any workspace by submitting its ID.
- Why it is vulnerable: The server trusts `workspaceId` and never confirms the actor belongs to that workspace.
- Preconditions: The attacker is logged in and knows a target workspace ID.
- Remediation: Require a membership check before allowing project creation under a workspace.

### VULN-010: Project existence leaks before authorization
- Category: info leak
- Severity: medium
- Affected files: `src/main/java/com/example/insecurecollab/controller/ProjectController.java:36-45`
- Description: The controller loads the project object before checking access.
- Why it is vulnerable: Different behavior and timing can reveal whether a private project exists even when the user is unauthorized.
- Preconditions: The attacker can probe project IDs on the HTML project route.
- Remediation: Validate access as early as possible and avoid resolving full objects before authorization decisions.

### VULN-011: Project update and delete actions lack ownership or membership checks
- Category: authz/idor
- Severity: critical
- Affected files: `src/main/java/com/example/insecurecollab/controller/ProjectController.java:66-98`, `src/main/java/com/example/insecurecollab/service/ProjectService.java:65-79`
- Description: Direct POST requests can modify or delete any project by ID.
- Why it is vulnerable: The write paths trust the route ID and never verify ownership, role, or workspace membership.
- Preconditions: The attacker can send crafted POST requests to known project IDs.
- Remediation: Enforce authorization on update and delete paths in both controller and service layers.

### VULN-012: State transition bug makes unpublished projects public
- Category: state management
- Severity: medium
- Affected files: `src/main/java/com/example/insecurecollab/service/ProjectService.java:65-73`
- Description: Clearing the `published` flag also flips `privateProject` to `false`.
- Why it is vulnerable: A supposedly safer draft/private transition accidentally removes privacy.
- Preconditions: Any caller can trigger a project update with `published=false`.
- Remediation: Separate publication state from privacy state and validate transitions explicitly.

### VULN-013: Stored XSS in project comments
- Category: xss
- Severity: high
- Affected files: `src/main/java/com/example/insecurecollab/service/ProjectService.java:81-89`, `src/main/resources/templates/project/detail.html:45-50`
- Description: Comment bodies are stored raw and later rendered with `th:utext`.
- Why it is vulnerable: User-supplied HTML and script content is persisted and then emitted unsanitized into the page.
- Preconditions: The attacker can post a comment to any project they can reach.
- Remediation: Sanitize or escape comment content before rendering it into HTML.

### VULN-014: Attachment upload ignores project authorization
- Category: authz
- Severity: high
- Affected files: `src/main/java/com/example/insecurecollab/controller/ProjectController.java:86-92`, `src/main/java/com/example/insecurecollab/service/AttachmentService.java:32-44`
- Description: Files can be uploaded to any project ID without checking membership or ownership.
- Why it is vulnerable: The server loads the target project and saves the file immediately.
- Preconditions: The attacker is logged in and knows a project ID.
- Remediation: Verify access to the target project before accepting or storing the upload.

### VULN-015: Path traversal via attachment download path parameter
- Category: path traversal
- Severity: high
- Affected files: `src/main/java/com/example/insecurecollab/controller/ApiController.java:106-113`, `src/main/java/com/example/insecurecollab/service/AttachmentService.java:51-52`
- Description: Download requests accept an arbitrary path fragment and join it with the upload root.
- Why it is vulnerable: Absolute paths or traversal sequences can escape the intended directory.
- Preconditions: The attacker can call `/api/attachments/download` directly.
- Remediation: Resolve canonical paths, restrict downloads to known attachment records, and reject traversal or absolute paths.

### VULN-016: Unauthenticated project API exposes private project metadata
- Category: idor/authz
- Severity: high
- Affected files: `src/main/java/com/example/insecurecollab/controller/ApiController.java:49-63`
- Description: The JSON project endpoint returns private project details without any session or membership check.
- Why it is vulnerable: The controller directly loads and returns project data for any numeric ID.
- Preconditions: The attacker can guess or enumerate project IDs.
- Remediation: Require authentication and verify access before returning project metadata.

### VULN-017: Unauthenticated comments API leaks private and internal notes
- Category: idor/authz
- Severity: high
- Affected files: `src/main/java/com/example/insecurecollab/controller/ApiController.java:65-76`
- Description: The comments API returns every comment, including internal notes, for any project ID.
- Why it is vulnerable: The endpoint does not enforce membership, privacy, or internal-note visibility rules.
- Preconditions: The attacker can request `/api/projects/{id}/comments`.
- Remediation: Gate comment access on project authorization and filter internal-only records for unauthorized viewers.

### VULN-018: Bulk close validates only the first project in the list
- Category: authz/bulk action
- Severity: high
- Affected files: `src/main/java/com/example/insecurecollab/controller/ApiController.java:86-99`
- Description: Authorization is based only on the first project ID, then the action applies to every ID in the payload.
- Why it is vulnerable: Mixed-authority bulk requests can smuggle unauthorized project IDs behind one authorized item.
- Preconditions: The attacker controls at least one project they own and can craft the JSON body.
- Remediation: Validate authorization for every item before applying bulk changes.

### VULN-019: Search endpoint is SQL injectable and cross-tenant
- Category: sqli
- Severity: critical
- Affected files: `src/main/java/com/example/insecurecollab/service/ProjectService.java:96-105`, `src/main/java/com/example/insecurecollab/controller/ApiController.java:101-103`
- Description: Search input is interpolated directly into a SQL string with no tenant filtering.
- Why it is vulnerable: Attackers can alter the query and search across all workspaces because no prepared statement or membership filter is used.
- Preconditions: The attacker can send a crafted `q` parameter to the page or API search flow.
- Remediation: Use parameterized queries and scope results to authorized workspaces only.

### VULN-020: Reflected XSS in search query rendering
- Category: xss
- Severity: medium
- Affected files: `src/main/resources/templates/index.html:13-15`
- Description: The search page renders the raw query string with `th:utext`.
- Why it is vulnerable: HTML and script supplied in `q` are reflected into the response without escaping.
- Preconditions: A user visits a crafted search URL.
- Remediation: Render untrusted search input with escaped output, not raw HTML insertion.

### VULN-021: Preview endpoint enables SSRF
- Category: ssrf
- Severity: high
- Affected files: `src/main/java/com/example/insecurecollab/controller/ApiController.java:115-118`, `src/main/java/com/example/insecurecollab/service/LinkPreviewService.java:13-18`
- Description: The app fetches arbitrary URLs supplied by the caller and returns part of the response.
- Why it is vulnerable: There is no allowlist, scheme restriction, or local-network protection.
- Preconditions: The attacker can call `/api/preview?url=...`.
- Remediation: Restrict outbound fetches to vetted hosts or remove arbitrary URL fetching entirely.

### VULN-022: TLS certificate and hostname validation are disabled
- Category: tls
- Severity: high
- Affected files: `src/main/java/com/example/insecurecollab/util/InsecureHttpClient.java:18-47`
- Description: The HTTP client trusts every certificate and hostname.
- Why it is vulnerable: Man-in-the-middle interception becomes trivial because the client accepts attacker-controlled TLS endpoints.
- Preconditions: The preview flow or any future caller uses this client over HTTPS.
- Remediation: Use standard TLS validation and never install trust-all managers in production code.

### VULN-023: Export archive command injection
- Category: command injection
- Severity: critical
- Affected files: `src/main/java/com/example/insecurecollab/util/ShellReportRunner.java:10-16`
- Description: User-controlled `fileName` is concatenated into a shell command executed with `sh -c`.
- Why it is vulnerable: Shell metacharacters in the requested archive name can execute arbitrary local commands.
- Preconditions: The attacker can trigger workspace export with a crafted `fileName`.
- Remediation: Avoid shell execution and use safe Java archiving APIs with strict filename validation.

### VULN-024: Workspace export lacks tenant authorization
- Category: authz/data exposure
- Severity: high
- Affected files: `src/main/java/com/example/insecurecollab/controller/ApiController.java:120-123`, `src/main/java/com/example/insecurecollab/service/ExportService.java:34-48`
- Description: Any caller who can hit the endpoint can export any workspace by ID.
- Why it is vulnerable: The export service loads workspace and project data directly without checking membership.
- Preconditions: The attacker knows or guesses another workspace ID.
- Remediation: Enforce workspace membership and role checks before generating exports.

### VULN-025: User-supplied Thymeleaf template execution in signature preview
- Category: template injection
- Severity: high
- Affected files: `src/main/java/com/example/insecurecollab/controller/SettingsController.java:52-58`, `src/main/java/com/example/insecurecollab/util/InsecureTemplateRenderer.java:14-21`, `src/main/resources/templates/settings/index.html:17-21`
- Description: The settings page evaluates attacker-controlled template markup and renders the result back as HTML.
- Why it is vulnerable: User input is treated as a template instead of inert content, enabling server-side expression execution and HTML injection.
- Preconditions: An authenticated user can reach the settings preview form.
- Remediation: Never evaluate user input as a server template; store it as plain text and escape output.

### VULN-026: Admin access is only hidden in the UI, not enforced in the backend
- Category: authz
- Severity: critical
- Affected files: `src/main/resources/templates/shared/layout.html:14-18`, `src/main/java/com/example/insecurecollab/controller/AdminController.java:26-35`
- Description: The admin link is shown only for admins in the template, but the backend route allows any logged-in user.
- Why it is vulnerable: UI hiding is treated as access control even though direct requests still succeed.
- Preconditions: Any authenticated non-admin user sends a direct request to `/admin`.
- Remediation: Enforce admin authorization on the server for every admin route.

### VULN-027: Impersonation endpoint lacks admin authorization
- Category: authz/impersonation
- Severity: critical
- Affected files: `src/main/java/com/example/insecurecollab/controller/AdminController.java:37-40`, `src/main/java/com/example/insecurecollab/service/AuthService.java:60-62`
- Description: Any logged-in user can set the impersonated account in their session.
- Why it is vulnerable: The impersonation action writes the target user ID straight into the session with no privilege check.
- Preconditions: The attacker can send a POST to `/admin/impersonate`.
- Remediation: Restrict impersonation to tightly controlled admin/support roles and add audit enforcement.

### VULN-028: Debug endpoint leaks fake secrets and full event data without auth
- Category: debug/info leak
- Severity: high
- Affected files: `src/main/java/com/example/insecurecollab/controller/AdminController.java:43-46`, `src/main/java/com/example/insecurecollab/service/AdminService.java:38-44`
- Description: The debug route returns secret-like config values and recent event content to anyone.
- Why it is vulnerable: There is no authentication or authorization check before exposing operational data.
- Preconditions: Any caller can reach `/admin/debug`.
- Remediation: Remove debug endpoints from runtime code or protect them with strict admin-only controls.

### VULN-029: Background digest crosses tenant boundaries
- Category: background/data exposure
- Severity: medium
- Affected files: `src/main/java/com/example/insecurecollab/service/DigestService.java:23-27`
- Description: Scheduled digest logic aggregates all projects across all workspaces into one shared summary.
- Why it is vulnerable: Service-layer background code bypasses the route-layer authorization assumptions used elsewhere.
- Preconditions: The scheduler runs while multiple tenants exist in the database.
- Remediation: Scope background processing per tenant and apply the same authorization boundaries used in request flows.

## Notes

- The vulnerabilities are distributed across controllers, services, config, templates, and helper utilities on purpose.
- The tests assert representative insecure behavior so the repository stays stable as a local benchmark target.
- All credentials and secrets in this repository are fake placeholders.
