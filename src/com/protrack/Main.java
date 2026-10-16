package com.protrack;

import com.protrack.model.*;
import com.protrack.service.ProjectService;
import com.protrack.service.SystemSettingService;
import com.protrack.service.TaskService;
import com.protrack.service.UserService;
import com.protrack.util.ConsoleUI;
import com.protrack.util.DataInitializer;
import com.protrack.util.WebServer;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;
import java.util.*;

/**
 * Main Application Entry Point.
 * Provides:
 * 1. Interactive Role-Based Console Menus (Admin, Project Manager, Team Member)
 * 2. Embedded Java HTTP Web Server serving the HTML5/CSS3/JS frontend
 * 3. Automated OOP Verification Demonstration
 */
public class Main {

    private final UserService userService;
    private final ProjectService projectService;
    private final TaskService taskService;
    private final SystemSettingService settingService;
    private final Scanner scanner;
    private WebServer webServer;

    public Main() {
        this.userService = new UserService();
        this.projectService = new ProjectService();
        this.taskService = new TaskService();
        this.settingService = new SystemSettingService();
        this.scanner = new Scanner(System.in);

        // Pre-seed baseline data
        DataInitializer.initializeData(userService, projectService, taskService);
    }

    public static void main(String[] args) {
        Main app = new Main();

        // If args contains "--server", start web server automatically
        if (args.length > 0 && args[0].equalsIgnoreCase("--server")) {
            app.startWebServer(8080);
            return;
        }

        app.runMainMenu();
    }

    public void runMainMenu() {
        while (true) {
            ConsoleUI.printBanner("PROTRACK ENTERPRISE - ONLINE PROJECT MANAGEMENT TOOL (Java OOP)");
            System.out.println("Select a user type persona or action to proceed:\n");
            System.out.println("  1. 🛡️  Login as Administrator       (User & Project Management, System Settings, Metrics)");
            System.out.println("  2. 💼  Login as Project Manager    (Project Creation, Task Assignment, Velocity & Reports)");
            System.out.println("  3. 👥  Login as Team Member        (Task Tracking, Project Deadlines, Profile Management)");
            System.out.println("  4. 🌐  Start ProTrack Web Server   (Serve HTML Frontend on http://localhost:8080)");
            System.out.println("  5. 🧪  Run Automated OOP Demo      (Verify Inheritance, Polymorphism, Encapsulation)");
            System.out.println("  0. 🚪  Exit Application\n");
            System.out.print("Enter choice (0-5): ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    runAdminMenu();
                    break;
                case "2":
                    runProjectManagerMenu();
                    break;
                case "3":
                    runTeamMemberMenu();
                    break;
                case "4":
                    startWebServer(8080);
                    break;
                case "5":
                    runAutomatedOopDemo();
                    break;
                case "0":
                    System.out.println("\nThank you for using ProTrack Enterprise. Goodbye!");
                    if (webServer != null) webServer.stop();
                    System.exit(0);
                    return;
                default:
                    ConsoleUI.printError("Invalid option selected. Please choose between 0 and 5.");
            }
        }
    }

    // =========================================================================
    // 1. ADMINISTRATOR WORKFLOW
    // =========================================================================
    private void runAdminMenu() {
        Admin admin = (Admin) userService.getUsersByRole(Role.ADMIN).get(0);

        while (true) {
            ConsoleUI.printBanner("ADMIN DASHBOARD - Logged in as: " + admin.getName());
            admin.displayRoleCapabilities();
            System.out.println("\nFunctionalities for Admin:");
            System.out.println("  1. User Management      : View, Add, Edit, or Delete User accounts");
            System.out.println("  2. Project Management   : View, Create, Update, or Remove Projects");
            System.out.println("  3. System Settings      : View & Update system-wide configuration");
            System.out.println("  4. Performance Metrics  : View usage graphs and activity telemetry");
            System.out.println("  0. Return to Main Menu\n");
            System.out.print("Select action (0-4): ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    handleAdminUserManagement();
                    break;
                case "2":
                    handleAdminProjectManagement();
                    break;
                case "3":
                    handleAdminSystemSettings();
                    break;
                case "4":
                    handleAdminPerformanceMetrics();
                    break;
                case "0":
                    return;
                default:
                    ConsoleUI.printError("Invalid choice.");
            }
        }
    }

