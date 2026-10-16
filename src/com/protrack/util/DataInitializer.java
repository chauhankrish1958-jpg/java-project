package com.protrack.util;

import com.protrack.model.*;
import com.protrack.service.ProjectService;
import com.protrack.service.TaskService;
import com.protrack.service.UserService;

/**
 * Utility initializing baseline enterprise dataset matching the frontend models.
 */
public class DataInitializer {

    public static void initializeData(UserService userService, ProjectService projectService, TaskService taskService) {
        // 1. Users
        User admin = userService.createUser("Sarah Jenkins", "admin@protrack.io", Role.ADMIN, "IT Administration", "Principal System Administrator");
        admin.setPhone("+1 (555) 019-2834");
        admin.setBio("Overseeing enterprise system architecture, access security, and IT governance.");

        User pm1 = userService.createUser("Alex Rivera", "alex.rivera@protrack.io", Role.PROJECT_MANAGER, "Engineering Delivery", "Senior Technical Project Manager");
        pm1.setPhone("+1 (555) 018-9921");
        pm1.setBio("10+ years delivering enterprise software projects with Agile & Scrum methodologies.");

        User pm2 = userService.createUser("Elena Rostova", "elena.r@protrack.io", Role.PROJECT_MANAGER, "Product Innovation", "Product & Project Lead");
        pm2.setPhone("+1 (555) 014-4412");
        pm2.setBio("Specializing in health-tech and user-centric multi-platform solutions.");

        User dev1 = userService.createUser("David Kim", "david.kim@protrack.io", Role.TEAM_MEMBER, "Backend Engineering", "Lead Java Software Engineer");
        dev1.setPhone("+1 (555) 012-7788");
        dev1.setBio("Passionate about Spring Boot, microservices architecture, and high-performance databases.");

        User dev2 = userService.createUser("Maya Patel", "maya.patel@protrack.io", Role.TEAM_MEMBER, "Frontend Engineering", "UI/UX & Web Developer");
        dev2.setPhone("+1 (555) 017-3341");
        dev2.setBio("Crafting intuitive user interfaces, accessible web components, and smooth animations.");

        User dev3 = userService.createUser("Liam O'Connor", "liam.oc@protrack.io", Role.TEAM_MEMBER, "Quality Assurance", "QA Automation Specialist");
        dev3.setPhone("+1 (555) 016-5520");
        dev3.setBio("Focused on end-to-end test automation, API reliability, and security compliance.");

        // 2. Projects
        Project p1 = projectService.createProject(
                "Enterprise Banking Portal",
                "Modernize legacy core banking system with cloud-native microservices architecture, Spring Boot, and PCI-DSS compliance.",
                "2026-08-01", "2026-12-15", pm1.getId(), pm1.getName(), "$180,000", Priority.HIGH
        );
        p1.setStatus("In Progress");

        Project p2 = projectService.createProject(
                "Healthcare Telemed App",
                "Cross-platform mobile and web consultation system with HIPAA-compliant encrypted video streaming and electronic medical records.",
                "2026-09-10", "2027-02-28", pm2.getId(), pm2.getName(), "$240,000", Priority.URGENT
        );
        p2.setStatus("In Progress");

        Project p3 = projectService.createProject(
                "AI Analytics & Anomaly Engine",
                "Real-time predictive telemetry engine utilizing deep learning models for cloud cluster health anomaly detection.",
                "2026-07-01", "2026-11-30", pm1.getId(), pm1.getName(), "$120,000", Priority.MEDIUM
        );
        p3.setStatus("Planning");

        Project p4 = projectService.createProject(
                "Supply Chain IoT Gateway",
                "High-throughput MQTT broker integration for cold-chain temperature sensors and automated smart contract verification.",
                "2026-05-15", "2026-10-10", pm2.getId(), pm2.getName(), "$95,000", Priority.LOW
        );
        p4.setStatus("Completed");

        // 3. Tasks
        Task t1 = taskService.assignTask(p1.getId(), p1.getTitle(), "Design Spring Boot Auth Service (JWT + OAuth2)",
                "Implement OAuth2 resource server with JWT validation, refresh tokens, and role-based access control.",
                dev1.getId(), dev1.getName(), "2026-10-25", Priority.HIGH, 32.0);
        t1.updateStatus(TaskStatus.IN_PROGRESS, 75);
        t1.setLoggedHours(24.0);

        Task t2 = taskService.assignTask(p1.getId(), p1.getTitle(), "Build Responsive Transaction History UI",
                "Create accessible HTML5/CSS3 data table with filtering, search, and pagination for ledger entries.",
                dev2.getId(), dev2.getName(), "2026-10-22", Priority.MEDIUM, 25.0);
        t2.updateStatus(TaskStatus.IN_PROGRESS, 60);
        t2.setLoggedHours(18.0);

        Task t3 = taskService.assignTask(p1.getId(), p1.getTitle(), "Penetration Testing & Security Audit",
                "Execute automated OWASP ZAP scans, audit API endpoints for SQLi/XSS, and generate compliance report.",
                dev3.getId(), dev3.getName(), "2026-11-05", Priority.HIGH, 20.0);
        t3.updateStatus(TaskStatus.PENDING, 10);
        t3.setLoggedHours(4.0);

        Task t4 = taskService.assignTask(p2.getId(), p2.getTitle(), "WebRTC Video Consultation Pipeline",
                "Configure STUN/TURN servers and setup end-to-end encrypted WebRTC audio-video streaming pipeline.",
                dev1.getId(), dev1.getName(), "2026-11-12", Priority.URGENT, 40.0);
        t4.updateStatus(TaskStatus.PENDING, 0);

        Task t5 = taskService.assignTask(p2.getId(), p2.getTitle(), "Patient Vital Signs Telemetry Dashboard",
                "Create real-time canvas charting for heart rate, blood pressure, and oxygen saturation.",
                dev2.getId(), dev2.getName(), "2026-10-29", Priority.HIGH, 30.0);
        t5.updateStatus(TaskStatus.IN_PROGRESS, 40);
        t5.setLoggedHours(14.0);

        Task t6 = taskService.assignTask(p4.getId(), p4.getTitle(), "Gateway Load & Stress Benchmarking",
                "Simulate 50,000 concurrent IoT devices using JMeter and analyze socket connection memory footprint.",
                dev3.getId(), dev3.getName(), "2026-10-05", Priority.MEDIUM, 30.0);
        t6.updateStatus(TaskStatus.COMPLETED, 100);
        t6.setLoggedHours(32.0);

        // Recalculate project progresses
        projectService.updateProgressFromTasks(p1.getId(), taskService.getAllTasks());
        projectService.updateProgressFromTasks(p2.getId(), taskService.getAllTasks());
        projectService.updateProgressFromTasks(p3.getId(), taskService.getAllTasks());
        projectService.updateProgressFromTasks(p4.getId(), taskService.getAllTasks());
    }
}
