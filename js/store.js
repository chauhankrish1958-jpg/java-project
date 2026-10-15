/**
 * Data Store for Online Project Management Tool
 * Handles State Management, Persistence (localStorage), and Mock OOP Data
 */

const STORAGE_KEYS = {
    CURRENT_USER: 'protrack_current_user',
    USERS: 'protrack_users',
    PROJECTS: 'protrack_projects',
    TASKS: 'protrack_tasks',
    SETTINGS: 'protrack_settings',
    ACTIVITIES: 'protrack_activities'
};

const DEFAULT_USERS = [
    {
        id: 1,
        name: "Sarah Jenkins",
        email: "admin@protrack.io",
        role: "ADMIN",
        department: "IT Administration",
        title: "Principal System Administrator",
        avatar: "SJ",
        phone: "+1 (555) 019-2834",
        bio: "Overseeing enterprise system architecture, access security, and IT governance."
    },
    {
        id: 2,
        name: "Alex Rivera",
        email: "alex.rivera@protrack.io",
        role: "PROJECT_MANAGER",
        department: "Engineering Delivery",
        title: "Senior Technical Project Manager",
        avatar: "AR",
        phone: "+1 (555) 018-9921",
        bio: "10+ years delivering enterprise software projects with Agile & Scrum methodologies."
    },
    {
        id: 3,
        name: "Elena Rostova",
        email: "elena.r@protrack.io",
        role: "PROJECT_MANAGER",
        department: "Product Innovation",
        title: "Product & Project Lead",
        avatar: "ER",
        phone: "+1 (555) 014-4412",
        bio: "Specializing in health-tech and user-centric multi-platform solutions."
    },
    {
        id: 4,
        name: "David Kim",
        email: "david.kim@protrack.io",
        role: "TEAM_MEMBER",
        department: "Backend Engineering",
        title: "Lead Java Software Engineer",
        avatar: "DK",
        phone: "+1 (555) 012-7788",
        bio: "Passionate about Spring Boot, microservices architecture, and high-performance databases."
    },
    {
        id: 5,
        name: "Maya Patel",
        email: "maya.patel@protrack.io",
        role: "TEAM_MEMBER",
        department: "Frontend Engineering",
        title: "UI/UX & Web Developer",
        avatar: "MP",
        phone: "+1 (555) 017-3341",
        bio: "Crafting intuitive user interfaces, accessible web components, and smooth animations."
    },
    {
        id: 6,
        name: "Liam O'Connor",
        email: "liam.oc@protrack.io",
        role: "TEAM_MEMBER",
        department: "Quality Assurance",
        title: "QA Automation Specialist",
        avatar: "LO",
        phone: "+1 (555) 016-5520",
        bio: "Focused on end-to-end test automation, API reliability, and security compliance."
    }
];

const DEFAULT_PROJECTS = [
    {
        id: 1,
        title: "Enterprise Banking Portal",
        description: "Modernize legacy core banking system with cloud-native microservices architecture, Spring Boot, and PCI-DSS compliance.",
        startDate: "2026-08-01",
        endDate: "2026-12-15",
        status: "In Progress",
        priority: "High",
        managerId: 2,
        managerName: "Alex Rivera",
        budget: "$180,000",
        progress: 68
    },
    {
        id: 2,
        title: "Healthcare Telemed App",
        description: "Cross-platform mobile and web consultation system with HIPAA-compliant encrypted video streaming and electronic medical records.",
        startDate: "2026-09-10",
        endDate: "2027-02-28",
        status: "In Progress",
        priority: "Urgent",
        managerId: 3,
        managerName: "Elena Rostova",
        budget: "$240,000",
        progress: 45
    },
    {
        id: 3,
        title: "AI Analytics & Anomaly Engine",
        description: "Real-time predictive telemetry engine utilizing deep learning models for cloud cluster health anomaly detection.",
        startDate: "2026-07-01",
        endDate: "2026-11-30",
        status: "Planning",
        priority: "Medium",
        managerId: 2,
        managerName: "Alex Rivera",
        budget: "$120,000",
        progress: 25
    },
    {
        id: 4,
        title: "Supply Chain IoT Gateway",
        description: "High-throughput MQTT broker integration for cold-chain temperature sensors and automated smart contract verification.",
        startDate: "2026-05-15",
        endDate: "2026-10-10",
        status: "Completed",
        priority: "Low",
        managerId: 3,
        managerName: "Elena Rostova",
        budget: "$95,000",
        progress: 100
    }
];

