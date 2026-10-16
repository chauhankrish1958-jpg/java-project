# ProTrack - Online Project Management Tool (Complete Java OOP Project & Frontend)

A complete, production-grade **Online Project Management Tool** built to fulfill all academic and enterprise specifications (GUVI Geek Network Pvt. Ltd. curriculum).

The project features a **complete Java OOP backend architecture** coupled with a modern, responsive **HTML5/CSS3/JavaScript frontend** with interactive dashboards for **Admin**, **Project Manager**, and **Team Member**.

---

## 🌟 Project Summary & Overview

* **Goal**: Provide a collaborative project management system for Project Managers to create and manage projects, Team Members to track and update tasks, and Administrators to oversee users, projects, and global system configurations.
* **Core Pillars**:
  1. **Core Java OOP Backend**: Implements Abstraction, Inheritance, Polymorphism, and Encapsulation across clean Model, Service, and Presentation layers.
  2. **Interactive CLI Console**: Complete terminal menu system allowing interactive execution of all Admin, PM, and Team Member workflows with formatted tables, progress bars, and confirmation messages.
  3. **Embedded Web Server**: Zero-dependency Java HTTP Server (`com.sun.net.httpserver`) built right into `Main.java` to serve the web application.
  4. **Modern HTML5 Frontend**: Unified Master Portal with live Role Switcher plus dedicated standalone dashboards for each user type.

---

## 👥 User Types & Functionality Matrix

### 1. 🛡️ Administrator
* **User Management**:
  * **Input**: User details (`name`, `email`, `role`, `department`, `title`, `phone`).
  * **Output**: Confirmation message for successful user creation/update/deletion (`"✔ CONFIRMATION: User account created successfully."`).
  * **Functionality**: Manage user accounts and assign roles (`Admin`, `Project Manager`, `Team Member`).
* **Project Management**:
  * **Input**: Project details (`title`, `description`, `timeline`, `budget`).
  * **Output**: Confirmation message for successful project creation/update/deletion.
  * **Functionality**: Oversee enterprise project creation, updates, and removals.
* **System Settings**:
  * **Input**: Configuration settings (system name, maintenance mode toggle, email alerts, default task priority, session timeout, security level).
  * **Output**: Confirmation message for successful settings update.
  * **Functionality**: Manage system-wide settings.
* **Performance Metrics**:
  * **Output**: Reports and graphs showing system usage, user role distribution, project health, and velocity.

### 2. 💼 Project Manager
* **Project Creation**:
  * **Input**: Project details (`title`, `description`, `timeline`, `priority`, `budget`).
  * **Output**: Confirmation message for successful project creation.
  * **Functionality**: Create and manage projects.
* **Task Assignment**:
  * **Input**: Task details (`title`, `description`, `dueDate`, `priority`, `estimatedHours`), Assignee (`TeamMember`).
  * **Output**: Task assignment confirmation (`"✔ CONFIRMATION: Task successfully assigned."`).
  * **Functionality**: Assign tasks to team members.
* **Progress Monitoring**:
  * **Input**: Project progress data.
  * **Output**: Progress reports and visual progress bars/graphs.
  * **Functionality**: Monitor and report on project progress.
* **Dashboards**:
  1. *Project Overview*: List of projects with status updates.
  2. *Task Management*: Table of assigned tasks with update tracking.
  3. *Progress Reports*: Graphical/ASCII representation of progress & completion rates.
  4. *Team Member Overview*: List of team members with task allocations and efficiency metrics.

### 3. 👥 Team Member
* **Task Tracking**:
  * **Input**: Task updates (`status`, `progress percentage`, `logged hours`, `notes`).
  * **Output**: Updated task status confirmation.
  * **Functionality**: Track and update task progress.
* **Project Details**:
  * **Input**: Project selection.
  * **Output**: Access to project information, deadlines, and manager contacts.
  * **Functionality**: View project scope and timeline.
* **Profile Management**:
  * **Input**: Profile details (`name`, `email`, `password`, `phone`, `bio`).
  * **Output**: Confirmation message for successful profile update.
  * **Functionality**: Manage personal profile details.
* **Dashboards**:
  1. *Task List*: Assigned tasks table with quick status updater.
  2. *Project Details*: Timeline, milestones, and deliverables.
  3. *Profile Management*: Form for updating personal contact details.
  4. *Activity Feed*: Recent updates and notifications.

---

## ☕ Java OOP Architecture & Design Patterns

The backend demonstrates all core Object-Oriented Programming concepts:

1. **Abstraction**:
   * Abstract class `User` (`src/com/protrack/model/User.java`) defines common identity and abstract methods (`getDashboardName()`, `displayRoleCapabilities()`, `getDashboardRoute()`).
2. **Inheritance**:
   * `Admin`, `ProjectManager`, and `TeamMember` inherit from `User`, reusing core attributes and specializing behavior.
3. **Polymorphism**:
   * **Runtime / Dynamic Method Overriding**: Subclasses override `getDashboardName()` and `displayRoleCapabilities()` to render role-specific menus.
   * **Compile-Time Method Overloading**: Services implement overloaded query methods (e.g., `searchUsers(String query)`, `searchUsers(Role role)`, `searchUsers(String query, Role role)`).
4. **Encapsulation**:
   * All fields in `Project`, `Task`, `User`, and `SystemSettings` are `private`, accessed strictly via getters and validated setters (clamping progress between 0% and 100%, validating emails, formatting currency).
5. **Composition & Association**:
   * `Project` maintains associations with its `Task` deliverables and assigned `ProjectManager`.

---

## 📁 Project Directory Structure

```
java project/
├── compile.sh               # Shell script to compile all Java classes to bin/
├── run.sh                   # Shell script to run the Java console application
├── bin/                     # Compiled .class bytecode
├── src/
│   └── com/protrack/
│       ├── Main.java        # Main entry point (Console Menus & Web Server)
│       ├── model/
│       │   ├── Role.java            # Enum: ADMIN, PROJECT_MANAGER, TEAM_MEMBER
│       │   ├── TaskStatus.java      # Enum: PENDING, IN_PROGRESS, COMPLETED
│       │   ├── Priority.java        # Enum: LOW, MEDIUM, HIGH, URGENT
│       │   ├── User.java            # Abstract Base Class
│       │   ├── Admin.java           # Subclass extending User
│       │   ├── ProjectManager.java  # Subclass extending User
│       │   ├── TeamMember.java      # Subclass extending User
│       │   ├── Project.java         # Entity: encapsulates project scope & progress
│       │   ├── Task.java            # Entity: encapsulates work item deliverables
│       │   ├── SystemSettings.java  # Configuration entity
│       │   └── ActivityLog.java     # Audit log entity
│       ├── service/
│       │   ├── UserService.java     # User CRUD & overloaded search methods
│       │   ├── ProjectService.java  # Project CRUD & progress recalculation
│       │   ├── TaskService.java     # Task assignment & status updater
│       │   └── SystemSettingService.java # Settings configuration logic
│       └── util/
│           ├── ConsoleUI.java       # Formatted colored ASCII tables & banners
│           ├── DataInitializer.java # Baseline pre-seeded realistic dataset
│           └── WebServer.java       # Built-in lightweight Java HttpServer
├── index.html               # Frontend: Unified Portal with live Role Switcher
├── admin-dashboard.html      # Frontend: Dedicated Admin Dashboard
├── pm-dashboard.html         # Frontend: Dedicated Project Manager Dashboard
├── team-dashboard.html       # Frontend: Dedicated Team Member Dashboard
├── login.html                # Frontend: Role-based Persona Login Portal
├── css/
│   └── styles.css           # Complete SaaS design system
├── js/
│   ├── store.js             # Data store with localStorage persistence
│   ├── ui.js                # UI components, modals, toasts, SVG charts
│   └── dashboards.js        # Frontend CRUD controllers
└── README.md                # Project documentation
```

---

## 🚀 How to Run the Project

### Option 1: Run the Java Console Application (Interactive Menus)

Open your Terminal and run:

```bash
cd "/Users/krishchauhan/Desktop/java project"
./run.sh
```

Or manually:
```bash
javac -d bin src/com/protrack/model/*.java src/com/protrack/service/*.java src/com/protrack/util/*.java src/com/protrack/Main.java
java -cp bin com.protrack.Main
```

From the Main Menu, you can select:
* `1` &rarr; 🛡️ Admin Dashboard
* `2` &rarr; 💼 Project Manager Dashboard
* `3` &rarr; 👥 Team Member Dashboard
* `4` &rarr; 🌐 Launch Built-in Java Web Server (serves on `http://localhost:8080`)
* `5` &rarr; 🧪 Run Automated OOP Verification Tests

---

### Option 2: Run the Built-in Java Web Server Directly

To launch the Java application directly in Web Server mode:

```bash
./run.sh --server
```

Then open your browser to **`http://localhost:8080/`**.

---

### Option 3: View the Frontend Directly in Browser

Double-click or open **`index.html`** in any browser (Chrome, Safari, Edge, Firefox).
No server or build tool is needed — all styling and charting are self-contained.
