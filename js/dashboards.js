/**
 * ProTrack Dashboard Controller
 * Powers Admin, Project Manager, and Team Member Dashboards
 */

class DashboardController {
    constructor() {
        this.currentTab = 'overview';
        this.initEventListeners();
    }

    initEventListeners() {
        // Listen to store user changes
        window.addEventListener('protrack:user-changed', (e) => {
            this.handleUserChange(e.detail);
        });

        // Listen to activity logs
        window.addEventListener('protrack:activity-logged', () => {
            this.renderActivityFeeds();
        });
    }

    // Switch between user roles and active dashboard
    switchRole(role) {
        const users = window.store.getUsers();
        let targetUser = users.find(u => u.role === role);
        if (!targetUser) {
            targetUser = users[0];
        }
        window.store.setCurrentUser(targetUser);
    }

    handleUserChange(user) {
        // Update user dropdown selector in header
        const select = document.getElementById('userQuickSelect');
        if (select) {
            select.value = user.id;
        }

        // Update header & sidebar avatar and name
        const userNames = document.querySelectorAll('.currentUser-name');
        userNames.forEach(el => el.textContent = user.name);

        const userRoles = document.querySelectorAll('.currentUser-role');
        userRoles.forEach(el => {
            el.textContent = user.role === 'ADMIN' ? 'Administrator' : 
                             user.role === 'PROJECT_MANAGER' ? 'Project Manager' : 'Team Member';
        });

        const userAvatars = document.querySelectorAll('.currentUser-avatar');
        userAvatars.forEach(el => el.textContent = user.avatar);

        // Update active role toggle buttons
        document.querySelectorAll('.role-switch-btn').forEach(btn => {
            const btnRole = btn.getAttribute('data-role');
            if (btnRole === user.role) {
                btn.classList.add('active');
            } else {
                btn.classList.remove('active');
            }
        });

        // Update role badge in sidebar
        const roleBadge = document.getElementById('sidebarRoleBadge');
        if (roleBadge) {
            if (user.role === 'ADMIN') {
                roleBadge.className = 'active-role-tag role-admin';
                roleBadge.innerHTML = '● Administrator';
            } else if (user.role === 'PROJECT_MANAGER') {
                roleBadge.className = 'active-role-tag role-pm';
                roleBadge.innerHTML = '● Project Manager';
            } else {
                roleBadge.className = 'active-role-tag role-team';
                roleBadge.innerHTML = '● Team Member';
            }
        }

        // Switch to the appropriate dashboard view
        this.activateDashboardForRole(user.role);
    }

    activateDashboardForRole(role) {
        // Hide all views
        document.querySelectorAll('.dashboard-view').forEach(view => {
            view.classList.remove('active');
        });

        // Show sidebar nav groups according to role
        const adminNav = document.getElementById('navAdminSection');
        const pmNav = document.getElementById('navPmSection');
        const teamNav = document.getElementById('navTeamSection');

        if (adminNav) adminNav.style.display = role === 'ADMIN' ? 'block' : 'none';
        if (pmNav) pmNav.style.display = role === 'PROJECT_MANAGER' ? 'block' : 'none';
        if (teamNav) teamNav.style.display = role === 'TEAM_MEMBER' ? 'block' : 'none';

        if (role === 'ADMIN') {
            const adminView = document.getElementById('view-admin');
            if (adminView) adminView.classList.add('active');
            this.renderAdminDashboard();
        } else if (role === 'PROJECT_MANAGER') {
            const pmView = document.getElementById('view-pm');
            if (pmView) pmView.classList.add('active');
            this.renderPmDashboard();
        } else {
            const teamView = document.getElementById('view-team');
            if (teamView) teamView.classList.add('active');
            this.renderTeamDashboard();
        }
    }

    // =========================================================================
    // ADMIN DASHBOARD
    // =========================================================================
    renderAdminDashboard() {
        this.renderAdminMetrics();
        this.renderAdminUsersTable();
        this.renderAdminProjectsTable();
        this.renderAdminSettings();
        this.renderAdminCharts();
    }

    renderAdminMetrics() {
        const users = window.store.getUsers();
        const projects = window.store.getProjects();
        const tasks = window.store.getTasks();

        const totalUsersEl = document.getElementById('adminStatUsers');
        if (totalUsersEl) totalUsersEl.textContent = users.length;

        const totalProjectsEl = document.getElementById('adminStatProjects');
        if (totalProjectsEl) totalProjectsEl.textContent = projects.length;

        const totalTasksEl = document.getElementById('adminStatTasks');
        if (totalTasksEl) totalTasksEl.textContent = tasks.length;

        const avgProgress = projects.length > 0 ? 
            Math.round(projects.reduce((sum, p) => sum + p.progress, 0) / projects.length) : 0;
        const avgProgressEl = document.getElementById('adminStatProgress');
        if (avgProgressEl) avgProgressEl.textContent = `${avgProgress}%`;
    }

