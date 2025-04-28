package com.mpp.disaster.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.web.filter.OncePerRequestFilter;

public class SpaWebFilter extends OncePerRequestFilter {

    /**
     * Forwards any unmapped paths (except those containing a period or starting with specific prefixes)
     * to the client {@code index.html}.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        // Extract the request path by removing the context path
        String path = request.getRequestURI().substring(request.getContextPath().length());

        // Exclude specific paths from being forwarded to index.html
        if (
            path.startsWith("/api") || // Exclude API endpoints
            path.startsWith("/management") || // Exclude management endpoints
            path.startsWith("/v3/api-docs") || // Exclude API documentation
            path.startsWith("/login") || // Exclude login endpoints
            path.startsWith("/oauth2") || // Exclude OAuth2 endpoints
            path.contains(".") // Exclude static resources (e.g., .js, .css, .png)
        ) {
            filterChain.doFilter(request, response);
            return;
        }

        // Forward all other requests to index.html
        if (path.matches("/(.*)")) {
            request.getRequestDispatcher("/index.html").forward(request, response);
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
