package com.protrack.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Cross-Site Request Forgery (CSRF) Protection Utility.
 * Manages cryptographically secure per-session CSRF tokens and request validation.
 */
public class CSRFTokenUtil {

    public static final String CSRF_PARAM_NAME = "csrfToken";
    public static final String CSRF_HEADER_NAME = "X-CSRF-TOKEN";
    public static final String SESSION_ATTR = "csrfToken";
    public static final String SESSION_ATTR_NAME = "csrfToken";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * Retrieves or initializes the CSRF token for the current HTTP session.
     */
    public static String getToken(HttpSession session) {
        if (session == null) return "";
        synchronized (session) {
            String token = (String) session.getAttribute(SESSION_ATTR_NAME);
            if (token == null || token.isEmpty()) {
                byte[] bytes = new byte[32];
                SECURE_RANDOM.nextBytes(bytes);
                token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
                session.setAttribute(SESSION_ATTR_NAME, token);
            }
            return token;
        }
    }

    /**
     * Validates whether the incoming HTTP request contains a valid CSRF token
     * matching the token stored in the user's session.
     */
    public static boolean isValid(HttpServletRequest request) {
        if (request == null) return false;
        HttpSession session = request.getSession(false);
        if (session == null) return false;

        String sessionToken = (String) session.getAttribute(SESSION_ATTR_NAME);
        if (sessionToken == null || sessionToken.isEmpty()) {
            return false;
        }

        String requestToken = request.getParameter(CSRF_PARAM_NAME);
        if (requestToken == null || requestToken.isEmpty()) {
            requestToken = request.getHeader(CSRF_HEADER_NAME);
        }

        if (requestToken == null || requestToken.isEmpty()) {
            return false;
        }

        return MessageDigest.isEqual(
                sessionToken.getBytes(StandardCharsets.UTF_8),
                requestToken.getBytes(StandardCharsets.UTF_8)
        );
    }
}
