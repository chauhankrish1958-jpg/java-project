/**
 * ProTrack Enterprise - Frontend JavaScript Controller
 * Handles Modals, Quick Actions, Client Search, Viva Credentials Auto-fill & Chart.js Visualizations
 */

document.addEventListener('DOMContentLoaded', () => {

    // 1. Mobile Sidebar Toggle
    const sidebarToggle = document.getElementById('sidebarToggle');
    const sidebar = document.querySelector('.sidebar');
    if (sidebarToggle && sidebar) {
        sidebarToggle.addEventListener('click', () => {
            sidebar.classList.toggle('show');
        });
    }

    // 2. Auto-dismiss Alert Banners after 5 seconds
    const alerts = document.querySelectorAll('.alert-dismissible');
    alerts.forEach(alert => {
        setTimeout(() => {
            const bsAlert = bootstrap.Alert.getOrCreateInstance(alert);
            if (bsAlert) bsAlert.close();
        }, 5000);
    });

    // 3. Client-side Instant Table Search
    const searchInputs = document.querySelectorAll('.table-search-input');
    searchInputs.forEach(input => {
        input.addEventListener('keyup', (e) => {
            const query = e.target.value.toLowerCase().trim();
            const targetTableId = input.getAttribute('data-target');
            const targetTable = document.getElementById(targetTableId);
            if (!targetTable) return;

            const rows = targetTable.querySelectorAll('tbody tr');
            rows.forEach(row => {
                const text = row.innerText.toLowerCase();
                row.style.display = text.includes(query) ? '' : 'none';
            });
        });
    });

    // 4. Quick-fill Demo Credentials (For Academic / Viva Demonstrations)
    window.fillCredentials = function(email, password) {
        const emailInput = document.getElementById('loginEmail');
        const passInput = document.getElementById('loginPassword');
        if (emailInput && passInput) {
            emailInput.value = email;
            passInput.value = password;
            // Highlight briefly
            emailInput.classList.add('is-valid');
            passInput.classList.add('is-valid');
            setTimeout(() => {
                emailInput.classList.remove('is-valid');
                passInput.classList.remove('is-valid');
            }, 1000);
        }
    };

    // 5. User Edit Modal Population
    const editUserModal = document.getElementById('editUserModal');
    if (editUserModal) {
        editUserModal.addEventListener('show.bs.modal', (event) => {
            const button = event.relatedTarget;
            if (!button) return;

            const id = button.getAttribute('data-id');
            const name = button.getAttribute('data-name');
            const email = button.getAttribute('data-email');
            const role = button.getAttribute('data-role');
            const status = button.getAttribute('data-status');

            document.getElementById('editUserId').value = id || '';
            document.getElementById('editUserName').value = name || '';
            document.getElementById('editUserEmail').value = email || '';
            document.getElementById('editUserRole').value = role || 'TEAM_MEMBER';
            document.getElementById('editUserStatus').value = status || 'ACTIVE';
        });
    }

    // 6. Project Edit Modal Population
    const editProjectModal = document.getElementById('editProjectModal');
    if (editProjectModal) {
        editProjectModal.addEventListener('show.bs.modal', (event) => {
            const button = event.relatedTarget;
            if (!button) return;

            document.getElementById('editProjectId').value = button.getAttribute('data-id') || '';
            document.getElementById('editProjectTitle').value = button.getAttribute('data-title') || '';
            document.getElementById('editProjectDesc').value = button.getAttribute('data-desc') || '';
            document.getElementById('editProjectStartDate').value = button.getAttribute('data-start') || '';
            document.getElementById('editProjectEndDate').value = button.getAttribute('data-end') || '';

            const managerSelect = document.getElementById('editProjectManager');
            if (managerSelect) {
                managerSelect.value = button.getAttribute('data-manager') || '';
            }

            const statusSelect = document.getElementById('editProjectStatus');
            if (statusSelect) {
                statusSelect.value = button.getAttribute('data-status') || 'PLANNING';
            }
        });
    }

    // 7. Task Edit Modal Population
    const editTaskModal = document.getElementById('editTaskModal');
    if (editTaskModal) {
        editTaskModal.addEventListener('show.bs.modal', (event) => {
            const button = event.relatedTarget;
            if (!button) return;

            document.getElementById('editTaskId').value = button.getAttribute('data-id') || '';
            document.getElementById('editTaskTitle').value = button.getAttribute('data-title') || '';
            document.getElementById('editTaskDesc').value = button.getAttribute('data-desc') || '';
            document.getElementById('editTaskProject').value = button.getAttribute('data-project') || '';

            const assigneeSelect = document.getElementById('editTaskAssignee');
            if (assigneeSelect) {
                assigneeSelect.value = button.getAttribute('data-assigned') || '';
            }

            document.getElementById('editTaskPriority').value = button.getAttribute('data-priority') || 'MEDIUM';
            document.getElementById('editTaskStatus').value = button.getAttribute('data-status') || 'TODO';
            document.getElementById('editTaskStartDate').value = button.getAttribute('data-start') || '';
            document.getElementById('editTaskDueDate').value = button.getAttribute('data-due') || '';
        });
    }

    // 8. Quick Task Status Change Modal (Used by Team Members)
    const updateStatusModal = document.getElementById('updateStatusModal');
    if (updateStatusModal) {
        updateStatusModal.addEventListener('show.bs.modal', (event) => {
            const button = event.relatedTarget;
            if (!button) return;

            document.getElementById('statusTaskId').value = button.getAttribute('data-id') || '';
            document.getElementById('statusTaskTitle').innerText = button.getAttribute('data-title') || 'Task';
            document.getElementById('statusTaskSelect').value = button.getAttribute('data-status') || 'TODO';
        });
    }

    // 9. Generic Delete Confirmation Modal
    const deleteModal = document.getElementById('deleteConfirmModal');
    if (deleteModal) {
        deleteModal.addEventListener('show.bs.modal', (event) => {
            const button = event.relatedTarget;
            if (!button) return;

            const id = button.getAttribute('data-id');
            const name = button.getAttribute('data-name') || 'this item';
            const actionUrl = button.getAttribute('data-action');

            document.getElementById('deleteItemName').innerText = name;
            const deleteForm = document.getElementById('deleteForm');
            if (deleteForm) {
                if (actionUrl) deleteForm.action = actionUrl;
                const idInput = deleteForm.querySelector('input[name="id"], input[name="userId"], input[name="projectId"], input[name="taskId"]');
                if (idInput) idInput.value = id;
            }
        });
    }
});