    renderAdminUsersTable(searchTerm = '') {
        const tbody = document.getElementById('adminUsersTableBody');
        if (!tbody) return;

        let users = window.store.getUsers();
        if (searchTerm) {
            const term = searchTerm.toLowerCase();
            users = users.filter(u => 
                u.name.toLowerCase().includes(term) || 
                u.email.toLowerCase().includes(term) || 
                u.role.toLowerCase().includes(term) ||
                u.department.toLowerCase().includes(term)
            );
        }

        if (users.length === 0) {
            tbody.innerHTML = `<tr><td colspan="6" style="text-align:center; padding:30px; color:var(--text-muted);">No users found matching search criteria.</td></tr>`;
            return;
        }

        tbody.innerHTML = users.map(user => `
            <tr>
                <td>
                    <div class="table-user-cell">
                        <div class="avatar-cell">${user.avatar}</div>
                        <div class="user-cell-info">
                            <strong>${user.name}</strong>
                            <span>${user.title}</span>
                        </div>
                    </div>
                </td>
                <td>${user.email}</td>
                <td>${window.ui.getRoleBadge(user.role)}</td>
                <td>${user.department}</td>
                <td>${user.phone || 'N/A'}</td>
                <td>
                    <div class="table-actions">
                        <button class="btn btn-secondary btn-sm" onclick="window.dashboard.openEditUserModal(${user.id})" title="Edit User">
                            ✏️ Edit
                        </button>
                        <button class="btn btn-danger btn-sm" onclick="window.dashboard.deleteUserAction(${user.id})" title="Delete User">
                            🗑️ Delete
                        </button>
                    </div>
                </td>
            </tr>
        `).join('');
    }

    renderAdminProjectsTable(searchTerm = '') {
        const tbody = document.getElementById('adminProjectsTableBody');
        if (!tbody) return;

        let projects = window.store.getProjects();
        if (searchTerm) {
            const term = searchTerm.toLowerCase();
            projects = projects.filter(p => 
                p.title.toLowerCase().includes(term) ||
                p.managerName.toLowerCase().includes(term) ||
                p.status.toLowerCase().includes(term)
            );
        }

        if (projects.length === 0) {
            tbody.innerHTML = `<tr><td colspan="7" style="text-align:center; padding:30px; color:var(--text-muted);">No projects found.</td></tr>`;
            return;
        }

        tbody.innerHTML = projects.map(project => `
            <tr>
                <td>
                    <strong style="color:var(--text-primary);">${project.title}</strong>
                    <div style="font-size:12px; color:var(--text-muted); max-width:260px; white-space:nowrap; overflow:hidden; text-overflow:ellipsis;">${project.description}</div>
                </td>
                <td>${project.managerName}</td>
                <td>
                    <div style="font-size:12px;">${window.ui.formatDate(project.startDate)} -</div>
                    <div style="font-size:12px; font-weight:600;">${window.ui.formatDate(project.endDate)}</div>
                </td>
                <td>${window.ui.getStatusBadge(project.status)}</td>
                <td>
                    <div class="progress-bar-container">
                        <div class="progress-track">
                            <div class="progress-fill ${project.progress === 100 ? 'success' : ''}" style="width: ${project.progress}%"></div>
                        </div>
                        <span class="progress-percent">${project.progress}%</span>
                    </div>
                </td>
                <td><strong style="color:var(--text-primary);">${project.budget}</strong></td>
                <td>
                    <div class="table-actions">
                        <button class="btn btn-secondary btn-sm" onclick="window.dashboard.openEditProjectModal(${project.id})" title="Edit Project">
                            ✏️ Edit
                        </button>
                        <button class="btn btn-danger btn-sm" onclick="window.dashboard.deleteProjectAction(${project.id})" title="Delete Project">
                            🗑️
                        </button>
                    </div>
                </td>
            </tr>
        `).join('');
    }

    renderAdminSettings() {
        const settings = window.store.getSettings();
        const form = document.getElementById('adminSettingsForm');
        if (!form) return;

        if (form.systemName) form.systemName.value = settings.systemName;
        if (form.organizationName) form.organizationName.value = settings.organizationName;
        if (form.maintenanceMode) form.maintenanceMode.checked = settings.maintenanceMode;
        if (form.emailNotifications) form.emailNotifications.checked = settings.emailNotifications;
        if (form.defaultTaskPriority) form.defaultTaskPriority.value = settings.defaultTaskPriority;
        if (form.sessionTimeout) form.sessionTimeout.value = settings.sessionTimeout;
        if (form.maxProjectsPerManager) form.maxProjectsPerManager.value = settings.maxProjectsPerManager;
        if (form.securityLevel) form.securityLevel.value = settings.securityLevel;
    }

    renderAdminCharts() {
        // User Distribution by Role
        const users = window.store.getUsers();
        const adminCount = users.filter(u => u.role === 'ADMIN').length;
        const pmCount = users.filter(u => u.role === 'PROJECT_MANAGER').length;
        const teamCount = users.filter(u => u.role === 'TEAM_MEMBER').length;

        window.ui.renderDonutChart('adminUserRoleChart', [
            { label: 'Admins', value: adminCount, color: '#ef4444' },
            { label: 'Project Managers', value: pmCount, color: '#0ea5e9' },
            { label: 'Team Members', value: teamCount, color: '#10b981' }
        ]);

        // Projects Health / Status
        const projects = window.store.getProjects();
        const completedProjects = projects.filter(p => p.status === 'Completed').length;
        const inProgressProjects = projects.filter(p => p.status === 'In Progress').length;
        const planningProjects = projects.filter(p => p.status === 'Planning').length;

        window.ui.renderDonutChart('adminProjectStatusChart', [
            { label: 'Completed', value: completedProjects, color: '#10b981' },
            { label: 'In Progress', value: inProgressProjects, color: '#4f46e5' },
            { label: 'Planning', value: planningProjects, color: '#f59e0b' }
        ]);

        // System Performance Bars
        window.ui.renderBarChart('adminMetricsBarChart', [
            { label: 'System Server Capacity', value: 42, display: '42% Utilized', color: '#10b981' },
            { label: 'Database I/O Throughput', value: 68, display: '1.2k ops/sec', color: '#3b82f6' },
            { label: 'Project Milestone Success Rate', value: 89, display: '89% on track', color: '#4f46e5' },
            { label: 'Active User Concurrent Sessions', value: 75, display: '75 Active Users', color: '#8b5cf6' }
        ]);
    }

