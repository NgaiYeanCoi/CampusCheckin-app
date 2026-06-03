package cn.nyc1.campuscheckin.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ApiRequestTimingFilter extends OncePerRequestFilter {
    public static final String START_TIME_ATTRIBUTE = ApiRequestTimingFilter.class.getName() + ".startTime";
    private static final Logger log = LoggerFactory.getLogger(ApiRequestTimingFilter.class);

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        if (!isApiRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        request.setAttribute(START_TIME_ATTRIBUTE, System.currentTimeMillis());
        try {
            filterChain.doFilter(request, response);
        } catch (ServletException | IOException | RuntimeException ex) {
            log.error("API_EXCEPTION {} {} -> {}", request.getMethod(), pathWithQuery(request), ex.getMessage(), ex);
            throw ex;
        }
    }

    private boolean isApiRequest(HttpServletRequest request) {
        return request.getRequestURI() != null && request.getRequestURI().startsWith("/api/v1/");
    }

    private String pathWithQuery(HttpServletRequest request) {
        String path = request.getRequestURI();
        String query = request.getQueryString();
        return query == null || query.isBlank() ? path : path + "?" + query;
    }
}