/**
 * Chart Initialization Helper Functions using Chart.js
 */
window.initAdminCharts = function(statusData, priorityData) {
    // 1. Project Status Doughnut Chart
    const statusCtx = document.getElementById('projectStatusChart');
    if (statusCtx && statusData) {
        new Chart(statusCtx, {
            type: 'doughnut',
            data: {
                labels: ['Planning', 'In Progress', 'On Hold', 'Completed', 'Cancelled'],
                datasets: [{
                    data: [
                        statusData.PLANNING || 0,
                        statusData.IN_PROGRESS || 0,
                        statusData.ON_HOLD || 0,
                        statusData.COMPLETED || 0,
                        statusData.CANCELLED || 0
                    ],
                    backgroundColor: [
                        '#0ea5e9', // Planning (Sky blue)
                        '#6366f1', // In Progress (Indigo)
                        '#f59e0b', // On Hold (Amber)
                        '#10b981', // Completed (Emerald)
                        '#ef4444'  // Cancelled (Red)
                    ],
                    borderWidth: 2,
                    borderColor: '#ffffff'
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'bottom',
                        labels: { boxWidth: 12, padding: 15, font: { family: 'Inter', size: 12 } }
                    }
                },
                cutout: '65%'
            }
        });
    }

    // 2. Task Priority Bar Chart
    const priorityCtx = document.getElementById('taskPriorityChart');
    if (priorityCtx && priorityData) {
        new Chart(priorityCtx, {
            type: 'bar',
            data: {
                labels: ['Low', 'Medium', 'High', 'Critical'],
                datasets: [{
                    label: 'Tasks',
                    data: [
                        priorityData.LOW || 0,
                        priorityData.MEDIUM || 0,
                        priorityData.HIGH || 0,
                        priorityData.CRITICAL || 0
                    ],
                    backgroundColor: [
                        'rgba(14, 165, 233, 0.85)',
                        'rgba(100, 116, 139, 0.85)',
                        'rgba(245, 158, 11, 0.85)',
                        'rgba(239, 68, 68, 0.85)'
                    ],
                    borderRadius: 6
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { display: false }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: { stepSize: 1, font: { family: 'Inter' } },
                        grid: { color: '#f1f5f9' }
                    },
                    x: {
                        grid: { display: false },
                        ticks: { font: { family: 'Inter' } }
                    }
                }
            }
        });
    }
};