    // --- Admin User Actions ---
    openAddUserModal() {
        const form = document.getElementById('userForm');
        if (form) {
            form.reset();
            form.userId.value = '';
            document.getElementById('userModalTitle').textContent = 'Add New User Account';
        }
        window.ui.openModal('userModal');
    }

    openEditUserModal(userId) {
        const user = window.store.getUserById(userId);
        if (!user) return;

        const form = document.getElementById('userForm');
        if (form) {
            form.userId.value = user.id;
            form.name.value = user.name;
            form.email.value = user.email;
            form.role.value = user.role;
            form.department.value = user.department;
            form.title.value = user.title;
            form.phone.value = user.phone;
            document.getElementById('userModalTitle').textContent = 'Edit User Account';
        }
        window.ui.openModal('userModal');
    }

    handleUserFormSubmit(event) {
        event.preventDefault();
        const form = event.target;
        const userId = form.userId.value;

        const userData = {
            name: form.name.value.trim(),
            email: form.email.value.trim(),
            role: form.role.value,
            department: form.department.value.trim(),
            title: form.title.value.trim(),
            phone: form.phone.value.trim()
        };

        if (userId) {
            window.store.updateUser(userId, userData);
            window.ui.showToast(`User account for "${userData.name}" updated successfully.`, 'success', 'User Updated');
        } else {
            window.store.addUser(userData);
            window.ui.showToast(`User account for "${userData.name}" created successfully.`, 'success', 'User Created');
        }

        window.ui.closeModal('userModal');
        this.renderAdminUsersTable();
        this.renderAdminMetrics();
        this.renderAdminCharts();
        this.populateSelectDropdowns();
    }

    deleteUserAction(userId) {
        const user = window.store.getUserById(userId);
        if (!user) return;

        if (confirm(`Are you sure you want to delete user account "${user.name}"? This action cannot be undone.`)) {
            try {
                window.store.deleteUser(userId);
                window.ui.showToast(`User account for "${user.name}" has been deleted.`, 'success', 'User Deleted');
                this.renderAdminUsersTable();
                this.renderAdminMetrics();
                this.renderAdminCharts();
                this.populateSelectDropdowns();
            } catch (err) {
                window.ui.showToast(err.message, 'error', 'Action Restricted');
            }
        }
    }

    // --- Admin & PM Project Actions ---
    openAddProjectModal() {
        const form = document.getElementById('projectForm');
        if (form) {
            form.reset();
            form.projectId.value = '';
            document.getElementById('projectModalTitle').textContent = 'Create New Project';
        }
        window.ui.openModal('projectModal');
    }

    openEditProjectModal(projectId) {
        const project = window.store.getProjectById(projectId);
        if (!project) return;

        const form = document.getElementById('projectForm');
        if (form) {
            form.projectId.value = project.id;
            form.title.value = project.title;
            form.description.value = project.description;
            form.startDate.value = project.startDate;
            form.endDate.value = project.endDate;
            form.status.value = project.status;
            form.priority.value = project.priority;
            form.managerId.value = project.managerId;
            form.budget.value = project.budget;
            document.getElementById('projectModalTitle').textContent = 'Edit Project Details';
        }
        window.ui.openModal('projectModal');
    }

    handleProjectFormSubmit(event) {
        event.preventDefault();
        const form = event.target;
        const projectId = form.projectId.value;

        const projectData = {
            title: form.title.value.trim(),
            description: form.description.value.trim(),
            startDate: form.startDate.value,
            endDate: form.endDate.value,
            status: form.status.value,
            priority: form.priority.value,
            managerId: form.managerId.value,
            budget: form.budget.value.trim()
        };

        if (projectId) {
            window.store.updateProject(projectId, projectData);
            window.ui.showToast(`Project "${projectData.title}" details successfully updated.`, 'success', 'Project Updated');
        } else {
            window.store.addProject(projectData);
            window.ui.showToast(`Project "${projectData.title}" created successfully.`, 'success', 'Project Created');
        }

        window.ui.closeModal('projectModal');
        this.renderAdminProjectsTable();
        this.renderAdminMetrics();
        this.renderAdminCharts();
        this.renderPmDashboard();
        this.populateSelectDropdowns();
    }

    deleteProjectAction(projectId) {
        const project = window.store.getProjectById(projectId);
        if (!project) return;

        if (confirm(`Are you sure you want to delete project "${project.title}" and all related tasks?`)) {
            window.store.deleteProject(projectId);
            window.ui.showToast(`Project "${project.title}" removed successfully.`, 'success', 'Project Removed');
            this.renderAdminProjectsTable();
            this.renderAdminMetrics();
            this.renderAdminCharts();
            this.renderPmDashboard();
            this.populateSelectDropdowns();
        }
    }

