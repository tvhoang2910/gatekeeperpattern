package com.example.gatekeeperpattern.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class SqlInjectionGatekeeperFilter implements Filter {

    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
            ".*('|\"|;|=|--|\\b(OR|AND|UNION|SELECT|INSERT|DELETE|UPDATE|DROP|CREATE|ALTER)\\b).*",
            Pattern.CASE_INSENSITIVE
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        CachedBodyHttpServletRequest wrappedRequest = new CachedBodyHttpServletRequest(httpRequest);

        for (String[] paramValues : wrappedRequest.getParameterMap().values()) {
            for (String value : paramValues) {
                if (containsSqlInjection(value)) {
                    sendErrorResponse(httpResponse, "Potential SQL Injection detected in query parameter.");
                    return;
                }
            }
        }

        String method = wrappedRequest.getMethod();
        if (HttpMethod.POST.name().equalsIgnoreCase(method) ||
                HttpMethod.PUT.name().equalsIgnoreCase(method) ||
                HttpMethod.PATCH.name().equalsIgnoreCase(method)) {
            String body = StreamUtils.copyToString(wrappedRequest.getInputStream(), StandardCharsets.UTF_8);
            if (containsSqlInjection(body)) {
                sendErrorResponse(httpResponse, "Potential SQL Injection detected in request body.");
                return;
            }
        }

        chain.doFilter(wrappedRequest, response);
    }

    private boolean containsSqlInjection(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        return SQL_INJECTION_PATTERN.matcher(value).matches();
    }

    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter().write(String.format("{\"error\": \"Forbidden\", \"message\": \"%s\"}", message));
    }
}