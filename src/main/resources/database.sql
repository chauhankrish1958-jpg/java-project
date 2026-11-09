-- ==============================================================================
-- WARNING: DESTRUCTIVE SCRIPT / DATA LOSS WARNING
-- ==============================================================================
-- This script contains DROP TABLE IF EXISTS statements that will permanently
-- delete all existing schema tables, data, users, projects, tasks, and audit logs.
-- DO NOT RUN IN A PRODUCTION ENVIRONMENT WITHOUT A BACKUP.
-- Intended for fresh development initialization and academic review only.
-- ==============================================================================
-- ProTrack - Online Project Management Tool Database Schema & Seed Data
-- Database: project_management_db
-- Compatibility: MySQL 5.7+ / MySQL 8.0+ / MariaDB 10.3+
-- ==============================================================================

CREATE DATABASE IF NOT EXISTS `project_management_db`
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE `project_management_db`;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `activity_logs`;
DROP TABLE IF EXISTS `tasks`;
DROP TABLE IF EXISTS `project_members`;
DROP TABLE IF EXISTS `projects`;
DROP TABLE IF EXISTS `system_settings`;
DROP TABLE IF EXISTS `users`;

SET FOREIGN_KEY_CHECKS = 1;

-- Table 1: users
CREATE TABLE `users` (
    `user_id` INT AUTO_INCREMENT PRIMARY KEY,
    `full_name` VARCHAR(100) NOT NULL,
    `email` VARCHAR(100) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `role` ENUM('ADMIN', 'PROJECT_MANAGER', 'TEAM_MEMBER') NOT NULL DEFAULT 'TEAM_MEMBER',
    `status` ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_users_role` (`role`),
    INDEX `idx_users_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table 2: projects
CREATE TABLE `projects` (
    `project_id` INT AUTO_INCREMENT PRIMARY KEY,
    `title` VARCHAR(150) NOT NULL,
    `description` TEXT,
    `start_date` DATE NOT NULL,
    `end_date` DATE NOT NULL,
    `manager_id` INT NOT NULL,
    `status` ENUM('PLANNING', 'IN_PROGRESS', 'ON_HOLD', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'PLANNING',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_projects_manager`
        FOREIGN KEY (`manager_id`) REFERENCES `users` (`user_id`)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    INDEX `idx_projects_manager` (`manager_id`),
    INDEX `idx_projects_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table 3: project_members
CREATE TABLE `project_members` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `project_id` INT NOT NULL,
    `user_id` INT NOT NULL,
    `assigned_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_pm_project`
        FOREIGN KEY (`project_id`) REFERENCES `projects` (`project_id`)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_pm_user`
        FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `uk_project_user` UNIQUE (`project_id`, `user_id`),
    INDEX `idx_pm_project` (`project_id`),
    INDEX `idx_pm_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table 4: tasks
CREATE TABLE `tasks` (
    `task_id` INT AUTO_INCREMENT PRIMARY KEY,
    `task_title` VARCHAR(200) NOT NULL,
    `description` TEXT,
    `project_id` INT NOT NULL,
    `assigned_to` INT NULL,
    `priority` ENUM('LOW', 'MEDIUM', 'HIGH', 'CRITICAL') NOT NULL DEFAULT 'MEDIUM',
    `status` ENUM('TODO', 'IN_PROGRESS', 'REVIEW', 'COMPLETED') NOT NULL DEFAULT 'TODO',
    `start_date` DATE,
    `due_date` DATE NOT NULL,
    `created_by` INT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_tasks_project`
        FOREIGN KEY (`project_id`) REFERENCES `projects` (`project_id`)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_tasks_assigned`
        FOREIGN KEY (`assigned_to`) REFERENCES `users` (`user_id`)
        ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT `fk_tasks_creator`
        FOREIGN KEY (`created_by`) REFERENCES `users` (`user_id`)
        ON DELETE SET NULL ON UPDATE CASCADE,
    INDEX `idx_tasks_project` (`project_id`),
    INDEX `idx_tasks_assigned` (`assigned_to`),
    INDEX `idx_tasks_status` (`status`),
    INDEX `idx_tasks_priority` (`priority`),
    INDEX `idx_tasks_due_date` (`due_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table 5: activity_logs
CREATE TABLE `activity_logs` (
    `log_id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT NULL,
    `action` VARCHAR(100) NOT NULL,
    `details` TEXT NOT NULL,
    `entity_type` VARCHAR(50) NULL,
    `entity_id` INT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_logs_user`
        FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
        ON DELETE SET NULL ON UPDATE CASCADE,
    INDEX `idx_logs_user` (`user_id`),
    INDEX `idx_logs_entity` (`entity_type`, `entity_id`),
    INDEX `idx_logs_created` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table 6: system_settings
CREATE TABLE `system_settings` (
    `setting_id` INT AUTO_INCREMENT PRIMARY KEY,
    `setting_key` VARCHAR(50) NOT NULL UNIQUE,
    `setting_value` TEXT NOT NULL,
    `description` VARCHAR(255),
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Seed Data (BCrypt hashed passwords)
INSERT INTO `users` (`user_id`, `full_name`, `email`, `password`, `role`, `status`) VALUES
(1, 'Administrator', 'admin@protrack.com', '$2a$10$dkSTgGWz/61knT01f29PqOUiiQE1htpJfrHqw/oNGYZNIaNh2b2IC', 'ADMIN', 'ACTIVE'),
(2, 'Sarah Jenkins', 'sarah.pm@protrack.com', '$2a$10$it8pPyDxU4ZPt7TCx3NJkep1cj1QGTN.N4pJ2oVW6PTuOiLHrNSfC', 'PROJECT_MANAGER', 'ACTIVE'),
(3, 'David Miller', 'david.pm@protrack.com', '$2a$10$it8pPyDxU4ZPt7TCx3NJkep1cj1QGTN.N4pJ2oVW6PTuOiLHrNSfC', 'PROJECT_MANAGER', 'ACTIVE'),
(4, 'Alex Rivera', 'alex.dev@protrack.com', '$2a$10$5qAKugzgpIHjmYpII7mkzOcjQloAUYdFuXnIMZeBvFmRfB5TF5iKa', 'TEAM_MEMBER', 'ACTIVE'),
(5, 'Emma Watson', 'emma.qa@protrack.com', '$2a$10$5qAKugzgpIHjmYpII7mkzOcjQloAUYdFuXnIMZeBvFmRfB5TF5iKa', 'TEAM_MEMBER', 'ACTIVE'),
(6, 'Liam Chen', 'liam.design@protrack.com', '$2a$10$5qAKugzgpIHjmYpII7mkzOcjQloAUYdFuXnIMZeBvFmRfB5TF5iKa', 'TEAM_MEMBER', 'ACTIVE'),
(7, 'Sophia Martinez', 'sophia.dev@protrack.com', '$2a$10$5qAKugzgpIHjmYpII7mkzOcjQloAUYdFuXnIMZeBvFmRfB5TF5iKa', 'TEAM_MEMBER', 'ACTIVE');

-- Projects
INSERT INTO `projects` (`project_id`, `title`, `description`, `start_date`, `end_date`, `manager_id`, `status`) VALUES
(1, 'Enterprise E-Commerce Platform', 'End-to-end multi-vendor marketplace featuring shopping cart, Stripe payment gateway, and dynamic product recommendations.', '2026-01-10', '2026-06-30', 2, 'IN_PROGRESS'),
(2, 'Mobile Banking App Redesign', 'Complete redesign of mobile banking UX with biometric authentication, instant payments, and interactive investment dashboards.', '2026-02-01', '2026-05-15', 3, 'IN_PROGRESS'),
(3, 'Cloud Infrastructure Migration', 'Migrate legacy on-premise application servers to Kubernetes clusters on AWS with automated CI/CD deployment pipelines.', '2026-03-01', '2026-08-31', 2, 'PLANNING'),
(4, 'AI Customer Support Chatbot', 'Build an automated natural language conversational agent integrated with CRM for 24/7 client tier-1 support.', '2025-10-01', '2026-01-15', 3, 'COMPLETED'),
(5, 'Internal HR & Payroll Portal', 'Employee attendance, leave management, payroll processing, and quarterly performance appraisal automation.', '2026-02-15', '2026-07-20', 2, 'ON_HOLD');

-- Project Members Mapping
INSERT INTO `project_members` (`project_id`, `user_id`) VALUES
(1, 4), (1, 5), (1, 6), (1, 7),
(2, 4), (2, 6),
(3, 4), (3, 7),
(4, 5), (4, 7),
(5, 5), (5, 6);

-- Tasks
INSERT INTO `tasks` (`task_id`, `task_title`, `description`, `project_id`, `assigned_to`, `priority`, `status`, `start_date`, `due_date`, `created_by`) VALUES
(1, 'Design Database Schema for Products & Orders', 'Normalize SQL tables for product catalog, inventory variations, categories, order items, and invoices.', 1, 4, 'HIGH', 'COMPLETED', '2026-01-12', '2026-01-25', 2),
(2, 'Integrate Stripe Payment Gateway API', 'Implement secure webhook handlers, 3D-Secure card verification, and automatic refund flows.', 1, 4, 'CRITICAL', 'IN_PROGRESS', '2026-02-01', '2026-03-10', 2),
(3, 'Design UI Mockups for Checkout Funnel', 'Create responsive Figma prototypes for checkout, order summary, address validation, and confirmation screen.', 1, 6, 'MEDIUM', 'COMPLETED', '2026-01-15', '2026-02-05', 2),
(4, 'Automated Test Suite for Shopping Cart', 'Write JUnit integration tests and Selenium web tests covering coupon codes, tax calculations, and cart expiry.', 1, 5, 'HIGH', 'REVIEW', '2026-02-10', '2026-03-05', 2),
(5, 'Implement Elasticsearch Product Search', 'Configure faceted search filters by brand, price range, ratings, and instant autocomplete suggestions.', 1, 7, 'HIGH', 'TODO', '2026-03-01', '2026-03-25', 2),
(6, 'Biometric Authentication Integration', 'Implement Face ID and Fingerprint biometric security with encrypted hardware keychain storage.', 2, 4, 'CRITICAL', 'IN_PROGRESS', '2026-02-05', '2026-02-28', 3),
(7, 'Dark Mode Color Palette & Asset Design', 'Design high-contrast dark theme UI kit with accessible typography and WCAG AAA compliance.', 2, 6, 'MEDIUM', 'COMPLETED', '2026-02-01', '2026-02-18', 3),
(8, 'Security Audit & Penetration Testing', 'Test API endpoints against OWASP Mobile Top 10 vulnerabilities, SSL pinning, and SQL injection.', 2, 5, 'CRITICAL', 'TODO', '2026-02-20', '2026-03-15', 3),
(9, 'Dockerize Microservices Architecture', 'Write multi-stage Dockerfiles for backend services, optimize image sizes, and scan with Trivy.', 3, 7, 'HIGH', 'TODO', '2026-03-05', '2026-03-20', 2),
(10, 'Helm Charts & Terraform Provisioning', 'Write Infrastructure as Code scripts for VPC, EKS cluster, managed RDS instances, and IAM roles.', 3, 4, 'MEDIUM', 'TODO', '2026-03-15', '2026-04-10', 2),
(11, 'Train NLP Intent Classification Model', 'Fine-tune BERT model on 10,000 historical support ticket transcripts for intent detection.', 4, 7, 'HIGH', 'COMPLETED', '2025-10-15', '2025-11-30', 3),
(12, 'Zendesk & Salesforce CRM Integration', 'Connect bot responses to create escalation tickets for tier-2 human representatives.', 4, 5, 'MEDIUM', 'COMPLETED', '2025-12-01', '2026-01-10', 3),
(13, 'Biometric Attendance Clock-in Sync', 'Sync hardware fingerprint machine logs via TCP socket into MySQL attendance table.', 5, 5, 'LOW', 'TODO', '2026-02-20', '2026-04-01', 2);

-- Activity Logs
INSERT INTO `activity_logs` (`user_id`, `action`, `details`, `entity_type`, `entity_id`, `created_at`) VALUES
(1, 'USER_REGISTER', 'Administrator created new team member account for Sophia Martinez.', 'USER', 7, TIMESTAMPADD(DAY, -5, NOW())),
(2, 'PROJECT_CREATE', 'Sarah Jenkins created new project "Enterprise E-Commerce Platform".', 'PROJECT', 1, TIMESTAMPADD(DAY, -4, NOW())),
(2, 'TASK_CREATE', 'Sarah Jenkins created task "Integrate Stripe Payment Gateway API" and assigned to Alex Rivera.', 'TASK', 2, TIMESTAMPADD(DAY, -3, NOW())),
(4, 'TASK_STATUS_UPDATE', 'Alex Rivera changed status of task "Design Database Schema" to COMPLETED.', 'TASK', 1, TIMESTAMPADD(DAY, -2, NOW())),
(3, 'PROJECT_CREATE', 'David Miller created new project "Mobile Banking App Redesign".', 'PROJECT', 2, TIMESTAMPADD(DAY, -2, NOW())),
(6, 'TASK_STATUS_UPDATE', 'Liam Chen changed status of task "Dark Mode Color Palette" to COMPLETED.', 'TASK', 7, TIMESTAMPADD(DAY, -1, NOW())),
(5, 'TASK_STATUS_UPDATE', 'Emma Watson submitted task "Automated Test Suite for Shopping Cart" for REVIEW.', 'TASK', 4, TIMESTAMPADD(HOUR, -3, NOW())),
(1, 'SETTINGS_UPDATE', 'Administrator updated system default task due days to 7 days.', 'SETTINGS', 1, TIMESTAMPADD(HOUR, -1, NOW()));

-- System Settings
INSERT INTO `system_settings` (`setting_id`, `setting_key`, `setting_value`, `description`) VALUES
(1, 'app_name', 'ProTrack Enterprise', 'The brand name of the project management platform'),
(2, 'company_name', 'ProTrack Software Labs', 'Organization or institution hosting this instance'),
(3, 'task_default_due_days', '7', 'Default duration in days when creating new tasks'),
(4, 'allow_user_registration', 'true', 'Enable or disable public self-registration for team members'),
(5, 'notification_banner', 'ProTrack v1.0 Production Ready - Welcome to your project command center!', 'Announcement message displayed on dashboard');