    handleSettingsFormSubmit(event) {
        event.preventDefault();
        const form = event.target;

        const newSettings = {
            systemName: form.systemName.value.trim(),
            organizationName: form.organizationName.value.trim(),
            maintenanceMode: form.maintenanceMode.checked,
            emailNotifications: form.emailNotifications.checked,
            defaultTaskPriority: form.defaultTaskPriority.value,
            sessionTimeout: Number(form.sessionTimeout.value),
            maxProjectsPerManager: Number(form.maxProjectsPerManager.value),
            securityLevel: form.securityLevel.value
        };

        window.store.updateSettings(newSettings);
        window.ui.showToast("System-wide configuration settings saved successfully.", "success", "Settings Saved");
    }

    // =========================================================================
    // PROJECT MANAGER DASHBOARD
    // =========================================================================
    renderPmDashboard() {
        this.renderPmMetrics();
        this.renderPmProjectsList();
        this.renderPmTasksTable();
        this.renderPmProgressReports();
        this.renderPmTeamMembers();
    }

    renderPmMetrics() {
        const currentUser = window.store.getCurrentUser();
        const projects = window.store.getProjects();
        const myProjects = projects.filter(p => p.managerId === currentUser.id);
        const tasks = window.store.getTasks();
        const myProjectIds = myProjects.map(p => p.id);
        const myTasks = tasks.filter(t => myProjectIds.includes(t.projectId));

        const pmStatProjects = document.getElementById('pmStatProjects');
        if (pmStatProjects) pmStatProjects.textContent = myProjects.length || projects.length;

        const pmStatTasks = document.getElementById('pmStatTasks');
        if (pmStatTasks) pmStatTasks.textContent = myTasks.length;

        const completedTasks = myTasks.filter(t => t.status === 'Completed').length;
        const rate = myTasks.length > 0 ? Math.round((completedTasks / myTasks.length) * 100) : 0;

        const pmStatRate = document.getElementById('pmStatRate');
        if (pmStatRate) pmStatRate.textContent = `${rate}%`;

        const activeTeam = new Set(myTasks.map(t => t.assigneeId)).size;
        const pmStatTeam = document.getElementById('pmStatTeam');
        if (pmStatTeam) pmStatTeam.textContent = activeTeam || 3;
    }

    renderPmProjectsList() {
        const container = document.getElementById('pmProjectsGrid');
        if (!container) return;

        const projects = window.store.getProjects();

        if (projects.length === 0) {
            container.innerHTML = `<div style="grid-column:1/-1; padding:30px; text-align:center; color:var(--text-muted);">No projects created yet. Click "Create Project" to start one.</div>`;
            return;
        }

        container.innerHTML = projects.map(p => `
            <div class="metric-card" style="margin-bottom:0;">
                <div>
                    <div style="display:flex; justify-content:space-between; align-items:flex-start; margin-bottom:8px;">
                        <h4 style="font-size:16px; font-weight:700; color:var(--text-primary);">${p.title}</h4>
                        ${window.ui.getStatusBadge(p.status)}
                    </div>
                    <p style="font-size:12.5px; color:var(--text-secondary); margin-bottom:14px; min-height:38px;">${p.description}</p>
                    <div style="display:flex; justify-content:space-between; font-size:12px; color:var(--text-muted); margin-bottom:12px;">
                        <span>📅 Due: <strong>${window.ui.formatDate(p.endDate)}</strong></span>
                        <span>Manager: <strong>${p.managerName}</strong></span>
                    </div>
                    <div class="progress-bar-container" style="margin-bottom:12px;">
                        <div class="progress-track">
                            <div class="progress-fill ${p.progress === 100 ? 'success' : ''}" style="width:${p.progress}%;"></div>
                        </div>
                        <span class="progress-percent">${p.progress}%</span>
                    </div>
                </div>
                <div style="display:flex; justify-content:space-between; align-items:center; border-top:1px solid var(--border-light); padding-top:12px; margin-top:8px;">
                    <span style="font-size:12px; font-weight:600; color:var(--text-muted);">Budget: ${p.budget}</span>
                    <div style="display:flex; gap:6px;">
                        <button class="btn btn-secondary btn-sm" onclick="window.dashboard.openEditProjectModal(${p.id})">✏️ Edit</button>
                        <button class="btn btn-primary btn-sm" onclick="window.dashboard.openTaskAssignModalForProject(${p.id})">+ Assign Task</button>
                    </div>
                </div>
            </div>
        `).join('');
    }

