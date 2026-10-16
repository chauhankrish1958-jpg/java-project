package com.protrack.util;

import com.protrack.model.Project;
import com.protrack.model.Task;
import com.protrack.model.User;

import java.util.List;

/**
 * Console Presentation & Table Formatting Utility.
 * Provides clean terminal outputs, confirmation messages, and ASCII progress visualizations.
 */
public class ConsoleUI {

    // ANSI Color Codes
    public static final String RESET = "\u001B[0m";
    public static final String BOLD = "\u001B[1m";
    public static final String GREEN = "\u001B[32m";
    public static final String BLUE = "\u001B[34m";
    public static final String CYAN = "\u001B[36m";
    public static final String YELLOW = "\u001B[33m";
    public static final String RED = "\u001B[31m";
    public static final String PURPLE = "\u001B[35m";

    public static void printBanner(String title) {
        System.out.println(CYAN + BOLD + "\n==========================================================================================");
        System.out.println("  " + title);
        System.out.println("==========================================================================================" + RESET);
    }

    public static void printSuccess(String message) {
        System.out.println(GREEN + BOLD + "✔ CONFIRMATION: " + RESET + GREEN + message + RESET);
    }

    public static void printError(String message) {
        System.out.println(RED + BOLD + "✖ ERROR: " + RESET + RED + message + RESET);
    }

    public static void printInfo(String message) {
        System.out.println(BLUE + "ℹ " + message + RESET);
    }

    public static String getProgressBar(int percent) {
        int width = 20;
        int completed = (percent * width) / 100;
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < width; i++) {
            if (i < completed) sb.append("■");
            else sb.append(" ");
        }
        sb.append(String.format("] %3d%%", percent));
        return sb.toString();
    }

    public static void printUserTable(List<User> users) {
        System.out.println(BOLD + String.format("%-5s | %-20s | %-28s | %-16s | %-22s",
                "ID", "NAME", "EMAIL", "ROLE", "DEPARTMENT") + RESET);
        System.out.println("---------------------------------------------------------------------------------------------------");
        for (User u : users) {
            String roleColor = u.getRole().name().equals("ADMIN") ? RED :
                               u.getRole().name().equals("PROJECT_MANAGER") ? BLUE : GREEN;
            System.out.println(String.format("%-5d | %-20s | %-28s | %s%-16s%s | %-22s",
                    u.getId(), u.getName(), u.getEmail(), roleColor, u.getRole().getDisplayName(), RESET, u.getDepartment()));
        }
        System.out.println("---------------------------------------------------------------------------------------------------");
    }

    public static void printProjectTable(List<Project> projects) {
        System.out.println(BOLD + String.format("%-4s | %-30s | %-16s | %-12s | %-12s | %-28s",
                "ID", "PROJECT TITLE", "MANAGER", "BUDGET", "STATUS", "PROGRESS TIMELINE") + RESET);
        System.out.println("------------------------------------------------------------------------------------------------------------------");
        for (Project p : projects) {
            String statusColor = p.getStatus().equalsIgnoreCase("Completed") ? GREEN :
                                 p.getStatus().equalsIgnoreCase("In Progress") ? BLUE : YELLOW;
            System.out.println(String.format("%-4d | %-30s | %-16s | %-12s | %s%-12s%s | %s",
                    p.getId(),
                    p.getTitle().length() > 28 ? p.getTitle().substring(0, 26) + ".." : p.getTitle(),
                    p.getManagerName(),
                    p.getBudget(),
                    statusColor, p.getStatus(), RESET,
                    getProgressBar(p.getProgress())));
        }
        System.out.println("------------------------------------------------------------------------------------------------------------------");
    }

    public static void printTaskTable(List<Task> tasks) {
        System.out.println(BOLD + String.format("%-5s | %-32s | %-16s | %-8s | %-12s | %-12s | %-25s",
                "ID", "TASK TITLE", "ASSIGNEE", "PRIORITY", "STATUS", "DUE DATE", "PROGRESS") + RESET);
        System.out.println("-----------------------------------------------------------------------------------------------------------------------------");
        for (Task t : tasks) {
            String statusColor = t.getStatus().name().equals("COMPLETED") ? GREEN :
                                 t.getStatus().name().equals("IN_PROGRESS") ? BLUE : YELLOW;
            String prioColor = t.getPriority().name().equals("URGENT") ? RED :
                               t.getPriority().name().equals("HIGH") ? PURPLE : RESET;

            System.out.println(String.format("%-5d | %-32s | %-16s | %s%-8s%s | %s%-12s%s | %-12s | %s",
                    t.getId(),
                    t.getTitle().length() > 30 ? t.getTitle().substring(0, 28) + ".." : t.getTitle(),
                    t.getAssigneeName(),
                    prioColor, t.getPriority().getDisplayName(), RESET,
                    statusColor, t.getStatus().getDisplayName(), RESET,
                    t.getDueDate(),
                    getProgressBar(t.getProgress())));
        }
        System.out.println("-----------------------------------------------------------------------------------------------------------------------------");
    }
}