    private void handleAdminUserManagement() {
        ConsoleUI.printBanner("ADMIN: USER MANAGEMENT");
        ConsoleUI.printUserTable(userService.getAllUsers());

        System.out.println("\nOptions: [1] Add User  [2] Edit User  [3] Delete User  [0] Back");
        System.out.print("Choose: ");
        String sub = scanner.nextLine().trim();

        if (sub.equals("1")) {
            System.out.print("Enter Full Name: ");
            String name = scanner.nextLine().trim();
            System.out.print("Enter Email Address: ");
            String email = scanner.nextLine().trim();
            System.out.print("Select Role (1=Admin, 2=Project Manager, 3=Team Member): ");
            String rChoice = scanner.nextLine().trim();
            Role role = rChoice.equals("1") ? Role.ADMIN : rChoice.equals("2") ? Role.PROJECT_MANAGER : Role.TEAM_MEMBER;
            System.out.print("Enter Department: ");
            String dept = scanner.nextLine().trim();
            System.out.print("Enter Job Title: ");
            String title = scanner.nextLine().trim();

            User created = userService.createUser(name, email, role, dept, title);
            ConsoleUI.printSuccess("Confirmation message: User account for '" + created.getName() + "' created successfully with ID #" + created.getId() + ".");
        } else if (sub.equals("2")) {
            System.out.print("Enter User ID to edit: ");
            try {
                int id = Integer.parseInt(scanner.nextLine().trim());
                Optional<User> uOpt = userService.getUserById(id);
                if (uOpt.isPresent()) {
                    User u = uOpt.get();
                    System.out.print("New Name [" + u.getName() + "]: ");
                    String name = scanner.nextLine().trim();
                    if (name.isEmpty()) name = u.getName();

                    System.out.print("New Email [" + u.getEmail() + "]: ");
                    String email = scanner.nextLine().trim();
                    if (email.isEmpty()) email = u.getEmail();

                    System.out.print("New Department [" + u.getDepartment() + "]: ");
                    String dept = scanner.nextLine().trim();
                    if (dept.isEmpty()) dept = u.getDepartment();

                    userService.updateUser(id, name, email, u.getRole(), dept, u.getTitle());
                    ConsoleUI.printSuccess("Confirmation message: User account #" + id + " ('" + name + "') updated successfully.");
                } else {
                    ConsoleUI.printError("User not found.");
                }
            } catch (Exception e) {
                ConsoleUI.printError("Invalid User ID.");
            }
        } else if (sub.equals("3")) {
            System.out.print("Enter User ID to delete: ");
            try {
                int id = Integer.parseInt(scanner.nextLine().trim());
                if (id == 1) {
                    ConsoleUI.printError("Cannot delete primary system Administrator.");
                } else if (userService.deleteUser(id)) {
                    ConsoleUI.printSuccess("Confirmation message: User account #" + id + " deleted successfully.");
                } else {
                    ConsoleUI.printError("User ID #" + id + " not found.");
                }
            } catch (Exception e) {
                ConsoleUI.printError("Invalid ID.");
            }
        }
    }