    renderPmTasksTable(filterStatus = 'all') {
        const tbody = document.getElementById('pmTasksTableBody');
        if (!tbody) return;

        let tasks = window.store.getTasks();
        if (filterStatus !== 'all') {
            tasks = tasks.filter(t => t.status.toLowerCase() === filterStatus.toLowerCase());
        }

        if (tasks.length === 0) {
            tbody.innerHTML = `<tr><td colspan="7" style="text-align:center; padding:30px; color:var(--text-muted);">No tasks found. Click "Assign Task" to assign one.</td></tr>`;
            return;
        }

        tbody.innerHTML = tasks.map(task => `
            <tr>
                <td>
                    <strong style="color:var(--text-primary);">${task.title}</strong>
                    <div style="font-size:11.5px; color:var(--text-muted);">${task.projectTitle}</div>
                </td>
                <td>
                    <div style="display:flex; align-items:center; gap:8px;">
                        <span class="avatar-cell" style="width:26px; height:26px; font-size:10px;">${task.assigneeName.split(' ').map(n=>n[0]).join('')}</span>
                        <span>${task.assigneeName}</span>
                    </div>
                </td>
                <td>${window.ui.getPriorityBadge(task.priority)}</td>
                <td>${window.ui.getStatusBadge(task.status)}</td>
                <td>${window.ui.formatDate(task.dueDate)}</td>
                <td>
                    <div class="progress-bar-container">
                        <div class="progress-track">
                            <div class="progress-fill ${task.progress === 100 ? 'success' : ''}" style="width: ${task.progress}%"></div>
                        </div>
                        <span class="progress-percent">${task.progress}%</span>
                    </div>
                </td>
                <td>
                    <div class="table-actions">
                        <button class="btn btn-secondary btn-sm" onclick="window.dashboard.openEditTaskModal(${task.id})" title="Edit Task">
                            ✏️
                        </button>
                        <button class="btn btn-danger btn-sm" onclick="window.dashboard.deleteTaskAction(${task.id})" title="Delete Task">
                            🗑️
                        </button>
                    </div>
                </td>
            </tr>
        `).join('');
    }

    renderPmProgressReports() {
        const tasks = window.store.getTasks();
        const pendingCount = tasks.filter(t => t.status === 'Pending').length;
        const inProgressCount = tasks.filter(t => t.status === 'In Progress').length;
        const completedCount = tasks.filter(t => t.status === 'Completed').length;

        window.ui.renderDonutChart('pmTaskDistributionChart', [
            { label: 'Completed', value: completedCount, color: '#10b981' },
            { label: 'In Progress', value: inProgressCount, color: '#4f46e5' },
            { label: 'Pending', value: pendingCount, color: '#f59e0b' }
        ]);

        const projects = window.store.getProjects();
        const projectBars = projects.map(p => ({
            label: p.title.length > 22 ? p.title.substring(0, 20) + '...' : p.title,
            value: p.progress,
            display: `${p.progress}% (${p.status})`,
            color: p.progress === 100 ? '#10b981' : p.progress > 50 ? '#4f46e5' : '#f59e0b'
        }));

        window.ui.renderBarChart('pmProjectProgressBarChart', projectBars);
    }

    renderPmTeamMembers() {
        const container = document.getElementById('pmTeamGrid');
        if (!container) return;

        const teamMembers = window.store.getUsers().filter(u => u.role === 'TEAM_MEMBER');
        const tasks = window.store.getTasks();

        container.innerHTML = teamMembers.map(member => {
            const memberTasks = tasks.filter(t => t.assigneeId === member.id);
            const completed = memberTasks.filter(t => t.status === 'Completed').length;
            const active = memberTasks.filter(t => t.status !== 'Completed').length;
            const hours = memberTasks.reduce((sum, t) => sum + (t.loggedHours || 0), 0);
            const efficiency = memberTasks.length > 0 ? Math.round((completed / memberTasks.length) * 100) : 100;

            return `
                <div class="member-card">
                    <div class="member-card-top">
                        <div class="avatar-cell" style="width:42px; height:42px; font-size:14px; background:linear-gradient(135deg, #a5b4fc, #6366f1); color:white;">
                            ${member.avatar}
                        </div>
                        <div>
                            <strong style="font-size:14px; color:var(--text-primary); display:block;">${member.name}</strong>
                            <span style="font-size:12px; color:var(--text-muted);">${member.title}</span>
                        </div>
                    </div>
                    <div style="font-size:12px; color:var(--text-secondary);">
                        📧 ${member.email} | 🏢 ${member.department}
                    </div>
                    <div class="member-card-stats">
                        <div>
                            <div class="member-card-stat-val">${active}</div>
                            <div class="member-card-stat-lbl">Active Tasks</div>
                        </div>
                        <div>
                            <div class="member-card-stat-val">${completed}</div>
                            <div class="member-card-stat-lbl">Completed</div>
                        </div>
                        <div>
                            <div class="member-card-stat-val">${hours}h</div>
                            <div class="member-card-stat-lbl">Logged Hours</div>
                        </div>
                        <div>
                            <div class="member-card-stat-val" style="color:var(--success);">${efficiency}%</div>
                            <div class="member-card-stat-lbl">Efficiency</div>
                        </div>
                    </div>
                </div>
            `;
        }).join('');
    }

    // --- Task Actions ---
    openAddTaskModal() {
        const form = document.getElementById('taskForm');
        if (form) {
            form.reset();
            form.taskId.value = '';
            document.getElementById('taskModalTitle').textContent = 'Assign New Task';
        }
        this.populateSelectDropdowns();
        window.ui.openModal('taskModal');
    }

    openTaskAssignModalForProject(projectId) {
        this.openAddTaskModal();
        const form = document.getElementById('taskForm');
        if (form && form.projectId) {
            form.projectId.value = projectId;
        }
    }

