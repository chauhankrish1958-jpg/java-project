# ProTrack - Online Project Management Tool

> **Enterprise Web-Based Project Management Platform**  
> Developed using **Java 17**, **Jakarta Servlets (Tomcat 10)**, **JSP**, **JDBC**, **MySQL**, **Bootstrap 5**, and **Maven**.  
> Designed following the **Model-View-Controller (MVC)** architectural pattern for academic evaluation and enterprise demonstration.

---

## Table of Contents
1. [Project Overview & Key Objectives](#1-project-overview--key-objectives)
2. [Technology Stack](#2-technology-stack)
3. [MVC System Architecture](#3-mvc-system-architecture)
4. [Database Design & ER Diagram](#4-database-design--er-diagram)
5. [User Roles & Access Control Matrix](#5-user-roles--access-control-matrix)
6. [Core Java Concepts Demonstrated](#6-core-java-concepts-demonstrated)
7. [JDBC & Database Architecture Deep Dive](#7-jdbc--database-architecture-deep-dive)
8. [Servlet & Filter Implementation Details](#8-servlet--filter-implementation-details)
9. [Innovative & Standout Features](#9-innovative--standout-features)
10. [Step-by-Step Installation & Deployment](#10-step-by-step-installation--deployment)
11. [Default Login Accounts](#11-default-login-accounts)
12. [Testing & Quality Assurance Checklist](#12-testing--quality-assurance-checklist)

---

## 1. Project Overview & Key Objectives

**ProTrack** is a modern, fully functional, multi-tenant web application engineered to solve end-to-end project collaboration, milestone tracking, and task governance. It strictly avoids high-level "magic" frameworks like Spring Boot or Hibernate to demonstrate fundamental mastery of **Core Java**, **Jakarta Servlet API**, **JDBC database integration**, and **JSP presentation technology**.

### Core Objectives:
1. **Administrative Governance:** System administrators provision users, manage all company projects, configure global preferences, and analyze company-wide performance metrics.
2. **Project Manager Supervision:** Project managers plan initiatives, define sprint milestones, allocate tasks to team members with priorities and deadlines, and monitor real-time completion percentages.
3. **Team Member Execution:** Developers, designers, and QA engineers view assigned work items, update task progression states (TODO &rarr; IN PROGRESS &rarr; REVIEW &rarr; COMPLETED), track upcoming deadlines, and inspect assigned projects.
4. **Security & Authorization:** Strict role-based URL guards prevent privilege escalation or URL tampering via Servlet Filters.

---

## 2. Technology Stack

| Layer | Technology | Specification / Version |
| :--- | :--- | :--- |
| **Language** | Java | Java SE 17 (LTS) / Java 21 compatible |
| **Web Server / Servlet Container** | Apache Tomcat | Version 10.1+ (Jakarta EE 10 / Servlet 6.0) |
| **Database** | MySQL | MySQL 8.0+ (Strict fail-fast requirement, zero H2 fallback) |
| **Database Connectivity & Pooling** | JDBC & HikariCP | `mysql-connector-j` 8.3.0 with `HikariCP` 5.1.0 (`PreparedStatement`, ACID Transactions) |
| **Presentation Tier** | JSP & JSTL | Jakarta Server Pages 3.1 & JSTL 3.0 (with XML output escaping) |
| **Frontend Framework** | Bootstrap 5 | Bootstrap 5.3.3 & Bootstrap Icons 1.11.3 |
| **Visual Analytics** | Chart.js | Chart.js 4.4.1 (Doughnut & Bar charts) |
| **Password & Token Security** | BCrypt & SecureRandom | OpenBSD Blowfish password hashing & cryptographic CSRF tokens |
| **Build & Packaging Tool** | Apache Maven | Maven 3.9+ (Produces standard `protrack.war`) |
| **Automated Testing** | JUnit 5 & Mockito | 68 automated unit tests with mocked JDBC & services (JUnit Jupiter 5.10.2 + Mockito 5.11.0) |

---

## 3. MVC System Architecture

ProTrack strictly adheres to the industry-standard **Model-View-Controller (MVC)** architectural design:

```
                      Browser Client
                           │
                 HTTP Request (GET/POST)
                           ▼
          ┌───────────────────────────────────┐
          │  Jakarta Servlet Filter Pipeline  │
          │  - EncodingFilter (UTF-8)         │
          │  - AuthenticationFilter (RBAC)    │
          └─────────────────┬─────────────────┘
                            │ (Authorized Request)
                            ▼
     ┌─────────────────────────────────────────────┐
     │             CONTROLLER LAYER                │
     │  - LoginServlet / RegisterServlet / Logout  │
     │  - DashboardServlet (Role-based dispatcher) │
     │  - UserServlet / ProjectServlet             │
     │  - TaskServlet / ProfileServlet / Settings  │
     └──────────────────────┬──────────────────────┘
                            │ Delegates Business Logic
                            ▼
     ┌─────────────────────────────────────────────┐
     │              SERVICE LAYER                  │
     │  - AuthService / UserService / ProjectServ  │
     │  - TaskService / DashboardService (Metrics) │
     └──────────────────────┬──────────────────────┘
                            │ Data Access Invocations
                            ▼
     ┌─────────────────────────────────────────────┐
     │                 DAO LAYER                   │
     │  - UserDAO / ProjectDAO / TaskDAO           │
     │  - ActivityDAO / SettingsDAO (JDBC + SQL)   │
     └──────────────────────┬──────────────────────┘
                            │ PreparedStatement / Connection
                            ▼
     ┌─────────────────────────────────────────────┐
     │              DATABASE TIER                  │
     │         MySQL: project_management_db        │
     └─────────────────────────────────────────────┘
```

### Directory Structure:
```
protrack/
├── pom.xml                               # Maven project configuration (dependencies & plugins)
├── mvnw / mvnw.cmd                       # Maven wrapper scripts
├── README.md                             # Comprehensive project documentation
├── src/
│   ├── main/
│   │   ├── java/com/protrack/
│   │   │   ├── exception/                # Custom business & database exceptions
│   │   │   ├── util/                     # BCrypt, DBConnection, CSRFTokenUtil, ValidationUtil
│   │   │   ├── model/                    # Domain entities, OOP inheritance & enums
│   │   │   ├── dao/                      # GenericDAO and entity DAO interfaces
│   │   │   │   └── impl/                 # JDBC implementations with PreparedStatements
│   │   │   ├── service/                  # Business logic, metrics & ownership checks
│   │   │   ├── filter/                   # EncodingFilter & AuthenticationFilter (RBAC + CSRF + Matrix guard)
│   │   │   ├── scheduler/                # Background ScheduledExecutorService & ReentrantLock daemon
│   │   │   ├── listener/                 # DBPoolContextListener & TaskSchedulerListener (Lifecycle hooks)
│   │   │   └── servlet/                  # Jakarta HTTP Servlets (Controllers)
│   │   ├── resources/
│   │   │   ├── db.properties             # Database connection credentials (env overrides supported)
│   │   │   └── database.sql              # Packaged schema resource
│   │   └── webapp/
│   │       ├── index.jsp                 # Smart root landing / dispatcher
│   │       ├── assets/
│   │       │   ├── css/style.css         # Modern SaaS stylesheet (navy/purple)
│   │       │   └── js/main.js            # Modals, client search, alerts & charts
│   │       └── WEB-INF/
│   │           ├── web.xml               # Deployment descriptor & error mappings
│   │           └── views/                # Protected JSP presentation templates (XML-escaped)
│   │               ├── common/           # Header, sidebar, navbar, footer, alerts
│   │               ├── auth/             # Login and register pages (CSRF protected)
│   │               ├── admin/            # Admin dashboard, users, projects, settings
│   │               ├── manager/          # PM dashboard, projects, tasks, detail
│   │               ├── member/           # Member dashboard, my tasks, projects
│   │               ├── error/            # Custom 403, 404, 500 error views
│   │               ├── profile.jsp       # User profile & password modification
│   │               └── activity.jsp      # System audit trail and activity feed
│   └── test/
│       ├── java/com/protrack/
│       │   ├── dao/ProjectDAORollbackTest.java     # Atomic transaction rollback tests
│       │   ├── filter/SecurityRBACAndAuthTest.java # RBAC, matrix URL bypass, CSRF & stale session tests
│       │   ├── scheduler/OverdueTaskScannerTest.java # Multithreading & ReentrantLock concurrency tests
│       │   ├── service/RegistrationSecurityTest.java # Role escalation & 8-char password tests
│       │   ├── service/TaskAuthorizationTest.java  # Task ownership, assignee validation & status update tests
│       │   ├── service/UserServiceProtectionTest.java # Admin self-protection & last admin safeguard tests
│       │   ├── servlet/ProjectServletAuthorizationTest.java # Project ownership & member access tests
│       │   ├── servlet/TaskServletAuthorizationTest.java    # Cross-project move & manager task scope tests
│       │   ├── servlet/LogoutServletTest.java               # POST-only CSRF-protected logout tests
│       │   ├── util/LoginAttemptTrackerTest.java   # Rate limiting & brute-force lockout tests
│       │   ├── util/XSSSanitizationTest.java       # HTML/script injection neutralization tests
│       │   └── ProTrackUnitTest.java               # Core unit tests (BCrypt, validation, enums)
│       └── resources/
│           └── mockito-extensions/                 # Java 21 compatible mock-maker-subclass
```

---

## 4. Database Design & ER Diagram

The database `project_management_db` is normalized up to **Third Normal Form (3NF)** with explicit foreign key constraints, indexes on lookup columns, and cascade rules.

```mermaid
erDiagram
    users ||--o{ projects : "manages"
    users ||--o{ project_members : "participates_in"
    projects ||--o{ project_members : "has"
    projects ||--o{ tasks : "contains"
    users ||--o{ tasks : "assigned_to"
    users ||--o{ tasks : "created_by"
    users ||--o{ activity_logs : "triggers"

    users {
        int user_id PK
        varchar full_name
        varchar email UK
        varchar password
        enum role
        enum status
        timestamp created_at
        timestamp updated_at
    }

    projects {
        int project_id PK
        varchar title
        text description
        date start_date
        date end_date
        int manager_id FK
        enum status
        timestamp created_at
        timestamp updated_at
    }

    project_members {
        int id PK
        int project_id FK
        int user_id FK
        timestamp assigned_at
    }

    tasks {
        int task_id PK
        varchar task_title
        text description
        int project_id FK
        int assigned_to FK
        enum priority
        enum status
        date start_date
        date due_date
        int created_by FK
        timestamp created_at
        timestamp updated_at
    }

    activity_logs {
        int log_id PK
        int user_id FK
        varchar action
        text details
        varchar entity_type
        int entity_id
        timestamp created_at
    }

    system_settings {
        int setting_id PK
        varchar setting_key UK
        text setting_value
        varchar description
        timestamp updated_at
    }
```

---

## 5. User Roles & Access Control Matrix

| Capability / Module | ADMIN | PROJECT_MANAGER | TEAM_MEMBER |
| :--- | :---: | :---: | :---: |
| **Access Admin Dashboard (`/admin/*`)** | :white_check_mark: Yes | :x: No (403) | :x: No (403) |
| **Manage Users (CRUD, Role Assignment)** | :white_check_mark: Yes | :x: No | :x: No |
| **Configure System Settings** | :white_check_mark: Yes | :x: No | :x: No |
| **Create Projects** | :white_check_mark: Yes | :white_check_mark: Yes | :x: No |
| **Edit / Delete Projects** | :white_check_mark: Yes | :white_check_mark: Yes (Managed projects only) | :x: No |
| **Assign Members to Projects** | :white_check_mark: Yes | :white_check_mark: Yes (Managed projects only) | :x: No |
| **Create Tasks** | :white_check_mark: Yes | :white_check_mark: Yes (Managed projects only) | :x: No (Blocked in Filter & Servlet) |
| **Edit / Delete Tasks** | :white_check_mark: Yes | :white_check_mark: Yes (Managed projects only) | :x: No (Blocked in Filter & Servlet) |
| **View Assigned Tasks** | :white_check_mark: Yes | :white_check_mark: Yes | :white_check_mark: Yes |
| **Update Task Status (TODO &rarr; COMPLETED)** | :white_check_mark: Yes | :white_check_mark: Yes | :white_check_mark: Yes (**Own assigned tasks only** - ownership verified) |
| **Self-Registration Role Assignment** | N/A (Admin only) | :x: Prevented | :white_check_mark: Yes (Forced strictly to `TEAM_MEMBER`) |
| **Update Personal Profile & Password** | :white_check_mark: Yes | :white_check_mark: Yes | :white_check_mark: Yes (Minimum 8 chars) |
| **View Audit Trail Activity Feed** | :white_check_mark: Yes | :white_check_mark: Yes | :white_check_mark: Yes |

> **Task Ownership Enforcement:** Even though `TaskServlet` handles status updates, backend business logic (`TaskService.updateTaskStatusWithRoleCheck`) cryptographically checks if the logged-in user is an Admin, the Project Manager of the project, or the exact `assignedTo` user. Team members cannot tamper with or update tasks belonging to other team members.

---

## 6. Core Java Concepts Demonstrated

### A. Object-Oriented Programming (OOP)
1. **Abstraction:**
   - `BaseEntity` abstract class encapsulates identity (`id`) and timestamp audit fields (`createdAt`, `updatedAt`).
   - `GenericDAO<T, ID>` interface defines standard CRUD contracts without leaking JDBC details.
2. **Inheritance:**
   - Hierarchy: `BaseEntity` &rarr; `User` &rarr; `Admin`, `ProjectManager`, `TeamMember`.
   - Each subclass inherits common credentials and identity from `User`.
3. **Polymorphism:**
   - Role capability methods defined in `User` (`canManageUsers()`, `canManageProjects()`, `canAssignTasks()`, `getDashboardUrl()`) are overridden polymorphically in `Admin`, `ProjectManager`, and `TeamMember`.
   - Factory pattern: `UserFactory.createUser(role, ...)` instantiates the correct concrete subtype dynamically.
4. **Encapsulation:**
   - All model fields are `private` or `protected` with strictly validated getters, setters, and constructors.

### B. Exception Handling Architecture
- **Custom Hierarchy:**
  - `AppException`: Root custom checked exception for expected business violations.
  - `UserNotFoundException`, `ProjectNotFoundException`, `InvalidCredentialsException`, `TaskAssignmentException`, `UnauthorizedAccessException`.
  - `DatabaseOperationException`: Unchecked runtime wrapper for low-level `SQLException`.
- **Try-Catch-Finally / Try-with-Resources:**
  - All database queries employ Java 7+ try-with-resources to guarantee connection and statement closure even if SQL failures occur.

### C. Collections and Generics
- Extensive use of generic collections: `List<User>`, `List<Project>`, `List<Task>`, `Map<String, Object>`, `EnumMap<TaskStatus, Integer>`.
- Generics in DAO pattern: `GenericDAO<T, ID>` enables reusable interfaces for any entity.

### D. Multithreading, Concurrency & Synchronization
1. **Asynchronous Daemon Task Scheduling (`ScheduledExecutorService`):**
   - [`OverdueTaskScanner`](file:///Users/krishchauhan/Downloads/project%202/src/main/java/com/protrack/scheduler/OverdueTaskScanner.java) employs `Executors.newSingleThreadScheduledExecutor` with a custom `ThreadFactory` creating named daemon worker threads (`ProTrack-OverdueTaskMonitor-X`).
   - Automatically initiated upon web application startup via [`TaskSchedulerListener`](file:///Users/krishchauhan/Downloads/project%202/src/main/java/com/protrack/listener/TaskSchedulerListener.java) (`ServletContextListener`), it runs periodic background scans (default interval: 120s) checking for overdue tasks without blocking incoming HTTP worker threads.
   - Graceful shutdown logic (`scheduler.shutdownNow()` and `awaitTermination()`) prevents thread or resource leaks upon servlet context destruction.
2. **Explicit Thread Synchronization (`ReentrantLock`):**
   - A `java.util.concurrent.locks.ReentrantLock` is used with standard `lock.lock()` and `try ... finally { lock.unlock(); }` idiom to guard shared multi-field scan metrics (`totalScansCompleted`, `lastOverdueDetectedCount`, `lastScanDurationMs`, `lastScanTime`, `lastThreadName`).
   - Guarantees atomicity and prevents race conditions when concurrent background scans or manual invocations update shared counters.
3. **Intrinsic Lock Synchronization (`synchronized`):**
   - The method `getMetricsSnapshot()` is `synchronized` and acquires the `ReentrantLock` to construct and return an immutable Java 17 `MetricsSnapshot` record, ensuring consistent memory visibility across HTTP request threads reading live stats for the Admin Dashboard.
4. **Thread-Safe Concurrent Rate Limiting (`ConcurrentHashMap`):**
   - [`LoginAttemptTracker`](file:///Users/krishchauhan/Downloads/project%202/src/main/java/com/protrack/util/LoginAttemptTracker.java) employs `ConcurrentHashMap` with atomic `compute()` operations to track and throttle concurrent login attempts per `email:ip` without external locking overhead.
5. **Visible Dashboard Concurrency Monitor:**
   - The Admin Dashboard features a prominent **"Background Overdue Task Daemon"** card that renders live metrics directly from the thread pool: active thread name, total scans completed, overdue tasks flagged, and execution duration.

---

## 7. JDBC & Database Architecture Deep Dive

1. **PreparedStatement Protection:**
   - All queries use parameterized `?` placeholders. No dynamic SQL string concatenation is used for user input, completely defeating SQL Injection attacks.
2. **HikariCP Connection Pooling & Fail-Fast MySQL Connectivity (Zero H2 Fallback):**
   - `DBConnection` manages a high-performance singleton **HikariCP Connection Pool** (`HikariDataSource`), configured from `db.properties` (`db.initialSize` mapped to `minimumIdle`, `db.maxTotal` mapped to `maximumPoolSize`) and runtime environment variable overrides (`DB_URL`, `DB_USER`, `DB_PASSWORD`, `DB_DRIVER`).
   - A fail-fast timeout (3,000 ms) is enforced: if MySQL is unreachable, `DBConnection` immediately and loudly throws an informative `SQLException` detailing the JDBC URL, host, and port rather than hanging or silently falling back.
   - **Zero silent fallback:** Embedded H2 database dependencies and fallbacks have been completely excised from the architecture to ensure total schema and data integrity across runs.
   - **Lifecycle Management:** [`DBPoolContextListener`](file:///Users/krishchauhan/Downloads/project%202/src/main/java/com/protrack/listener/DBPoolContextListener.java) implements `ServletContextListener` and explicitly shuts down the HikariCP pool (`DBConnection.closePool()`) upon Tomcat server shutdown or application undeployment.
3. **JDBC Transactions (ACID Support) & Verified Rollback:**
   - In `ProjectDAOImpl.saveWithMembers()`, multi-table atomic persistence is guaranteed:
     ```java
     conn.setAutoCommit(false);
     // 1. Insert project record -> retrieve generated project_id
     // 2. Batch insert project_members
     conn.commit(); // Atomically committed upon total success
     // In catch block:
     DBConnection.rollback(conn); // Rolled back on any batch failure
     conn.setAutoCommit(true);    // Connection state cleanly restored
     ```
   - This transaction rollback behavior is covered by unit tests with mocked JDBC (`ProjectDAORollbackTest`), verifying that failure at any point triggers `conn.rollback()` and properly restores auto-commit state.

---

## 8. Servlet & Security Architecture

1. **Explicit Filter Pipeline Ordering:**
   - Declared directly in `web.xml` with standard XML elements (`EncodingFilter` first, followed by `AuthenticationFilter`); `@WebFilter` annotations are removed to prevent duplicate container registration.
   - `EncodingFilter`: Intercepts `/*` and sets `UTF-8` character encoding for all HTTP requests and responses.
   - `AuthenticationFilter`:
     - **Matrix URL Parameter Defense:** Strips semicolon matrix parameters (`path.substring(0, semi)`) from incoming request paths before evaluating authorization rules. A malicious URL like `/manager/tasks;x=.css` cannot bypass authentication filters.
     - **Stale Session Invalidation & Role Refresh:** For authenticated requests, the filter re-queries user status and role against the database (`UserDAO.findById`) using a 30-second per-session cache window. If a user is deactivated or deleted, their session is invalidated and redirected to `/login`. If an administrator changes a user's role, the session's `currentUser` is refreshed in real time.
     - **Path-Based Whitelisting:** Public endpoints (`/login`, `/register`) and static assets strictly within `/assets/*` are permitted. All application URLs (`/admin/*`, `/manager/*`, `/member/*`, `/profile`, `/tasks`, `/projects`) require active session authentication and role permissions. Mismatches return HTTP 403 Forbidden.
     - **CSRF Token Validation:** All state-changing `POST` requests for authenticated operations (user management, project creation/editing, task manipulation, system settings, profile updates, and logout) are validated against a cryptographically secure per-session token generated by `CSRFTokenUtil`. Requests with missing or mismatched tokens are rejected immediately with HTTP 403 Forbidden.
2. **Session Management & Cookie Hardening:**
   - `request.getSession(true)` initializes authenticated user state.
   - **Session Fixation Prevention:** Upon successful credential validation, `LoginServlet` invokes `request.changeSessionId()`, rotating the session ID while preserving session state to defeat session hijacking attacks.
   - **Cookie Configuration:** The `PROTRACK_SESSION` cookie is configured in `web.xml` with `<http-only>true</http-only>` to block client-side JavaScript access, and `SameSite=Lax` declared via Servlet 6 syntax (`<attribute><attribute-name>SameSite</attribute-name><attribute-value>Lax</attribute-value></attribute>`). For production deployments with TLS/HTTPS, `<secure>true</secure>` can be enabled.
   - Session timeout set to 30 minutes in `web.xml`.
   - **POST-Only CSRF Logout:** `LogoutServlet` enforces `POST`-only requests containing valid `csrfToken` to prevent cross-site forced logout attacks. `GET /logout` safely redirects to the user's dashboard without invalidating the session.
3. **Login Brute-Force Rate Limiting:**
   - [`LoginAttemptTracker`](file:///Users/krishchauhan/Downloads/project%202/src/main/java/com/protrack/util/LoginAttemptTracker.java) maintains an in-memory `ConcurrentHashMap` keyed by `email:ip`.
   - After 5 consecutive failed attempts, the key is locked for 5 minutes (`300,000` ms) and surfaces `"Too many failed attempts, try again later."`.
   - Successful authentication immediately resets the failed counter. Generic `"Invalid email or password."` is preserved on earlier failures to prevent account enumeration.
4. **Context-Aware JSP Output Escaping (Defeating Double-Escaping):**
   - **Clean Database Records:** User input (such as project titles containing special characters, e.g., "R&D Team" or "Q&A System") is stored in its natural raw form in the database without premature entity encoding. Input validation in services verifies length and format constraints.
   - **Contextual Output Escaping in JSPs:** All dynamic user data rendered in JSPs is safely escaped at display time using `<%= ValidationUtil.escapeXml(...) %>` (converting `<`, `>`, `&`, `"`, `'` into safe XML entities). This strictly avoids double-escaping anomalies (e.g., preventing `R&D Team` from turning into `R&amp;amp;D Team`) while thoroughly neutralizing stored and reflected XSS attacks.
5. **Internal Error Masking & Server-Side Logging:**
   - `500.jsp` masks raw stack traces and internal exceptions (`exception.getMessage()` removed) from end users and logs the full exception server-side using `application.log(...)`.
   - Servlets catch blocks log unexpected runtime errors server-side via `getServletContext().log(...)` and surface safe, generic messages.
6. **Strong Password Policy:**
   - Password minimum length is strictly enforced to at least 8 characters across frontend HTML5 validators, `ValidationUtil.isStrongPassword()`, and backend service layers.

---

## 9. Innovative & Standout Features

1. **Automated Deadline & Overdue Identification:** Real-time SQL comparisons against `CURRENT_DATE()` instantly flag overdue tasks with pulsing red badges.
2. **Dynamic Project Completion Metrics:** Mathematical formula `(completedTasks / totalTasks) * 100` dynamically calculates live project progress bars.
3. **Comprehensive Audit Logging:** Every create, update, delete, status transition, and login action is recorded into the `activity_logs` table.
4. **Interactive Dashboard Charts:** Chart.js integration plots project status doughnuts and task priority bar charts from live database counts.
5. **Quick-Fill Demo Credentials:** One-click demo chips on the login screen automatically populate credentials for rapid viva evaluation.

---

## 10. Step-by-Step Installation & Deployment

### Prerequisites
- **Java Development Kit (JDK):** Version 17 or higher
- **Apache Tomcat:** Version 10.1 or higher (Jakarta EE 10)
- **MySQL Database Server:** Version 8.0 or higher
- **Apache Maven:** Version 3.8+ (An embedded wrapper `./mvnw` is also included)

### Step 1: Database Setup
1. Open your terminal or MySQL Workbench.
2. Log in to MySQL:
   ```bash
   mysql -u root -p
   ```
3. Run the provided database initialization script from `src/main/resources/database.sql`:
   ```sql
   source /path/to/project/src/main/resources/database.sql;
   ```
   *(Or copy-paste the entire contents of `src/main/resources/database.sql` into MySQL Workbench and execute).*

### Step 2: Configure Database Credentials
Edit `src/main/resources/db.properties` to match your local MySQL configuration:
```properties
db.driver=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://localhost:3306/project_management_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8
db.user=root
db.password=your_mysql_password
```

> [!IMPORTANT]
> The repository includes default credentials (`root/root`) for convenience during local evaluation. **You should update `db.user` and `db.password` to match your local MySQL account**, or provide them as environment variables (`DB_URL`, `DB_USER`, `DB_PASSWORD`) which automatically take precedence without modifying source files.

### Step 3: Automated Database Verification (Optional)
Run the built-in database initializer to test connectivity:
```bash
./mvnw test-compile exec:java -Dexec.mainClass="com.protrack.util.DatabaseInitializer"
```

### Step 4: Build the Project with Maven
Open terminal in the project directory and build the deployable `.war` archive:
```bash
./mvnw clean package
```
*(On Windows, run `mvnw.cmd clean package` or standard `mvn clean package`).*

Upon success, Maven creates:
```
target/protrack.war
```

### Step 5: Deploy to Apache Tomcat 10
1. Copy `target/protrack.war` to your Tomcat `webapps/` folder:
   ```bash
   cp target/protrack.war /path/to/apache-tomcat-10.1.x/webapps/
   ```
2. Start Apache Tomcat:
   ```bash
   # On macOS / Linux
   /path/to/apache-tomcat-10.1.x/bin/startup.sh

   # On Windows
   C:\path\to\apache-tomcat-10.1.x\bin\startup.bat
   ```
3. Open your web browser and navigate to:
   ```
   http://localhost:8085/protrack
   (or http://localhost:8085/ or http://localhost:8080/protrack)
   ```

---

## 11. Default Login Accounts

| Role | Email Address | Password | Description / Use Case |
| :--- | :--- | :--- | :--- |
| **ADMIN** | `admin@protrack.com` | `admin123` | Full system governance, user CRUD, system settings |
| **PROJECT_MANAGER** | `sarah.pm@protrack.com` | `manager123` | Leads E-Commerce, Cloud Migration & HR Portal |
| **PROJECT_MANAGER** | `david.pm@protrack.com` | `manager123` | Leads Mobile Banking & AI Chatbot projects |
| **TEAM_MEMBER** | `alex.dev@protrack.com` | `member123` | Senior Full-Stack Developer with multiple tasks |
| **TEAM_MEMBER** | `emma.qa@protrack.com` | `member123` | Quality Assurance Engineer |
| **TEAM_MEMBER** | `liam.design@protrack.com` | `member123` | UI/UX Designer |
| **TEAM_MEMBER** | `sophia.dev@protrack.com` | `member123` | Backend & Cloud Infrastructure Developer |

---

## 12. Testing & Quality Assurance Checklist

ProTrack includes an extensive automated test suite built on **JUnit 5 (Jupiter)** and **Mockito 5**. All tests run completely offline and independent of external database state via mock drivers and isolated unit logic.

Execute the entire test suite using:
```bash
./mvnw test
```

### Automated Test Suite Overview (68 Tests — 100% Passing):

| Test Suite Class | Tests | Category | Key Assertions & Scenarios Verified |
| :--- | :---: | :--- | :--- |
| **`OverdueTaskScannerTest`** | 5 | Concurrency & Scheduler | • Thread-safe concurrent counter updates under `ReentrantLock`<br>• `ScheduledExecutorService` lifecycle management (start/stop)<br>• Overdue task scan execution via mock DAO<br>• Graceful database failure handling without crashing daemon thread<br>• Snapshot immutability across concurrent readers |
| **`TaskServletAuthorizationTest`** | 6 | Servlet & Security | • Project Manager blocked from cross-project task move (target project ownership check)<br>• Project Manager blocked from editing tasks in unmanaged projects<br>• Manager `/manager/tasks` scoped to manager's projects at DAO level<br>• Manager searching unowned `projectId` returns empty list<br>• Administrator sees all tasks across all projects<br>• Malformed date strings trigger descriptive format error |
| **`ProjectServletAuthorizationTest`** | 6 | Servlet & Ownership | • Project Manager viewing unmanaged project denied with HTTP 403 Forbidden (`403.jsp`)<br>• Project Manager viewing owned project succeeds (`manager/project-detail.jsp`)<br>• Administrator viewing any project workspace succeeds (`admin/project-detail.jsp`)<br>• Team Member viewing unassigned project denied with HTTP 403 Forbidden<br>• Team Member viewing assigned project succeeds and receives filtered list containing only their own tasks (`member/project-detail.jsp`)<br>• Malformed date strings trigger descriptive format error |
| **`LogoutServletTest`** | 4 | Servlet & CSRF | • GET /logout while logged in safely redirects to dashboard without invalidating session<br>• GET /logout while unauthenticated returns HTTP 405 Method Not Allowed<br>• POST /logout with valid CSRF token invalidates session and redirects to /login<br>• POST /logout with missing or invalid CSRF token returns HTTP 403 Forbidden |
| **`LoginAttemptTrackerTest`** | 5 | Security & Rate Limiting | • Brand new email/IP combination is not locked<br>• 1 to 4 failed login attempts do not trigger lockout<br>• Exactly 5 failed attempts locks target key for 5 minutes<br>• Successful login immediately resets failed attempt counter<br>• Lock tracking is strictly isolated per email + IP combination |
| **`UserServiceProtectionTest`** | 7 | Service & RBAC Defense | • Administrator cannot change their own role away from `ADMIN`<br>• Administrator cannot deactivate their own account status<br>• Cannot demote the last remaining active administrator<br>• Cannot deactivate the last remaining active administrator<br>• Cannot delete the last remaining active administrator<br>• User self-deletion strictly rejected with clear error message<br>• Demoting an administrator succeeds when multiple active administrators exist |
| **`TaskAuthorizationTest`** | 9 | Service & Assignee Security | • Team members can update status on tasks assigned directly to their `userId`<br>• Team members denied with `UnauthorizedAccessException` when attempting to alter unassigned tasks<br>• Team members denied when attempting to alter other team members' tasks<br>• Administrators can update status of any task across all projects<br>• Assignee validation: non-existent user rejected<br>• Assignee validation: inactive user rejected<br>• Assignee validation: user without `Role.TEAM_MEMBER` rejected<br>• Assignee validation: user not assigned to the task's project rejected<br>• Assignee validation: valid active project member succeeds |
| **`SecurityRBACAndAuthTest`** | 9 | Security & Filters | • Unauthenticated requests safely redirected to `/login`<br>• Semicolon matrix parameter auth bypass neutralized (`/manager/tasks;x=.css` denied)<br>• RBAC role mismatch returns HTTP 403 Forbidden<br>• Static asset whitelist (`/assets/*`) bypasses auth without risk<br>• Missing CSRF token on POST yields HTTP 403<br>• Invalid CSRF token on POST yields HTTP 403<br>• Valid session CSRF token on POST passes filter successfully<br>• Stale session: deactivated user in DB has session invalidated and redirected to `/login`<br>• Stale session: role update in DB dynamically refreshes session role and updates RBAC enforcement |
| **`RegistrationSecurityTest`** | 5 | Service & Validation | • Passwords shorter than 8 characters strictly rejected<br>• Raw special characters (e.g. & in 'R&D') preserved cleanly in storage without premature entity encoding<br>• Role escalation prevented: public registrations forced strictly to `Role.TEAM_MEMBER`<br>• Duplicate email registrations rejected with `AppException`<br>• Registration policy check rejects signups when registration is disabled in settings |
| **`XSSSanitizationTest`** | 4 | Security Utilities | • JSP output escaping (`escapeXml`) neutralizes dangerous `<script>` and `javascript:` payloads<br>• Neutralizes HTML attribute breakout attacks (`"><img src=x onerror=...`)<br>• Converts `&`, `<`, `>`, `"`, `'` to safe XML entities once without double-escaping real-world names (e.g. `R&D Team` renders cleanly)<br>• Gracefully handles `null` and empty string inputs |
| **`ProTrackUnitTest`** | 6 | Core Unit Tests | • BCrypt salt generation and password verification<br>• Rejection of passwords with insufficient length (< 8 chars)<br>• Email format regex validation<br>• Date calculations and overdue detection<br>• TaskStatus and Priority enum display mappings<br>• Polymorphic role capability checks on concrete `User` subtypes |
| **`ProjectDAORollbackTest`** | 2 | Unit Test with Mocked JDBC | • Atomic rollback on batch failure: verifies `conn.rollback()` is invoked, auto-commit is restored, and `DatabaseOperationException` is thrown using mocked JDBC connection<br>• Clean commit on valid project and member batch insertion |

```
[INFO] Results:
[INFO] Tests run: 68, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

### Functional Verification Matrix:
- [x] **Authentication & Session Fixation:** Login with valid credentials invokes `request.changeSessionId()` and redirects to role-specific dashboard.
- [x] **Stale Session Invalidation & Role Refresh:** `AuthenticationFilter` re-evaluates user status and role against the database (~30s cache); deactivations immediately invalidate sessions and demotions enforce new roles.
- [x] **CSRF Protection:** Per-session cryptographic token required on state-changing authenticated POST forms.
- [x] **URL Matrix Parameter Defense:** Semicolon path parameters (`/manager/tasks;x=.css`) stripped and blocked from filter bypass.
- [x] **Project Ownership on View:** `ProjectServlet` verifies that Project Managers can only view projects they manage; unauthorized access returns HTTP 403 (`403.jsp`).
- [x] **Team Member Read-Only Workspace:** Team members accessing `/member/projects?action=view` view their assigned project details, assigned team, and only their own tasks via `member/project-detail.jsp`; unassigned members receive HTTP 403.
- [x] **Task Move Ownership Protection:** TaskServlet verifies ownership of both current and target projects on task updates, preventing Project Managers from moving tasks into projects they do not manage.
- [x] **Manager Task Scope Filtering:** `TaskServlet` and `TaskDAO.searchTasks` restrict manager task listings to projects owned by that manager at the database query level.
- [x] **Task Assignee Membership & Status Validation:** `TaskService` validates that assigned users exist, are `ACTIVE`, hold `Role.TEAM_MEMBER`, and are mapped to the task's project in `project_members`.
- [x] **Administrator Protection:** Admin accounts cannot demote their own role or deactivate themselves; the last remaining active administrator cannot be demoted, deactivated, or deleted.
- [x] **Role Authorization:** Accessing `/admin/users` as a Team Member produces a 403 Forbidden page.
- [x] **Role Escalation Prevention:** Public registration form forced strictly to `Role.TEAM_MEMBER`.
- [x] **Password Security:** BCrypt Blowfish hashing with 8-character minimum policy verified; plain-text passwords are never stored.
- [x] **Zero H2 Fallback:** `DBConnection` strictly enforces MySQL with loud `SQLException` on failure.
- [x] **HikariCP Connection Pool & Lifecycle:** `HikariDataSource` connection pool manages JDBC lifecycle with `minimumIdle`, `maximumPoolSize`, fail-fast timeout, and context shutdown hook (`DBPoolContextListener`).
- [x] **Atomic Transactions:** Project batch persistence verified with automated rollback on failure (`ProjectDAORollbackTest`).
- [x] **Context-Aware Output Escaping:** Dynamic JSP escaping via `ValidationUtil.escapeXml()` prevents XSS and avoids double-escaping raw text like "R&D" or "Q&A <test>".
- [x] **Login Brute-Force Rate Limiting:** `LoginAttemptTracker` locks login after 5 failed attempts for 5 minutes with `"Too many failed attempts, try again later."`.
- [x] **POST-Only CSRF Logout:** `LogoutServlet` enforces `POST` with CSRF token; `GET /logout` requests redirect safely to dashboard without terminating session.
- [x] **Internal Error Masking & Server Logging:** `500.jsp` and servlet catch blocks mask internal stack traces and log server-side (`application.log` and `getServletContext().log`).
- [x] **Date Format Validation:** Malformed date strings trigger `"Invalid date format (expected yyyy-MM-dd)"`.
- [x] **SameSite Cookie Configuration:** Session cookies protected with `SameSite=Lax` via Servlet 6 attribute declaration in `web.xml`.
- [x] **Background Task Daemon & Concurrency:** Periodic overdue task monitor runs via `ScheduledExecutorService` with shared metrics guarded by `ReentrantLock` and visible on the Admin Dashboard.

---

&copy; **ProTrack - Online Project Management Tool**. Built for Academic Evaluation.