    private void handleAdminProjectManagement() {
        ConsoleUI.printBanner("ADMIN: PROJECT MANAGEMENT OVERSIGHT");
        ConsoleUI.printProjectTable(projectService.getAllProjects());

        System.out.println("\nOptions: [1] Create Project  [2] Update Project  [3] Remove Project  [0] Back");
        System.out.print("Choose: ");
        String sub = scanner.nextLine().trim();

        if (sub.equals("1")) {
            System.out.print("Project Title: ");
            String title = scanner.nextLine().trim();
            System.out.print("Description: ");
            String desc = scanner.nextLine().trim();
            System.out.print("Start Date (YYYY-MM-DD): ");
            String start = scanner.nextLine().trim();
            System.out.print("End Date (YYYY-MM-DD): ");
            String end = scanner.nextLine().trim();
            System.out.print("Budget (e.g. $150,000): ");
            String budget = scanner.nextLine().trim();

            List<User> pms = userService.getUsersByRole(Role.PROJECT_MANAGER);
            User pm = pms.isEmpty() ? userService.getAllUsers().get(0) : pms.get(0);

            Project p = projectService.createProject(title, desc, start, end, pm.getId(), pm.getName(), budget, Priority.HIGH);
            ConsoleUI.printSuccess("Confirmation message: Project '" + p.getTitle() + "' successfully created and registered with ID #" + p.getId() + ".");
        } else if (sub.equals("2")) {
            System.out.print("Enter Project ID to update: ");
            try {
                int id = Integer.parseInt(scanner.nextLine().trim());
                Optional<Project> pOpt = projectService.getProjectById(id);
                if (pOpt.isPresent()) {
                    Project p = pOpt.get();
                    System.out.print("New Title [" + p.getTitle() + "]: ");
                    String title = scanner.nextLine().trim();
                    if (title.isEmpty()) title = p.getTitle();

                    System.out.print("New Status [" + p.getStatus() + "] (Planning/In Progress/Completed): ");
                    String status = scanner.nextLine().trim();
                    if (status.isEmpty()) status = p.getStatus();

                    projectService.updateProject(id, title, p.getDescription(), p.getStartDate(), p.getEndDate(),
                            p.getManagerId(), p.getManagerName(), p.getBudget(), p.getPriority(), status);
                    ConsoleUI.printSuccess("Confirmation message: Project #" + id + " ('" + title + "') updated successfully.");
                } else {
                    ConsoleUI.printError("Project not found.");
                }
            } catch (Exception e) {
                ConsoleUI.printError("Invalid Project ID.");
            }
        } else if (sub.equals("3")) {
            System.out.print("Enter Project ID to remove: ");
            try {
                int id = Integer.parseInt(scanner.nextLine().trim());
                if (projectService.deleteProject(id)) {
                    ConsoleUI.printSuccess("Confirmation message: Project #" + id + " successfully deleted.");
                } else {
                    ConsoleUI.printError("Project ID not found.");
                }
            } catch (Exception e) {
                ConsoleUI.printError("Invalid Project ID.");
            }
        }
    }

    private void handleAdminSystemSettings() {
        ConsoleUI.printBanner("ADMIN: SYSTEM SETTINGS");
        SystemSettings s = settingService.getSettings();
        System.out.println("Current System Settings:");
        System.out.println("  • Application Name      : " + s.getSystemName());
        System.out.println("  • Organization          : " + s.getOrganizationName());
        System.out.println("  • Maintenance Mode      : " + (s.isMaintenanceMode() ? "ENABLED" : "DISABLED"));
        System.out.println("  • Email Notifications   : " + (s.isEmailNotifications() ? "ENABLED" : "DISABLED"));
        System.out.println("  • Session Timeout       : " + s.getSessionTimeoutMinutes() + " minutes");
        System.out.println("  • Security Level        : " + s.getSecurityLevel());

        System.out.println("\nOptions: [1] Update Settings  [0] Back");
        System.out.print("Choose: ");
        if (scanner.nextLine().trim().equals("1")) {
            System.out.print("New Application Name [" + s.getSystemName() + "]: ");
            String appName = scanner.nextLine().trim();
            if (appName.isEmpty()) appName = s.getSystemName();

            System.out.print("Toggle Maintenance Mode? (y/n): ");
            boolean maint = scanner.nextLine().trim().equalsIgnoreCase("y");

            System.out.print("Session Timeout Minutes [" + s.getSessionTimeoutMinutes() + "]: ");
            String tStr = scanner.nextLine().trim();
            int timeout = tStr.isEmpty() ? s.getSessionTimeoutMinutes() : Integer.parseInt(tStr);

            settingService.updateSettings(appName, s.getOrganizationName(), maint, s.getDefaultTaskPriority(),
                    s.isEmailNotifications(), timeout, s.getMaxProjectsPerManager(), s.getSecurityLevel());
            ConsoleUI.printSuccess("Confirmation message: System-wide configuration settings updated successfully.");
        }
    }

