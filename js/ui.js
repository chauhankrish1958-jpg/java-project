/**
 * UI Utilities and Shared Components
 * Handles Modals, Toast Notifications, SVG Charts, and Role Switching
 */

class UIManager {
    constructor() {
        this.initToastContainer();
    }

    // --- Toast Notifications ---
    initToastContainer() {
        if (!document.getElementById('toastContainer')) {
            const container = document.createElement('div');
            container.id = 'toastContainer';
            container.className = 'toast-container';
            document.body.appendChild(container);
        }
    }

    showToast(message, type = 'success', title = '') {
        const container = document.getElementById('toastContainer') || document.body;
        const toast = document.createElement('div');
        toast.className = `toast toast-${type}`;

        const icons = {
            success: '✓',
            error: '✕',
            warning: '⚠',
            info: 'ℹ'
        };

        const titles = {
            success: title || 'Success',
            error: title || 'Error',
            warning: title || 'Warning',
            info: title || 'Information'
        };

        toast.innerHTML = `
            <div class="toast-icon">${icons[type] || '•'}</div>
            <div class="toast-content">
                <div class="toast-title">${titles[type]}</div>
                <div class="toast-message">${message}</div>
            </div>
            <button class="toast-close" onclick="this.parentElement.remove()">✕</button>
        `;

        container.appendChild(toast);

        setTimeout(() => {
            if (toast && toast.parentElement) {
                toast.style.opacity = '0';
                toast.style.transform = 'translateX(100%)';
                toast.style.transition = 'all 0.3s ease-out';
                setTimeout(() => toast.remove(), 300);
            }
        }, 4000);
    }

    // --- Modals ---
    openModal(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) {
            modal.classList.add('active');
            document.body.style.overflow = 'hidden';
        }
    }

    closeModal(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) {
            modal.classList.remove('active');
            document.body.style.overflow = '';
        }
    }

    // --- Formatters ---
    formatDate(dateStr) {
        if (!dateStr) return 'N/A';
        const d = new Date(dateStr);
        if (isNaN(d.getTime())) return dateStr;
        return d.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
    }

    getStatusBadge(status) {
        const clean = (status || '').toLowerCase().replace(/\s+/g, '-');
        return `<span class="badge badge-${clean}">${status}</span>`;
    }

    getPriorityBadge(priority) {
        const clean = (priority || '').toLowerCase();
        return `<span class="badge badge-${clean}">${priority}</span>`;
    }

    getRoleBadge(role) {
        if (role === 'ADMIN') return `<span class="badge badge-admin">Admin</span>`;
        if (role === 'PROJECT_MANAGER') return `<span class="badge badge-pm">Project Manager</span>`;
        return `<span class="badge badge-team">Team Member</span>`;
    }

    // --- SVG Charts (No external CDN dependency needed) ---
    renderDonutChart(containerId, data) {
        const container = document.getElementById(containerId);
        if (!container) return;

        const total = data.reduce((acc, item) => acc + item.value, 0) || 1;
        let cumulativeAngle = 0;
        const radius = 60;
        const cx = 90;
        const cy = 90;
        const strokeWidth = 24;

        let paths = '';
        let legend = '<div style="display:flex; flex-direction:column; gap:6px; margin-left:20px;">';

        data.forEach(item => {
            const angle = (item.value / total) * 360;
            const startAngle = cumulativeAngle;
            const endAngle = cumulativeAngle + angle;
            cumulativeAngle = endAngle;

            const startRad = (startAngle - 90) * Math.PI / 180;
            const endRad = (endAngle - 90) * Math.PI / 180;

            const x1 = cx + radius * Math.cos(startRad);
            const y1 = cy + radius * Math.sin(startRad);
            const x2 = cx + radius * Math.cos(endRad);
            const y2 = cy + radius * Math.sin(endRad);

            const largeArc = angle > 180 ? 1 : 0;
            const pathData = `M ${x1} ${y1} A ${radius} ${radius} 0 ${largeArc} 1 ${x2} ${y2}`;

            paths += `<path d="${pathData}" fill="none" stroke="${item.color}" stroke-width="${strokeWidth}" stroke-linecap="round" />`;

            legend += `
                <div style="display:flex; align-items:center; gap:8px; font-size:12.5px;">
                    <span style="width:10px; height:10px; border-radius:50%; background:${item.color};"></span>
                    <span style="color:var(--text-secondary);">${item.label}:</span>
                    <strong style="color:var(--text-primary); margin-left:auto;">${item.value}</strong>
                </div>
            `;
        });

        legend += '</div>';

        container.innerHTML = `
            <div style="display:flex; align-items:center; justify-content:center; padding:10px;">
                <svg width="180" height="180" viewBox="0 0 180 180">
                    <circle cx="${cx}" cy="${cy}" r="${radius}" fill="none" stroke="#f1f5f9" stroke-width="${strokeWidth}" />
                    ${paths}
                    <text x="${cx}" y="${cy - 4}" text-anchor="middle" font-size="19" font-weight="700" fill="var(--text-primary)">${total}</text>
                    <text x="${cx}" y="${cy + 16}" text-anchor="middle" font-size="11" fill="var(--text-muted)">TOTAL</text>
                </svg>
                ${legend}
            </div>
        `;
    }

    renderBarChart(containerId, items) {
        const container = document.getElementById(containerId);
        if (!container) return;

        let bars = '';
        items.forEach(item => {
            const percent = Math.min(100, Math.max(0, item.value));
            bars += `
                <div style="margin-bottom:12px;">
                    <div style="display:flex; justify-content:space-between; font-size:12.5px; margin-bottom:5px;">
                        <span style="font-weight:500; color:var(--text-secondary);">${item.label}</span>
                        <span style="font-weight:600; color:var(--text-primary);">${item.display || (percent + '%')}</span>
                    </div>
                    <div style="height:10px; background:#f1f5f9; border-radius:99px; overflow:hidden;">
                        <div style="height:100%; width:${percent}%; background:${item.color || 'var(--primary)'}; border-radius:99px; transition:width 0.4s ease;"></div>
                    </div>
                </div>
            `;
        });

        container.innerHTML = bars;
    }
}

window.ui = new UIManager();
