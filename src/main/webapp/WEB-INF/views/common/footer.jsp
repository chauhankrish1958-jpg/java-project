<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

        <!-- Bottom Footer -->
        <footer class="py-3 px-4 bg-white border-top text-muted small d-flex flex-column flex-md-row justify-content-between align-items-center">
            <div>
                &copy; <%= java.time.Year.now().getValue() %> <strong>ProTrack</strong>. Online Project Management System.
            </div>
            <div class="mt-2 mt-md-0">
                <span class="badge bg-light text-secondary border">Java 17</span>
                <span class="badge bg-light text-secondary border">Jakarta Servlets</span>
                <span class="badge bg-light text-secondary border">JDBC</span>
                <span class="badge bg-light text-secondary border">MySQL</span>
                <span class="badge bg-light text-secondary border">Tomcat 10</span>
            </div>
        </footer>
    </div><!-- /.main-wrapper -->
</div><!-- /.app-wrapper -->

<!-- Bootstrap 5 Bundle JS (Includes Popper) -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

<!-- ProTrack Main Application Script -->
<script src="<%= request.getContextPath() %>/assets/js/main.js"></script>
</body>
</html>