const DEFAULT_TASKS = [
    {
        id: 101,
        projectId: 1,
        projectTitle: "Enterprise Banking Portal",
        title: "Design Spring Boot Auth Service (JWT + OAuth2)",
        description: "Implement OAuth2 resource server with JWT validation, refresh tokens, and role-based access control.",
        assigneeId: 4,
        assigneeName: "David Kim",
        status: "In Progress",
        priority: "High",
        dueDate: "2026-10-25",
        progress: 75,
        loggedHours: 24,
        estimatedHours: 32
    },
    {
        id: 102,
        projectId: 1,
        projectTitle: "Enterprise Banking Portal",
        title: "Build Responsive Transaction History UI",
        description: "Create accessible HTML5/CSS3 data table with filtering, search, and pagination for ledger entries.",
        assigneeId: 5,
        assigneeName: "Maya Patel",
        status: "In Progress",
        priority: "Medium",
        dueDate: "2026-10-22",
        progress: 60,
        loggedHours: 18,
        estimatedHours: 25
    },
    {
        id: 103,
        projectId: 1,
        projectTitle: "Enterprise Banking Portal",
        title: "Penetration Testing & Security Audit",
        description: "Execute automated OWASP ZAP scans, audit API endpoints for SQLi/XSS, and generate compliance report.",
        assigneeId: 6,
        assigneeName: "Liam O'Connor",
        status: "Pending",
        priority: "High",
        dueDate: "2026-11-05",
        progress: 10,
        loggedHours: 4,
        estimatedHours: 20
    },
    {
        id: 104,
        projectId: 2,
        projectTitle: "Healthcare Telemed App",
        title: "WebRTC Video Consultation Pipeline",
        description: "Configure STUN/TURN servers and setup end-to-end encrypted WebRTC audio-video streaming pipeline.",
        assigneeId: 4,
        assigneeName: "David Kim",
        status: "Pending",
        priority: "Urgent",
        dueDate: "2026-11-12",
        progress: 0,
        loggedHours: 0,
        estimatedHours: 40
    },
    {
        id: 105,
        projectId: 2,
        projectTitle: "Healthcare Telemed App",
        title: "Patient Vital Signs Telemetry Dashboard",
        description: "Create real-time canvas charting for heart rate, blood pressure, and oxygen saturation.",
        assigneeId: 5,
        assigneeName: "Maya Patel",
        status: "In Progress",
        priority: "High",
        dueDate: "2026-10-29",
        progress: 40,
        loggedHours: 14,
        estimatedHours: 30
    },
    {
        id: 106,
        projectId: 4,
        projectTitle: "Supply Chain IoT Gateway",
        title: "Gateway Load & Stress Benchmarking",
        description: "Simulate 50,000 concurrent IoT devices using JMeter and analyze socket connection memory footprint.",
        assigneeId: 6,
        assigneeName: "Liam O'Connor",
        status: "Completed",
        priority: "Medium",
        dueDate: "2026-10-05",
        progress: 100,
        loggedHours: 32,
        estimatedHours: 30
    }
];

const DEFAULT_SETTINGS = {
    systemName: "ProTrack Enterprise Suite",
    organizationName: "Apex Global Technology Solutions",
    maintenanceMode: false,
    defaultTaskPriority: "Medium",
    emailNotifications: true,
    sessionTimeout: 60,
    maxProjectsPerManager: 10,
    securityLevel: "High (MFA Enabled)",
    systemLogLevel: "INFO",
    theme: "light",
    allowRegistration: false
};

const DEFAULT_ACTIVITIES = [
    {
        id: 1,
        timestamp: "Just now",
        user: "David Kim",
        role: "TEAM_MEMBER",
        action: "Updated status of 'Design Spring Boot Auth Service' to 75% In Progress",
        type: "task"
    },
    {
        id: 2,
        timestamp: "1 hour ago",
        user: "Alex Rivera",
        role: "PROJECT_MANAGER",
        action: "Assigned new task 'WebRTC Video Consultation Pipeline' to David Kim",
        type: "project"
    },
    {
        id: 3,
        timestamp: "3 hours ago",
        user: "Sarah Jenkins",
        role: "ADMIN",
        action: "Updated system security policies and session timeout to 60 minutes",
        type: "system"
    },
    {
        id: 4,
        timestamp: "5 hours ago",
        user: "Maya Patel",
        role: "TEAM_MEMBER",
        action: "Completed component styling for Patient Vital Signs Telemetry",
        type: "task"
    },
    {
        id: 5,
        timestamp: "1 day ago",
        user: "Elena Rostova",
        role: "PROJECT_MANAGER",
        action: "Marked project 'Supply Chain IoT Gateway' as Completed (100%)",
        type: "project"
    }
];