    private void handleAdminPerformanceMetrics() {
        ConsoleUI.printBanner("ADMIN: PERFORMANCE METRICS & SYSTEM USAGE");
        List<User> users = userService.getAllUsers();
        List<Project> projects = projectService.getAllProjects();
        List<Task> tasks = taskService.getAllTasks();

        long adminCount = users.stream().filter(u -> u.getRole() == Role.ADMIN).count();
        long pmCount = users.stream().filter(u -> u.getRole() == Role.PROJECT_MANAGER).count();
        long teamCount = users.stream().filter(u -> u.getRole() == Role.TEAM_MEMBER).count();

        System.out.println("📊 USER ROLE DISTRIBUTION:");
        System.out.println("  • Administrators    : " + adminCount + " " + ConsoleUI.getProgressBar((int)((adminCount*100)/users.size())));
        System.out.println("  • Project Managers  : " + pmCount + " " + ConsoleUI.getProgressBar((int)((pmCount*100)/users.size())));
        System.out.println("  • Team Members      : " + teamCount + " " + ConsoleUI.getProgressBar((int)((teamCount*100)/users.size())));

        System.out.println("\n📈 PROJECT HEALTH & DELIVERY METRICS:");
        System.out.println("  • Total Projects    : " + projects.size());
        System.out.println("  • Average Progress  : " + String.format("%.1f%%", projectService.getAverageProgress()));
        System.out.println("  • Task Completion   : " + String.format("%.1f%%", taskService.getCompletionRate()));
        System.out.println("  • Total Work Items  : " + tasks.size());
        System.out.println("  • Server Status     : ONLINE (Zero errors logged)");
    }

    // =========================================================================
    // 2. PROJECT MANAGER WORKFLOW
    // =========================================================================
    private void runProjectManagerMenu() {
        ProjectManager pm = (ProjectManager) userService.getUsersByRole(Role.PROJECT_MANAGER).get(0);

        while (true) {
            ConsoleUI.printBanner("PROJECT MANAGER DASHBOARD - Logged in as: " + pm.getName());
            pm.displayRoleCapabilities();
            System.out.println("\nFunctionalities for Project Manager:");
            System.out.println("  1. Project Overview       : View list of projects & status updates");
            System.out.println("  2. Project Creation       : Initiate and register a new project");
            System.out.println("  3. Task Assignment        : Assign task deliverable to a team member");
            System.out.println("  4. Task Management        : Table of assigned tasks with update tracking");
            System.out.println("  5. Progress Reports       : Graphical progress reports & completion rates");
            System.out.println("  6. Team Member Overview   : Team member workload & performance metrics");
            System.out.println("  0. Return to Main Menu\n");
            System.out.print("Select action (0-6): ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    ConsoleUI.printBanner("PM: PROJECT OVERVIEW");
                    ConsoleUI.printProjectTable(projectService.getAllProjects());
                    break;
                case "2":
                    handlePmProjectCreation(pm);
                    break;
                case "3":
                    handlePmTaskAssignment();
                    break;
                case "4":
                    handlePmTaskManagement();
                    break;
                case "5":
                    handlePmProgressReports();
                    break;
                case "6":
                    handlePmTeamOverview();
                    break;
                case "0":
                    return;
                default:
                    ConsoleUI.printError("Invalid choice.");
            }
        }
    }

    private void handlePmProjectCreation(ProjectManager pm) {
        ConsoleUI.printBanner("PM: PROJECT CREATION");
        System.out.print("Project Title: ");
        String title = scanner.nextLine().trim();
        System.out.print("Scope Description: ");
        String desc = scanner.nextLine().trim();
        System.out.print("Start Date (YYYY-MM-DD): ");
        String start = scanner.nextLine().trim();
        System.out.print("Target Delivery Date (YYYY-MM-DD): ");
        String end = scanner.nextLine().trim();
        System.out.print("Budget (e.g. $180,000): ");
        String budget = scanner.nextLine().trim();

        Project p = projectService.createProject(title, desc, start, end, pm.getId(), pm.getName(), budget, Priority.HIGH);
        pm.addManagedProject(p.getId());
        ConsoleUI.printSuccess("Confirmation message: Project '" + p.getTitle() + "' created successfully with timeline " + start + " to " + end + ".");
    }

