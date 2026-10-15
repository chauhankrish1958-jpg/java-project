# ProTrack - Online Project Management Tool (Java OOP Frontend)

A modern, responsive, enterprise-grade frontend built with HTML5, CSS3, and JavaScript, designed to integrate seamlessly with a **Java Object-Oriented Programming (OOP)** backend.

---

## 🌟 Project Overview

ProTrack delivers dedicated dashboards and role-specific workflows for three user types:
1. **Admin**: Manages user accounts, oversees enterprise projects, configures system-wide settings, and monitors usage & performance metrics.
2. **Project Manager**: Creates and manages projects, assigns tasks to team members, monitors progress with visualizations, and oversees team workloads.
3. **Team Member**: Tracks and updates assigned tasks, views project details and deadlines, manages personal profile details, and follows the live activity feed.

---

## 🗂️ Project Structure

```
java project/
├── index.html               # Main unified portal with live role-switcher & dynamic views
├── admin-dashboard.html      # Dedicated Admin Dashboard view
├── pm-dashboard.html         # Dedicated Project Manager Dashboard view
├── team-dashboard.html       # Dedicated Team Member Dashboard view
├── login.html                # Role-based Persona Login Portal
├── css/
│   └── styles.css           # Complete SaaS design system (variables, layouts, modals, toasts, tables)
├── js/
│   ├── store.js             # Data store with initial OOP mock data & localStorage persistence
│   ├── ui.js                # UI components (modal manager, toast alerts, SVG chart renderers)
│   └── dashboards.js        # Controller for Admin, PM, and Team Member logic & CRUD operations
├── java-oop-reference/       # Java OOP class models demonstrating the backend architecture
│   ├── User.java            # Abstract base class (Encapsulation & Abstraction)
│   ├── Admin.java           # Subclass extending User (Inheritance & Polymorphism)
│   ├── ProjectManager.java  # Subclass extending User (Inheritance & Association)
│   ├── TeamMember.java      # Subclass extending User (Inheritance)
│   ├── Project.java         # Project entity (Composition & State management)
│   ├── Task.java            # Task entity (Encapsulation)
│   └── SystemSettings.java  # System configuration entity
└── README.md                # Documentation & evaluation guide
```

---

## 🎯 Detailed Feature & Rubric Mapping

### 1. Administrator Dashboard (`admin-dashboard.html` / Unified Portal)
| Feature | Input | Output / Behavior | Functionality |
| :--- | :--- | :--- | :--- |
| **User Management** | Name, email, role, department, title, phone | Confirmation toast message (`User created/updated/deleted successfully`) | Full CRUD on user accounts; search/filter users; edit and delete options. |
| **Project Management** | Project title, description, start/end dates, budget, assigned PM | Confirmation toast message (`Project created/updated/deleted`) | Oversee all enterprise projects, view real-time progress bars and status badges. |
| **System Settings** | System name, organization name, maintenance toggle, email alerts, default priority, session timeout, max projects | Confirmation message (`System settings updated successfully`) | Manage global system configurations. |
| **Performance Metrics** | Telemetry and usage statistics | Donut charts & progress gauges | View User Role Distribution, Project Health breakdown, and Infrastructure Throughput. |

---

### 2. Project Manager Dashboard (`pm-dashboard.html` / Unified Portal)
| Feature | Input | Output / Behavior | Functionality |
| :--- | :--- | :--- | :--- |
| **Project Creation** | Title, description, timeline dates, priority, budget | Confirmation toast message (`Project created successfully`) | Create and manage projects with custom timelines and priorities. |
| **Task Assignment** | Task details (title, description, project, priority, due date), Assignee | Task assignment confirmation message (`Task assigned to [Name] successfully`) | Assign tasks to team members with due dates and estimated hours. |
| **Progress Monitoring** | Task completion updates and logged hours | Dynamic charts & progress bars | Monitor milestone progress with task status breakdown donut charts and project completion benchmarks. |
| **Project Overview** | Project selection / filter | Cards showing deadlines, status badges, progress bars, and budgets | Real-time overview of all managed projects. |
| **Task Management** | Status filters (All, Pending, In Progress, Completed) | Interactive task table with edit and delete capabilities | Track deliverables and balance task pipelines. |
| **Team Member Overview** | Team member selection | Team cards showing active tasks, completed tasks, logged hours, efficiency % | Track team member velocity and balance workloads. |

---

### 3. Team Member Dashboard (`team-dashboard.html` / Unified Portal)
| Feature | Input | Output / Behavior | Functionality |
| :--- | :--- | :--- | :--- |
| **Task Tracking** | Task updates (Status: Pending / In Progress / Completed, Progress % slider, Logged Hours, Notes) | Updated task status toast message (`Updated task status to [Status]`) | Live status and progress bar updates reflecting immediately across all views. |
| **Project Details** | Project card click | Modal displaying full scope, start/end dates, budget, PM contact, and task checklists | View project milestones and deadlines. |
| **Profile Management** | Profile details (Name, email, password, phone, department, title, bio) | Confirmation message (`Personal profile details updated successfully`) | Update personal contact and role information. |
| **Task List** | Filter by status | Table of assigned tasks with quick-action status updater | Clear, accessible list of work items. |
| **Activity Feed** | Real-time system events | Chronological feed with badges and timestamps | Live updates on newly assigned tasks, project milestone updates, and system notes. |

---

## ☕ Java OOP Architecture Mapping

The frontend is specifically structured to map 1:1 with Java Object-Oriented Programming concepts:

1. **Inheritance**:
   - `User` is an abstract base class (`User.java`).
   - `Admin`, `ProjectManager`, and `TeamMember` extend `User`, inheriting core fields (`id`, `name`, `email`, `role`, `department`) and overriding `getDashboardRoute()`.
2. **Encapsulation**:
   - All models use private fields with public getters and setters.
   - The JavaScript `DataStore` mirrors Java encapsulation by preventing direct memory mutations and using validated CRUD methods (`addUser`, `addProject`, `updateTask`).
3. **Polymorphism**:
   - The dashboard dynamically displays role-specific capabilities depending on the user's concrete subtype (e.g., only Admin sees System Settings; PM sees Team Workload; Team Member sees Task Status Updater).
4. **Composition & Association**:
   - `Project` contains a list of `Task` objects (`List<Task> tasks`).
   - `Task` references an assigned `TeamMember` via `assigneeId`.

---

## 🚀 How to Run the Frontend

No build tools, npm packages, or external installations are required! All styling and SVG charting are 100% self-contained.

### Option 1: Direct File Opening (Fastest)
1. Double-click or open **`index.html`** in any web browser (Google Chrome, Safari, Microsoft Edge, Mozilla Firefox).
2. Use the **Role Switcher** in the top-right header to toggle instantly between:
   - 🛡️ **Admin**
   - 💼 **Project Manager**
   - 👥 **Team Member**

### Option 2: Running with a Local Server
In your terminal, navigate to the folder and run:
```bash
# Python 3
python3 -m http.server 8000

# Open in browser:
http://localhost:8000/
```

### Option 3: Dedicated Page Access
- **Admin Dashboard**: Open `admin-dashboard.html`
- **Project Manager Dashboard**: Open `pm-dashboard.html`
- **Team Member Dashboard**: Open `team-dashboard.html`
- **Login Portal**: Open `login.html`