class DataStore {
    constructor() {
        this.init();
    }

    init() {
        if (!localStorage.getItem(STORAGE_KEYS.USERS)) {
            this.setUsers(DEFAULT_USERS);
        }
        if (!localStorage.getItem(STORAGE_KEYS.PROJECTS)) {
            this.setProjects(DEFAULT_PROJECTS);
        }
        if (!localStorage.getItem(STORAGE_KEYS.TASKS)) {
            this.setTasks(DEFAULT_TASKS);
        }
        if (!localStorage.getItem(STORAGE_KEYS.SETTINGS)) {
            this.setSettings(DEFAULT_SETTINGS);
        }
        if (!localStorage.getItem(STORAGE_KEYS.ACTIVITIES)) {
            this.setActivities(DEFAULT_ACTIVITIES);
        }
        if (!localStorage.getItem(STORAGE_KEYS.CURRENT_USER)) {
            // Default current user is Admin (id: 1)
            this.setCurrentUser(DEFAULT_USERS[0]);
        }
    }

    resetToDefault() {
        localStorage.setItem(STORAGE_KEYS.USERS, JSON.stringify(DEFAULT_USERS));
        localStorage.setItem(STORAGE_KEYS.PROJECTS, JSON.stringify(DEFAULT_PROJECTS));
        localStorage.setItem(STORAGE_KEYS.TASKS, JSON.stringify(DEFAULT_TASKS));
        localStorage.setItem(STORAGE_KEYS.SETTINGS, JSON.stringify(DEFAULT_SETTINGS));
        localStorage.setItem(STORAGE_KEYS.ACTIVITIES, JSON.stringify(DEFAULT_ACTIVITIES));
        localStorage.setItem(STORAGE_KEYS.CURRENT_USER, JSON.stringify(DEFAULT_USERS[0]));
    }

    // --- Current User & Session ---
    getCurrentUser() {
        try {
            return JSON.parse(localStorage.getItem(STORAGE_KEYS.CURRENT_USER)) || DEFAULT_USERS[0];
        } catch (e) {
            return DEFAULT_USERS[0];
        }
    }

    setCurrentUser(user) {
        localStorage.setItem(STORAGE_KEYS.CURRENT_USER, JSON.stringify(user));
        window.dispatchEvent(new CustomEvent('protrack:user-changed', { detail: user }));
    }

    switchUserById(userId) {
        const users = this.getUsers();
        const found = users.find(u => u.id === Number(userId));
        if (found) {
            this.setCurrentUser(found);
            return found;
        }
        return null;
    }

    // --- Users CRUD ---
    getUsers() {
        try {
            return JSON.parse(localStorage.getItem(STORAGE_KEYS.USERS)) || DEFAULT_USERS;
        } catch (e) {
            return DEFAULT_USERS;
        }
    }

    setUsers(users) {
        localStorage.setItem(STORAGE_KEYS.USERS, JSON.stringify(users));
    }

    getUserById(id) {
        return this.getUsers().find(u => u.id === Number(id));
    }

    addUser(userData) {
        const users = this.getUsers();
        const newId = users.length > 0 ? Math.max(...users.map(u => u.id)) + 1 : 1;
        
        // Generate initials for avatar
        const initials = (userData.name || "U")
            .split(" ")
            .map(n => n[0])
            .join("")
            .substring(0, 2)
            .toUpperCase();

        const newUser = {
            id: newId,
            name: userData.name,
            email: userData.email,
            role: userData.role,
            department: userData.department || "General",
            title: userData.title || (userData.role === "ADMIN" ? "System Administrator" : userData.role === "PROJECT_MANAGER" ? "Project Manager" : "Software Engineer"),
            avatar: initials,
            phone: userData.phone || "+1 (555) 000-0000",
            bio: userData.bio || "Team member at ProTrack."
        };

        users.push(newUser);
        this.setUsers(users);
        this.logActivity(`Admin added new user '${newUser.name}' with role ${newUser.role}`, 'system');
        return newUser;
    }

