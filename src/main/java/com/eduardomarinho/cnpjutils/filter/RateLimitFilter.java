package com.eduardomarinho.cnpjutils.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.eduardomarinho.cnpjutils.properties.RateLimitProperties;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitProperties rateLimitProperties;
    private final ConcurrentHashMap<String, RequestInfo> requestCounts = new ConcurrentHashMap<>();

    @Autowired
    public RateLimitFilter(RateLimitProperties rateLimitProperties) {
        this.rateLimitProperties = rateLimitProperties;
    }

    private static class RequestInfo {
        private final AtomicInteger count = new AtomicInteger(0);
        private LocalDateTime windowStart;

        public RequestInfo() {
            this.windowStart = LocalDateTime.now();
        }

        public AtomicInteger getCount() {
            return count;
        }

        public LocalDateTime getWindowStart() {
            return windowStart;
        }

        public void resetWindow() {
            this.windowStart = LocalDateTime.now();
            this.count.set(0);
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Se o rate limiting estiver desabilitado, apenas continua
        if (!rateLimitProperties.isEnabled()) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientIp = getClientIp(request);
        RequestInfo requestInfo = requestCounts.computeIfAbsent(clientIp, k -> new RequestInfo());
        
        LocalDateTime now = LocalDateTime.now();
        
        // Verifica se a janela de tempo expirou
        if (ChronoUnit.MINUTES.between(requestInfo.getWindowStart(), now) >= rateLimitProperties.getMinutes()) {
            requestInfo.resetWindow();
        }
        
        // Verifica se excedeu o limite
        if (requestInfo.getCount().get() >= rateLimitProperties.getRequests()) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("""
                {
                    "message": "Limite de requisições excedido. Tente novamente em alguns minutos.",
                    "success": false,
                    "error": "RATE_LIMIT_EXCEEDED"
                }
                """);
            response.setContentType("application/json");
            return;
        }
        
        // Incrementa o contador e continua
        requestInfo.getCount().incrementAndGet();
        filterChain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/swagger-ui") || 
               path.startsWith("/v3/api-docs") || 
               path.startsWith("/actuator");
    }
}