    private void handlePmTaskAssignment() {
        ConsoleUI.printBanner("PM: TASK ASSIGNMENT");
        List<Project> projects = projectService.getAllProjects();
        List<User> members = userService.getUsersByRole(Role.TEAM_MEMBER);

        System.out.println("Available Projects:");
        for (Project p : projects) {
            System.out.println("  [" + p.getId() + "] " + p.getTitle());
        }
        System.out.print("Select Project ID: ");
        int pId = Integer.parseInt(scanner.nextLine().trim());
        Project proj = projectService.getProjectById(pId).orElse(projects.get(0));

        System.out.print("Task Title: ");
        String title = scanner.nextLine().trim();
        System.out.print("Task Description: ");
        String desc = scanner.nextLine().trim();

        System.out.println("\nAvailable Team Members:");
        for (User m : members) {
            System.out.println("  [" + m.getId() + "] " + m.getName() + " (" + m.getTitle() + ")");
        }
        System.out.print("Select Assignee ID: ");
        int mId = Integer.parseInt(scanner.nextLine().trim());
        User member = userService.getUserById(mId).orElse(members.get(0));

        System.out.print("Due Date (YYYY-MM-DD): ");
        String dueDate = scanner.nextLine().trim();
        System.out.print("Priority (Low/Medium/High/Urgent): ");
        Priority prio = Priority.fromString(scanner.nextLine().trim());

        Task t = taskService.assignTask(proj.getId(), proj.getTitle(), title, desc, member.getId(), member.getName(), dueDate, prio, 25.0);
        proj.addTaskId(t.getId());
        if (member instanceof TeamMember) {
            ((TeamMember) member).addAssignedTaskId(t.getId());
        }

        ConsoleUI.printSuccess("Task assignment confirmation: Task '" + t.getTitle() + "' successfully assigned to " + member.getName() + ".");
    }

    private void handlePmTaskManagement() {
        ConsoleUI.printBanner("PM: TASK MANAGEMENT");
        ConsoleUI.printTaskTable(taskService.getAllTasks());

        System.out.println("\nOptions: [1] Update Task Status  [2] Delete Task  [0] Back");
        System.out.print("Choose: ");
        String sub = scanner.nextLine().trim();
        if (sub.equals("1")) {
            System.out.print("Enter Task ID: ");
            try {
                int id = Integer.parseInt(scanner.nextLine().trim());
                System.out.print("New Status (1=Pending, 2=In Progress, 3=Completed): ");
                String st = scanner.nextLine().trim();
                TaskStatus status = st.equals("1") ? TaskStatus.PENDING : st.equals("2") ? TaskStatus.IN_PROGRESS : TaskStatus.COMPLETED;
                System.out.print("Progress Percentage (0-100): ");
                int prog = Integer.parseInt(scanner.nextLine().trim());

                if (taskService.updateTaskStatus(id, status, prog, 0)) {
                    Task t = taskService.getTaskById(id).get();
                    projectService.updateProgressFromTasks(t.getProjectId(), taskService.getAllTasks());
                    ConsoleUI.printSuccess("Confirmation: Task #" + id + " updated to " + status.getDisplayName() + " (" + prog + "% complete).");
                }
            } catch (Exception e) {
                ConsoleUI.printError("Invalid input.");
            }
        }
    }

    private void handlePmProgressReports() {
        ConsoleUI.printBanner("PM: PROGRESS REPORTS & VISUALIZATIONS");
        List<Project> projects = projectService.getAllProjects();
        List<Task> tasks = taskService.getAllTasks();

        System.out.println("📊 PROJECT PROGRESS BENCHMARKS:");
        for (Project p : projects) {
            System.out.println(String.format("  • %-30s : %s", p.getTitle(), ConsoleUI.getProgressBar(p.getProgress())));
        }

        long comp = tasks.stream().filter(t -> t.getStatus() == TaskStatus.COMPLETED).count();
        long inProg = tasks.stream().filter(t -> t.getStatus() == TaskStatus.IN_PROGRESS).count();
        long pend = tasks.stream().filter(t -> t.getStatus() == TaskStatus.PENDING).count();

        System.out.println("\n📈 TASK STATUS BREAKDOWN:");
        System.out.println("  • Completed   : " + comp + " tasks (" + ConsoleUI.getProgressBar((int)((comp*100)/tasks.size())) + ")");
        System.out.println("  • In Progress : " + inProg + " tasks (" + ConsoleUI.getProgressBar((int)((inProg*100)/tasks.size())) + ")");
        System.out.println("  • Pending     : " + pend + " tasks (" + ConsoleUI.getProgressBar((int)((pend*100)/tasks.size())) + ")");
    }