    openEditTaskModal(taskId) {
        const task = window.store.getTaskById(taskId);
        if (!task) return;

        this.populateSelectDropdowns();
        const form = document.getElementById('taskForm');
        if (form) {
            form.taskId.value = task.id;
            form.title.value = task.title;
            form.description.value = task.description;
            form.projectId.value = task.projectId;
            form.assigneeId.value = task.assigneeId;
            form.priority.value = task.priority;
            form.status.value = task.status;
            form.dueDate.value = task.dueDate;
            form.progress.value = task.progress;
            form.loggedHours.value = task.loggedHours || 0;
            form.estimatedHours.value = task.estimatedHours || 20;
            document.getElementById('taskModalTitle').textContent = 'Update Task Assignment';
        }
        window.ui.openModal('taskModal');
    }

    handleTaskFormSubmit(event) {
        event.preventDefault();
        const form = event.target;
        const taskId = form.taskId.value;

        const taskData = {
            title: form.title.value.trim(),
            description: form.description.value.trim(),
            projectId: Number(form.projectId.value),
            assigneeId: Number(form.assigneeId.value),
            priority: form.priority.value,
            status: form.status.value,
            dueDate: form.dueDate.value,
            progress: Number(form.progress.value) || 0,
            loggedHours: Number(form.loggedHours.value) || 0,
            estimatedHours: Number(form.estimatedHours.value) || 20
        };

        if (taskId) {
            window.store.updateTask(taskId, taskData);
            window.ui.showToast(`Task "${taskData.title}" details updated.`, 'success', 'Task Updated');
        } else {
            const newTask = window.store.addTask(taskData);
            window.ui.showToast(`Task assigned to ${newTask.assigneeName} successfully.`, 'success', 'Task Assigned');
        }

        window.ui.closeModal('taskModal');
        this.renderPmDashboard();
        this.renderAdminDashboard();
        this.renderTeamDashboard();
    }

    deleteTaskAction(taskId) {
        const task = window.store.getTaskById(taskId);
        if (!task) return;

        if (confirm(`Are you sure you want to delete task "${task.title}"?`)) {
            window.store.deleteTask(taskId);
            window.ui.showToast(`Task "${task.title}" deleted.`, 'success', 'Task Removed');
            this.renderPmDashboard();
            this.renderAdminDashboard();
            this.renderTeamDashboard();
        }
    }

    // =========================================================================
    // TEAM MEMBER DASHBOARD
    // =========================================================================
    renderTeamDashboard() {
        const currentUser = window.store.getCurrentUser();
        this.renderTeamMetrics(currentUser);
        this.renderTeamTasksTable(currentUser);
        this.renderTeamProjectsList();
        this.renderTeamProfile(currentUser);
        this.renderActivityFeeds();
    }

    renderTeamMetrics(currentUser) {
        const tasks = window.store.getTasks().filter(t => t.assigneeId === currentUser.id);
        const completed = tasks.filter(t => t.status === 'Completed').length;
        const pending = tasks.filter(t => t.status === 'Pending').length;
        const inProgress = tasks.filter(t => t.status === 'In Progress').length;
        const totalHours = tasks.reduce((sum, t) => sum + (t.loggedHours || 0), 0);

        const assignedEl = document.getElementById('teamStatAssigned');
        if (assignedEl) assignedEl.textContent = tasks.length;

        const inProgressEl = document.getElementById('teamStatInProgress');
        if (inProgressEl) inProgressEl.textContent = inProgress;

        const completedEl = document.getElementById('teamStatCompleted');
        if (completedEl) completedEl.textContent = completed;

        const hoursEl = document.getElementById('teamStatHours');
        if (hoursEl) hoursEl.textContent = `${totalHours}h`;
    }

    renderTeamTasksTable(currentUser, filterStatus = 'all') {
        const tbody = document.getElementById('teamTasksTableBody');
        if (!tbody) return;

        let tasks = window.store.getTasks().filter(t => t.assigneeId === currentUser.id);
        if (filterStatus !== 'all') {
            tasks = tasks.filter(t => t.status.toLowerCase() === filterStatus.toLowerCase());
        }

        if (tasks.length === 0) {
            tbody.innerHTML = `<tr><td colspan="7" style="text-align:center; padding:30px; color:var(--text-muted);">No assigned tasks matching current filter.</td></tr>`;
            return;
        }

        tbody.innerHTML = tasks.map(task => `
            <tr>
                <td>
                    <strong style="color:var(--text-primary); font-size:13.5px;">${task.title}</strong>
                    <div style="font-size:12px; color:var(--text-muted); margin-top:2px;">${task.description}</div>
                </td>
                <td>
                    <button class="btn btn-secondary btn-sm" onclick="window.dashboard.openProjectDetailModal(${task.projectId})" style="font-size:11.5px;">
                        📁 ${task.projectTitle}
                    </button>
                </td>
                <td>${window.ui.getPriorityBadge(task.priority)}</td>
                <td>${window.ui.getStatusBadge(task.status)}</td>
                <td>${window.ui.formatDate(task.dueDate)}</td>
                <td>
                    <div class="progress-bar-container">
                        <div class="progress-track">
                            <div class="progress-fill ${task.progress === 100 ? 'success' : ''}" style="width: ${task.progress}%"></div>
                        </div>
                        <span class="progress-percent">${task.progress}%</span>
                    </div>
                </td>
                <td>
                    <button class="btn btn-primary btn-sm" onclick="window.dashboard.openStatusUpdateModal(${task.id})">
                        🔄 Update Status
                    </button>
                </td>
            </tr>
        `).join('');
    }

