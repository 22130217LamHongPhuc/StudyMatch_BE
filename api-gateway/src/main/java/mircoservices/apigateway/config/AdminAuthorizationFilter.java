package mircoservices.apigateway.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class AdminAuthorizationFilter implements GlobalFilter, Ordered {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app.security.jwt.secret-key}")
    private String secretKey;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        boolean isAdminPath = "/api/admin".equals(path) || path.startsWith("/api/admin/");
        boolean isSuperAdminPath = "/api/super-admin".equals(path) || path.startsWith("/api/super-admin/");
        if (HttpMethod.OPTIONS.matches(exchange.getRequest().getMethod().name()) || (!isAdminPath && !isSuperAdminPath)) {
            return chain.filter(exchange);
        }

        String authorization = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return writeError(
                    exchange,
                    HttpStatus.UNAUTHORIZED,
                    "UNAUTHORIZED",
                    "Thiếu token xác thực"
            );
        }

        String token = authorization.substring(7);

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String role = claims.get("role", String.class);
            if (isSuperAdminPath) {
                if (!"super_admin".equalsIgnoreCase(role)) {
                    return writeError(
                            exchange,
                            HttpStatus.FORBIDDEN,
                            "ACCESS_DENIED",
                            "Bạn không có quyền truy cập tài nguyên quản trị cao cấp"
                    );
                }
            } else if (isAdminPath) {
                if (!"admin".equalsIgnoreCase(role) && !"super_admin".equalsIgnoreCase(role)) {
                    return writeError(
                            exchange,
                            HttpStatus.FORBIDDEN,
                            "ACCESS_DENIED",
                            "Bạn không có quyền truy cập tài nguyên quản trị"
                    );
                }
            }

            return chain.filter(exchange);
        } catch (JwtException | IllegalArgumentException ex) {
            return writeError(
                    exchange,
                    HttpStatus.UNAUTHORIZED,
                    "INVALID_TOKEN",
                    "Token không hợp lệ hoặc đã hết hạn"
            );
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 10;
    }

    private Key getSignKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    private Mono<Void> writeError(
            ServerWebExchange exchange,
            HttpStatus status,
            String code,
            String message
    ) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", false);
        body.put("code", code);
        body.put("message", message);
        body.put("data", null);

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(body);
            return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
        } catch (JsonProcessingException e) {
            byte[] bytes = "{\"success\":false,\"code\":\"INTERNAL_SERVER_ERROR\",\"message\":\"Không thể tạo phản hồi lỗi\",\"data\":null}"
                    .getBytes(StandardCharsets.UTF_8);
            return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
        }
    }
}