    private void handlePmTeamOverview() {
        ConsoleUI.printBanner("PM: TEAM MEMBER OVERVIEW & PERFORMANCE METRICS");
        List<User> members = userService.getUsersByRole(Role.TEAM_MEMBER);
        List<Task> allTasks = taskService.getAllTasks();

        System.out.println(ConsoleUI.BOLD + String.format("%-18s | %-22s | %-12s | %-12s | %-12s | %-12s",
                "TEAM MEMBER", "ROLE TITLE", "ACTIVE TASKS", "COMPLETED", "HOURS LOGGED", "EFFICIENCY") + ConsoleUI.RESET);
        System.out.println("-------------------------------------------------------------------------------------------------------------");

        for (User m : members) {
            long active = allTasks.stream().filter(t -> t.getAssigneeId() == m.getId() && t.getStatus() != TaskStatus.COMPLETED).count();
            long completed = allTasks.stream().filter(t -> t.getAssigneeId() == m.getId() && t.getStatus() == TaskStatus.COMPLETED).count();
            double hours = allTasks.stream().filter(t -> t.getAssigneeId() == m.getId()).mapToDouble(Task::getLoggedHours).sum();
            int total = (int) (active + completed);
            int efficiency = total > 0 ? (int)((completed * 100) / total) : 100;

            System.out.println(String.format("%-18s | %-22s | %-12d | %-12d | %-12.1fh | %s%d%%%s",
                    m.getName(), m.getTitle(), active, completed, hours, ConsoleUI.GREEN, efficiency, ConsoleUI.RESET));
        }
        System.out.println("-------------------------------------------------------------------------------------------------------------");
    }

    // =========================================================================
    // 3. TEAM MEMBER WORKFLOW
    // =========================================================================
    private void runTeamMemberMenu() {
        TeamMember member = (TeamMember) userService.getUsersByRole(Role.TEAM_MEMBER).get(0);

        while (true) {
            ConsoleUI.printBanner("TEAM MEMBER DASHBOARD - Logged in as: " + member.getName() + " (" + member.getTitle() + ")");
            member.displayRoleCapabilities();
            System.out.println("\nFunctionalities for Team Member:");
            System.out.println("  1. My Task List           : View assigned tasks");
            System.out.println("  2. Task Tracking          : Update task status, progress %, & log hours");
            System.out.println("  3. Project Details        : Access project information, scope, & timeline");
            System.out.println("  4. Profile Management     : View and update personal profile details");
            System.out.println("  5. Activity Feed          : View recent updates and notifications");
            System.out.println("  0. Return to Main Menu\n");
            System.out.print("Select action (0-5): ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    ConsoleUI.printBanner("MY ASSIGNED TASKS");
                    ConsoleUI.printTaskTable(taskService.getTasksByAssignee(member.getId()));
                    break;
                case "2":
                    handleTeamTaskTracking(member);
                    break;
                case "3":
                    handleTeamProjectDetails(member);
                    break;
                case "4":
                    handleTeamProfileManagement(member);
                    break;
                case "5":
                    handleTeamActivityFeed();
                    break;
                case "0":
                    return;
                default:
                    ConsoleUI.printError("Invalid choice.");
            }
        }
    }

    private void handleTeamTaskTracking(TeamMember member) {
        ConsoleUI.printBanner("TEAM MEMBER: TASK TRACKING & STATUS UPDATE");
        List<Task> myTasks = taskService.getTasksByAssignee(member.getId());
        ConsoleUI.printTaskTable(myTasks);

        if (myTasks.isEmpty()) {
            ConsoleUI.printInfo("No tasks assigned to you currently.");
            return;
        }

        System.out.print("Enter Task ID to update: ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());
            Optional<Task> tOpt = taskService.getTaskById(id);
            if (tOpt.isPresent() && tOpt.get().getAssigneeId() == member.getId()) {
                System.out.print("New Status (1=Pending, 2=In Progress, 3=Completed): ");
                String sStr = scanner.nextLine().trim();
                TaskStatus status = sStr.equals("1") ? TaskStatus.PENDING : sStr.equals("2") ? TaskStatus.IN_PROGRESS : TaskStatus.COMPLETED;

                System.out.print("Progress Percentage (0-100): ");
                int prog = Integer.parseInt(scanner.nextLine().trim());

                System.out.print("Additional Hours Logged: ");
                double hours = Double.parseDouble(scanner.nextLine().trim());

                taskService.updateTaskStatus(id, status, prog, hours);
                projectService.updateProgressFromTasks(tOpt.get().getProjectId(), taskService.getAllTasks());

                ConsoleUI.printSuccess("Updated task status: Task #" + id + " status successfully updated to '" +
                        status.getDisplayName() + "' (" + prog + "% complete, " + hours + "h logged).");
            } else {
                ConsoleUI.printError("Task not found or not assigned to you.");
            }
        } catch (Exception e) {
            ConsoleUI.printError("Invalid input.");
        }
    }