    updateUser(id, updatedData) {
        let users = this.getUsers();
        const index = users.findIndex(u => u.id === Number(id));
        if (index !== -1) {
            users[index] = { ...users[index], ...updatedData };
            this.setUsers(users);

            // If current user is updated, refresh session
            const current = this.getCurrentUser();
            if (current.id === Number(id)) {
                this.setCurrentUser(users[index]);
            }

            this.logActivity(`User '${users[index].name}' details updated`, 'system');
            return users[index];
        }
        return null;
    }

    deleteUser(id) {
        let users = this.getUsers();
        const userToDelete = users.find(u => u.id === Number(id));
        if (!userToDelete) return false;

        // Prevent deleting current user
        const current = this.getCurrentUser();
        if (current.id === Number(id)) {
            throw new Error("Cannot delete currently logged-in user account.");
        }

        users = users.filter(u => u.id !== Number(id));
        this.setUsers(users);
        this.logActivity(`Admin removed user account '${userToDelete.name}'`, 'system');
        return true;
    }

    // --- Projects CRUD ---
    getProjects() {
        try {
            return JSON.parse(localStorage.getItem(STORAGE_KEYS.PROJECTS)) || DEFAULT_PROJECTS;
        } catch (e) {
            return DEFAULT_PROJECTS;
        }
    }

    setProjects(projects) {
        localStorage.setItem(STORAGE_KEYS.PROJECTS, JSON.stringify(projects));
    }

    getProjectById(id) {
        return this.getProjects().find(p => p.id === Number(id));
    }

    addProject(projectData) {
        const projects = this.getProjects();
        const newId = projects.length > 0 ? Math.max(...projects.map(p => p.id)) + 1 : 1;

        const manager = this.getUserById(projectData.managerId);

        const newProject = {
            id: newId,
            title: projectData.title,
            description: projectData.description,
            startDate: projectData.startDate,
            endDate: projectData.endDate,
            status: projectData.status || "Planning",
            priority: projectData.priority || "Medium",
            managerId: Number(projectData.managerId) || (manager ? manager.id : 2),
            managerName: manager ? manager.name : "Alex Rivera",
            budget: projectData.budget || "$50,000",
            progress: Number(projectData.progress) || 0
        };

        projects.push(newProject);
        this.setProjects(projects);
        this.logActivity(`Project '${newProject.title}' created and assigned to ${newProject.managerName}`, 'project');
        return newProject;
    }

    updateProject(id, updatedData) {
        let projects = this.getProjects();
        const index = projects.findIndex(p => p.id === Number(id));
        if (index !== -1) {
            if (updatedData.managerId) {
                const manager = this.getUserById(updatedData.managerId);
                if (manager) {
                    updatedData.managerName = manager.name;
                }
            }
            projects[index] = { ...projects[index], ...updatedData };
            this.setProjects(projects);
            this.logActivity(`Project '${projects[index].title}' details updated`, 'project');
            return projects[index];
        }
        return null;
    }

    deleteProject(id) {
        let projects = this.getProjects();
        const projectToDelete = projects.find(p => p.id === Number(id));
        if (!projectToDelete) return false;

        projects = projects.filter(p => p.id !== Number(id));
        this.setProjects(projects);

        // Also clean up tasks related to this project
        let tasks = this.getTasks();
        tasks = tasks.filter(t => t.projectId !== Number(id));
        this.setTasks(tasks);

        this.logActivity(`Project '${projectToDelete.title}' and associated tasks deleted`, 'project');
        return true;
    }

    // --- Tasks CRUD ---
    getTasks() {
        try {
            return JSON.parse(localStorage.getItem(STORAGE_KEYS.TASKS)) || DEFAULT_TASKS;
        } catch (e) {
            return DEFAULT_TASKS;
        }
    }

    setTasks(tasks) {
        localStorage.setItem(STORAGE_KEYS.TASKS, JSON.stringify(tasks));
    }

    getTaskById(id) {
        return this.getTasks().find(t => t.id === Number(id));
    }

