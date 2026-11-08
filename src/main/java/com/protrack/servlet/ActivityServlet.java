package com.protrack.servlet;

import com.protrack.model.ActivityLog;
import com.protrack.service.ActivityService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Audit trail and system activity feed viewer.
 */
@WebServlet(name = "ActivityServlet", urlPatterns = {"/activity"})
public class ActivityServlet extends HttpServlet {

    private final ActivityService activityService = new ActivityService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<ActivityLog> activities = activityService.getRecentActivities(50);
        request.setAttribute("activities", activities);

        request.getRequestDispatcher("/WEB-INF/views/activity.jsp").forward(request, response);
    }
}