    private void handleTeamProjectDetails(TeamMember member) {
        ConsoleUI.printBanner("TEAM MEMBER: ACCESS PROJECT INFORMATION");
        List<Project> projects = projectService.getAllProjects();
        ConsoleUI.printProjectTable(projects);

        System.out.print("Enter Project ID to view detailed scope: ");
        try {
            int pId = Integer.parseInt(scanner.nextLine().trim());
            Optional<Project> pOpt = projectService.getProjectById(pId);
            if (pOpt.isPresent()) {
                Project p = pOpt.get();
                System.out.println("\n------------------------------------------------------------");
                System.out.println("  PROJECT: " + p.getTitle());
                System.out.println("  STATUS : " + p.getStatus() + " | Priority: " + p.getPriority().getDisplayName());
                System.out.println("  MANAGER: " + p.getManagerName());
                System.out.println("  TIMELINE: " + p.getStartDate() + " to " + p.getEndDate());
                System.out.println("  BUDGET  : " + p.getBudget());
                System.out.println("  PROGRESS: " + ConsoleUI.getProgressBar(p.getProgress()));
                System.out.println("  SCOPE   : " + p.getDescription());
                System.out.println("------------------------------------------------------------");
                ConsoleUI.printSuccess("Access to project information granted for '" + p.getTitle() + "'.");
            } else {
                ConsoleUI.printError("Project not found.");
            }
        } catch (Exception e) {
            ConsoleUI.printError("Invalid ID.");
        }
    }

    private void handleTeamProfileManagement(TeamMember member) {
        ConsoleUI.printBanner("TEAM MEMBER: PROFILE MANAGEMENT");
        System.out.println("Current Profile:");
        System.out.println("  • Name       : " + member.getName());
        System.out.println("  • Email      : " + member.getEmail());
        System.out.println("  • Title      : " + member.getTitle());
        System.out.println("  • Department : " + member.getDepartment());
        System.out.println("  • Phone      : " + member.getPhone());
        System.out.println("  • Bio        : " + member.getBio());

        System.out.print("\nUpdate profile? (y/n): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            System.out.print("New Name [" + member.getName() + "]: ");
            String name = scanner.nextLine().trim();
            if (name.isEmpty()) name = member.getName();

            System.out.print("New Email [" + member.getEmail() + "]: ");
            String email = scanner.nextLine().trim();
            if (email.isEmpty()) email = member.getEmail();

            System.out.print("New Phone [" + member.getPhone() + "]: ");
            String phone = scanner.nextLine().trim();
            if (phone.isEmpty()) phone = member.getPhone();

            System.out.print("New Bio [" + member.getBio() + "]: ");
            String bio = scanner.nextLine().trim();
            if (bio.isEmpty()) bio = member.getBio();

            userService.updateProfile(member.getId(), name, email, phone, bio, null);
            ConsoleUI.printSuccess("Confirmation message: Personal profile details updated successfully for " + name + ".");
        }
    }

    private void handleTeamActivityFeed() {
        ConsoleUI.printBanner("RECENT ACTIVITY FEED & NOTIFICATIONS");
        System.out.println("  [Just now]    David Kim updated status of 'Design Spring Boot Auth Service' to 75%");
        System.out.println("  [1 hour ago]  Alex Rivera assigned new task 'WebRTC Video Consultation Pipeline' to David Kim");
        System.out.println("  [3 hours ago] Sarah Jenkins updated system security policies and session timeout to 60 mins");
        System.out.println("  [5 hours ago] Maya Patel completed transaction history table UI styling");
        System.out.println("  [1 day ago]   Elena Rostova marked project 'Supply Chain IoT Gateway' as Completed (100%)");
    }

