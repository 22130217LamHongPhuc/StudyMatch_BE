package mircoservices.apigateway.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.Key;

@Component
public class JwtHeaderFilter implements GlobalFilter, Ordered {

    @Value("${app.security.jwt.secret-key}")
    private String secretKey;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String authorization = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);
            try {
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(getSignKey())
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                Object userIdObj = claims.get("userId");
                if (userIdObj != null) {
                    String userId = userIdObj.toString();
                    ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                            .header("X-User-Id", userId)
                            .build();
                    return chain.filter(exchange.mutate().request(mutatedRequest).build());
                }
            } catch (JwtException | IllegalArgumentException ex) {
                System.err.println("Failed to parse token in gateway: " + ex.getMessage());
            }
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 5;
    }

    private Key getSignKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }
}
