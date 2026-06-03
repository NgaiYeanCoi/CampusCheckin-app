package cn.nyc1.campuscheckin.config;

import cn.nyc1.campuscheckin.common.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice(basePackages = "cn.nyc1.campuscheckin.controller")
public class ApiResponseLogAdvice implements ResponseBodyAdvice<Object> {
    private static final Logger log = LoggerFactory.getLogger(ApiResponseLogAdvice.class);

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response
    ) {
        if (!(body instanceof ApiResponse<?> apiResponse) || !isApiPath(request)) {
            return body;
        }

        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        long duration = durationMillis(attributes);
        int httpStatus = httpStatus(attributes);
        log.info(
                "API_CALL {} {} -> http={} code={} message={} duration={}ms",
                request.getMethod(),
                pathWithQuery(request),
                httpStatus,
                apiResponse.getCode(),
                apiResponse.getMessage(),
                duration
        );
        return body;
    }

    private boolean isApiPath(ServerHttpRequest request) {
        String path = request.getURI().getPath();
        return path != null && path.startsWith("/api/v1/");
    }

    private String pathWithQuery(ServerHttpRequest request) {
        String path = request.getURI().getPath();
        String query = request.getURI().getRawQuery();
        return query == null || query.isBlank() ? path : path + "?" + query;
    }

    private long durationMillis(ServletRequestAttributes attributes) {
        if (attributes == null) {
            return -1;
        }
        HttpServletRequest request = attributes.getRequest();
        Object start = request.getAttribute(ApiRequestTimingFilter.START_TIME_ATTRIBUTE);
        if (!(start instanceof Long startTime)) {
            return -1;
        }
        return System.currentTimeMillis() - startTime;
    }

    private int httpStatus(ServletRequestAttributes attributes) {
        if (attributes == null) {
            return 200;
        }
        HttpServletResponse response = attributes.getResponse();
        return response == null ? 200 : response.getStatus();
    }
}