    renderTeamProjectsList() {
        const container = document.getElementById('teamProjectsList');
        if (!container) return;

        const currentUser = window.store.getCurrentUser();
        const tasks = window.store.getTasks().filter(t => t.assigneeId === currentUser.id);
        const myProjectIds = [...new Set(tasks.map(t => t.projectId))];
        const allProjects = window.store.getProjects();
        const involvedProjects = allProjects.filter(p => myProjectIds.includes(p.id));

        const projectsToDisplay = involvedProjects.length > 0 ? involvedProjects : allProjects;

        container.innerHTML = projectsToDisplay.map(project => `
            <div class="metric-card" style="margin-bottom:0;">
                <div style="display:flex; justify-content:space-between; align-items:flex-start; margin-bottom:8px;">
                    <h4 style="font-size:15px; font-weight:700; color:var(--text-primary);">${project.title}</h4>
                    ${window.ui.getStatusBadge(project.status)}
                </div>
                <p style="font-size:12.5px; color:var(--text-secondary); margin-bottom:12px;">${project.description}</p>
                <div style="font-size:12px; color:var(--text-muted); margin-bottom:12px;">
                    <div>📅 Timeline: <strong>${window.ui.formatDate(project.startDate)}</strong> to <strong>${window.ui.formatDate(project.endDate)}</strong></div>
                    <div>👤 Manager: <strong>${project.managerName}</strong></div>
                </div>
                <div class="progress-bar-container" style="margin-bottom:12px;">
                    <div class="progress-track">
                        <div class="progress-fill ${project.progress === 100 ? 'success' : ''}" style="width:${project.progress}%;"></div>
                    </div>
                    <span class="progress-percent">${project.progress}%</span>
                </div>
                <button class="btn btn-secondary btn-sm" style="width:100%; justify-content:center;" onclick="window.dashboard.openProjectDetailModal(${project.id})">
                    View Full Details & Timeline
                </button>
            </div>
        `).join('');
    }

    renderTeamProfile(currentUser) {
        const form = document.getElementById('profileForm');
        if (!form) return;

        form.profileName.value = currentUser.name || '';
        form.profileEmail.value = currentUser.email || '';
        form.profilePhone.value = currentUser.phone || '';
        form.profileDepartment.value = currentUser.department || '';
        form.profileTitle.value = currentUser.title || '';
        form.profileBio.value = currentUser.bio || '';

        const profileAvatarDisplay = document.getElementById('profileAvatarDisplay');
        if (profileAvatarDisplay) profileAvatarDisplay.textContent = currentUser.avatar;

        const profileHeaderName = document.getElementById('profileHeaderName');
        if (profileHeaderName) profileHeaderName.textContent = currentUser.name;

        const profileHeaderRole = document.getElementById('profileHeaderRole');
        if (profileHeaderRole) profileHeaderRole.textContent = currentUser.title || currentUser.role;
    }

    handleProfileFormSubmit(event) {
        event.preventDefault();
        const form = event.target;
        const currentUser = window.store.getCurrentUser();

        const updated = {
            name: form.profileName.value.trim(),
            email: form.profileEmail.value.trim(),
            phone: form.profilePhone.value.trim(),
            department: form.profileDepartment.value.trim(),
            title: form.profileTitle.value.trim(),
            bio: form.profileBio.value.trim()
        };

        if (form.profilePassword && form.profilePassword.value) {
            // Simulated password update
            updated.passwordUpdated = true;
        }

        window.store.updateUser(currentUser.id, updated);
        window.ui.showToast("Personal profile details updated successfully.", "success", "Profile Saved");
        this.renderTeamProfile(window.store.getCurrentUser());
    }

    // --- Status Updater for Team Member ---
    openStatusUpdateModal(taskId) {
        const task = window.store.getTaskById(taskId);
        if (!task) return;

        const form = document.getElementById('statusUpdateForm');
        if (form) {
            form.taskId.value = task.id;
            document.getElementById('statusTaskTitle').textContent = task.title;
            document.getElementById('statusProjectName').textContent = task.projectTitle;
            form.taskStatus.value = task.status;
            form.taskProgress.value = task.progress;
            document.getElementById('progressOutputVal').textContent = `${task.progress}%`;
            form.loggedHours.value = task.loggedHours || 0;
            form.updateNotes.value = '';
        }
        window.ui.openModal('statusUpdateModal');
    }

    handleStatusUpdateSubmit(event) {
        event.preventDefault();
        const form = event.target;
        const taskId = form.taskId.value;
        const status = form.taskStatus.value;
        let progress = Number(form.taskProgress.value);
        const loggedHours = Number(form.loggedHours.value);
        const notes = form.updateNotes.value.trim();

        // Auto align 100% with completed status
        if (status === 'Completed' && progress < 100) {
            progress = 100;
        } else if (status !== 'Completed' && progress === 100) {
            progress = 90;
        }

        const updated = window.store.updateTask(taskId, {
            status: status,
            progress: progress,
            loggedHours: loggedHours
        });

        if (notes) {
            window.store.logActivity(`Note added on '${updated.title}': ${notes}`, 'task');
        }

        window.ui.showToast(`Updated task status to "${status}" (${progress}% complete).`, "success", "Status Updated");
        window.ui.closeModal('statusUpdateModal');
        this.renderTeamDashboard();
        this.renderPmDashboard();
        this.renderAdminDashboard();
    }