    addTask(taskData) {
        const tasks = this.getTasks();
        const newId = tasks.length > 0 ? Math.max(...tasks.map(t => t.id)) + 1 : 101;

        const project = this.getProjectById(taskData.projectId);
        const assignee = this.getUserById(taskData.assigneeId);

        const newTask = {
            id: newId,
            projectId: Number(taskData.projectId),
            projectTitle: project ? project.title : "Unassigned Project",
            title: taskData.title,
            description: taskData.description,
            assigneeId: Number(taskData.assigneeId),
            assigneeName: assignee ? assignee.name : "Unassigned",
            status: taskData.status || "Pending",
            priority: taskData.priority || "Medium",
            dueDate: taskData.dueDate,
            progress: Number(taskData.progress) || 0,
            loggedHours: Number(taskData.loggedHours) || 0,
            estimatedHours: Number(taskData.estimatedHours) || 20
        };

        tasks.push(newTask);
        this.setTasks(tasks);
        this.recalculateProjectProgress(newTask.projectId);
        this.logActivity(`Task '${newTask.title}' assigned to ${newTask.assigneeName}`, 'task');
        return newTask;
    }

    updateTask(id, updatedData) {
        let tasks = this.getTasks();
        const index = tasks.findIndex(t => t.id === Number(id));
        if (index !== -1) {
            if (updatedData.assigneeId) {
                const assignee = this.getUserById(updatedData.assigneeId);
                if (assignee) {
                    updatedData.assigneeName = assignee.name;
                }
            }
            if (updatedData.projectId) {
                const project = this.getProjectById(updatedData.projectId);
                if (project) {
                    updatedData.projectTitle = project.title;
                }
            }
            tasks[index] = { ...tasks[index], ...updatedData };
            this.setTasks(tasks);
            this.recalculateProjectProgress(tasks[index].projectId);
            this.logActivity(`Task '${tasks[index].title}' updated (Status: ${tasks[index].status}, Progress: ${tasks[index].progress}%)`, 'task');
            return tasks[index];
        }
        return null;
    }

    deleteTask(id) {
        let tasks = this.getTasks();
        const taskToDelete = tasks.find(t => t.id === Number(id));
        if (!taskToDelete) return false;

        const projectId = taskToDelete.projectId;
        tasks = tasks.filter(t => t.id !== Number(id));
        this.setTasks(tasks);
        this.recalculateProjectProgress(projectId);
        this.logActivity(`Task '${taskToDelete.title}' was deleted`, 'task');
        return true;
    }

    recalculateProjectProgress(projectId) {
        const tasks = this.getTasks().filter(t => t.projectId === Number(projectId));
        if (tasks.length === 0) return;

        const totalProgress = tasks.reduce((sum, t) => sum + (Number(t.progress) || 0), 0);
        const avgProgress = Math.round(totalProgress / tasks.length);

        let projects = this.getProjects();
        const pIndex = projects.findIndex(p => p.id === Number(projectId));
        if (pIndex !== -1) {
            projects[pIndex].progress = avgProgress;
            if (avgProgress === 100) {
                projects[pIndex].status = "Completed";
            } else if (avgProgress > 0 && projects[pIndex].status === "Planning") {
                projects[pIndex].status = "In Progress";
            }
            this.setProjects(projects);
        }
    }

    // --- System Settings ---
    getSettings() {
        try {
            return JSON.parse(localStorage.getItem(STORAGE_KEYS.SETTINGS)) || DEFAULT_SETTINGS;
        } catch (e) {
            return DEFAULT_SETTINGS;
        }
    }

    updateSettings(newSettings) {
        const current = this.getSettings();
        const updated = { ...current, ...newSettings };
        localStorage.setItem(STORAGE_KEYS.SETTINGS, JSON.stringify(updated));
        this.logActivity("System configurations and security parameters updated by Admin", 'system');
        return updated;
    }

    // --- Activities & Logs ---
    getActivities() {
        try {
            return JSON.parse(localStorage.getItem(STORAGE_KEYS.ACTIVITIES)) || DEFAULT_ACTIVITIES;
        } catch (e) {
            return DEFAULT_ACTIVITIES;
        }
    }

    setActivities(acts) {
        localStorage.setItem(STORAGE_KEYS.ACTIVITIES, JSON.stringify(acts));
    }

    logActivity(actionText, type = 'general') {
        const user = this.getCurrentUser();
        const activities = this.getActivities();
        const newAct = {
            id: Date.now(),
            timestamp: "Just now",
            user: user ? user.name : "System",
            role: user ? user.role : "SYSTEM",
            action: actionText,
            type: type
        };
        activities.unshift(newAct);
        // keep recent 25
        if (activities.length > 25) activities.pop();
        this.setActivities(activities);
        window.dispatchEvent(new CustomEvent('protrack:activity-logged', { detail: newAct }));
    }
}

// Global Store Instance
window.store = new DataStore();