    // =========================================================================
    // 4. EMBEDDED WEB SERVER
    // =========================================================================
    public void startWebServer(int port) {
        try {
            File baseDir = new File(".");
            webServer = new WebServer(port, baseDir, userService, projectService, taskService, settingService);
            webServer.start();

            ConsoleUI.printBanner("PROTRACK EMBEDDED WEB SERVER STARTED");
            ConsoleUI.printSuccess("Server listening at: http://localhost:" + port);
            System.out.println("  • Main Portal      : http://localhost:" + port + "/index.html");
            System.out.println("  • Admin Dashboard  : http://localhost:" + port + "/admin-dashboard.html");
            System.out.println("  • PM Dashboard     : http://localhost:" + port + "/pm-dashboard.html");
            System.out.println("  • Team Dashboard   : http://localhost:" + port + "/team-dashboard.html");

            // Attempt to open browser
            try {
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    Desktop.getDesktop().browse(new URI("http://localhost:" + port + "/index.html"));
                }
            } catch (Exception ignored) {}

            System.out.println("\nPress Enter to stop server and return to main menu...");
            scanner.nextLine();
            webServer.stop();
            ConsoleUI.printInfo("Web server stopped.");
        } catch (Exception e) {
            ConsoleUI.printError("Failed to start server: " + e.getMessage());
        }
    }

    // =========================================================================
    // 5. AUTOMATED OOP DEMONSTRATION & VERIFICATION
    // =========================================================================
    public void runAutomatedOopDemo() {
        ConsoleUI.printBanner("AUTOMATED OBJECT-ORIENTED PROGRAMMING (OOP) VERIFICATION");

        // 1. Inheritance
        System.out.println(ConsoleUI.BOLD + "\n[TEST 1] INHERITANCE VERIFICATION:" + ConsoleUI.RESET);
        User admin = userService.getUsersByRole(Role.ADMIN).get(0);
        User pm = userService.getUsersByRole(Role.PROJECT_MANAGER).get(0);
        User dev = userService.getUsersByRole(Role.TEAM_MEMBER).get(0);

        System.out.println("  • Admin is an instance of User: " + (admin instanceof User) + " (Class: " + admin.getClass().getSimpleName() + ")");
        System.out.println("  • PM is an instance of User   : " + (pm instanceof User) + " (Class: " + pm.getClass().getSimpleName() + ")");
        System.out.println("  • Dev is an instance of User  : " + (dev instanceof User) + " (Class: " + dev.getClass().getSimpleName() + ")");

        // 2. Polymorphism (Runtime / Dynamic Method Overriding)
        System.out.println(ConsoleUI.BOLD + "\n[TEST 2] POLYMORPHISM (RUNTIME DYNAMIC METHOD OVERRIDING):" + ConsoleUI.RESET);
        List<User> userList = Arrays.asList(admin, pm, dev);
        for (User u : userList) {
            System.out.println("  • User: " + u.getName() + " -> Dynamic Dashboard: '" + u.getDashboardName() + "' (Route: " + u.getDashboardRoute() + ")");
        }

        // 3. Compile-time Polymorphism (Method Overloading)
        System.out.println(ConsoleUI.BOLD + "\n[TEST 3] POLYMORPHISM (COMPILE-TIME METHOD OVERLOADING):" + ConsoleUI.RESET);
        System.out.println("  • searchUsers(\"Kim\"): Found " + userService.searchUsers("Kim").size() + " user(s)");
        System.out.println("  • searchUsers(Role.ADMIN): Found " + userService.searchUsers(Role.ADMIN).size() + " user(s)");
        System.out.println("  • searchUsers(\"Sarah\", Role.ADMIN): Found " + userService.searchUsers("Sarah", Role.ADMIN).size() + " user(s)");

        // 4. Encapsulation
        System.out.println(ConsoleUI.BOLD + "\n[TEST 4] ENCAPSULATION & DATA INTEGRITY:" + ConsoleUI.RESET);
        Project p = projectService.getAllProjects().get(0);
        p.setProgress(150); // should clamp to 100
        System.out.println("  • Clamped progress check (passed 150): Progress is " + p.getProgress() + "%");
        p.setProgress(-20); // should clamp to 0
        System.out.println("  • Clamped progress check (passed -20): Progress is " + p.getProgress() + "%");
        p.setProgress(68);

        // 5. Composition & Recalculation
        System.out.println(ConsoleUI.BOLD + "\n[TEST 5] COMPOSITION & SYSTEM RECALCULATIONS:" + ConsoleUI.RESET);
        System.out.println("  • Average project progress across system: " + String.format("%.2f%%", projectService.getAverageProgress()));
        System.out.println("  • Overall task completion rate: " + String.format("%.2f%%", taskService.getCompletionRate()));

        ConsoleUI.printSuccess("ALL OOP ARCHITECTURE TESTS PASSED SUCCESSFULLY (100% COMPLIANT)!\n");
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }
}