    // --- Project Detail Modal ---
    openProjectDetailModal(projectId) {
        const project = window.store.getProjectById(projectId);
        if (!project) return;

        const modal = document.getElementById('projectDetailModal');
        if (!modal) return;

        document.getElementById('detailProjectTitle').textContent = project.title;
        document.getElementById('detailProjectDescription').textContent = project.description;
        document.getElementById('detailProjectTimeline').textContent = `${window.ui.formatDate(project.startDate)} to ${window.ui.formatDate(project.endDate)}`;
        document.getElementById('detailProjectManager').textContent = project.managerName;
        document.getElementById('detailProjectStatusBadge').innerHTML = window.ui.getStatusBadge(project.status);
        document.getElementById('detailProjectBudget').textContent = project.budget;
        document.getElementById('detailProjectProgressVal').textContent = `${project.progress}%`;
        document.getElementById('detailProjectProgressBar').style.width = `${project.progress}%`;

        // Associated tasks
        const tasks = window.store.getTasks().filter(t => t.projectId === project.id);
        const taskContainer = document.getElementById('detailProjectTaskList');
        if (taskContainer) {
            if (tasks.length === 0) {
                taskContainer.innerHTML = `<li style="padding:10px; color:var(--text-muted);">No tasks assigned under this project yet.</li>`;
            } else {
                taskContainer.innerHTML = tasks.map(t => `
                    <li style="display:flex; justify-content:space-between; align-items:center; padding:10px 0; border-bottom:1px solid var(--border-light);">
                        <div>
                            <strong style="font-size:13px; color:var(--text-primary);">${t.title}</strong>
                            <div style="font-size:11.5px; color:var(--text-muted);">Assigned to: ${t.assigneeName} | Due: ${window.ui.formatDate(t.dueDate)}</div>
                        </div>
                        <div>
                            ${window.ui.getStatusBadge(t.status)}
                        </div>
                    </li>
                `).join('');
            }
        }

        window.ui.openModal('projectDetailModal');
    }

    // --- Activity Feed ---
    renderActivityFeeds() {
        const activities = window.store.getActivities();
        const containers = document.querySelectorAll('.activity-feed-container');

        const icons = {
            task: '📋',
            project: '🚀',
            system: '⚙️',
            general: '🔔'
        };

        const bgColors = {
            task: 'var(--primary-light)',
            project: 'var(--info-light)',
            system: 'var(--warning-light)',
            general: 'var(--success-light)'
        };

        containers.forEach(container => {
            if (activities.length === 0) {
                container.innerHTML = `<p style="color:var(--text-muted); font-size:13px;">No recent activities logged.</p>`;
                return;
            }

            container.innerHTML = `
                <ul class="activity-feed-list">
                    ${activities.map(act => `
                        <li class="activity-item">
                            <div class="activity-icon-bullet" style="background:${bgColors[act.type] || '#f1f5f9'};">
                                ${icons[act.type] || '•'}
                            </div>
                            <div class="activity-info">
                                <p><strong>${act.user}</strong>: ${act.action}</p>
                                <span class="activity-time">${act.timestamp}</span>
                            </div>
                        </li>
                    `).join('')}
                </ul>
            `;
        });
    }

    // Populate dropdowns (Managers, Projects, Assignees)
    populateSelectDropdowns() {
        const users = window.store.getUsers();
        const projects = window.store.getProjects();

        // Quick user select in header
        const userQuickSelect = document.getElementById('userQuickSelect');
        if (userQuickSelect) {
            const current = window.store.getCurrentUser();
            userQuickSelect.innerHTML = users.map(u => `
                <option value="${u.id}" ${u.id === current.id ? 'selected' : ''}>
                    ${u.name} (${u.role === 'ADMIN' ? 'Admin' : u.role === 'PROJECT_MANAGER' ? 'Project Manager' : 'Team Member'})
                </option>
            `).join('');
        }

        // Project Form Manager Select
        const pmSelect = document.getElementById('projectManagerSelect');
        if (pmSelect) {
            const pms = users.filter(u => u.role === 'PROJECT_MANAGER' || u.role === 'ADMIN');
            pmSelect.innerHTML = pms.map(u => `
                <option value="${u.id}">${u.name} (${u.department})</option>
            `).join('');
        }

        // Task Form Project Select
        const taskProjectSelect = document.getElementById('taskProjectSelect');
        if (taskProjectSelect) {
            taskProjectSelect.innerHTML = projects.map(p => `
                <option value="${p.id}">${p.title}</option>
            `).join('');
        }

        // Task Form Assignee Select
        const taskAssigneeSelect = document.getElementById('taskAssigneeSelect');
        if (taskAssigneeSelect) {
            const members = users.filter(u => u.role === 'TEAM_MEMBER');
            taskAssigneeSelect.innerHTML = members.map(m => `
                <option value="${m.id}">${m.name} (${m.department})</option>
            `).join('');
        }
    }

    // Initial App Boot
    boot() {
        this.populateSelectDropdowns();
        const current = window.store.getCurrentUser();
        this.handleUserChange(current);
        this.renderActivityFeeds();
    }
}

window.dashboard = new DashboardController();
document.addEventListener('DOMContentLoaded', () => {
    window.dashboard.boot();
});
