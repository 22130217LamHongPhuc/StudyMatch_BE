package mircoservices.apigateway.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdminAuthorizationFilterTest {

    private static final String SECRET_KEY = "nlu_echo_by_phuc_lam_2004_this_is_be_for_applicati";

    @Test
    void shouldAllowAdminRequestWhenTokenContainsAdminRole() {
        AdminAuthorizationFilter filter = createFilter();
        AtomicBoolean chainCalled = new AtomicBoolean(false);

        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/admin/users")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateToken("admin"))
                        .build()
        );

        GatewayFilterChain chain = serverWebExchange -> {
            chainCalled.set(true);
            return Mono.empty();
        };

        filter.filter(exchange, chain).block();

        assertTrue(chainCalled.get());
    }

    @Test
    void shouldRejectAdminRequestWhenAuthorizationHeaderIsMissing() {
        AdminAuthorizationFilter filter = createFilter();
        AtomicBoolean chainCalled = new AtomicBoolean(false);

        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/admin/users").build()
        );

        GatewayFilterChain chain = serverWebExchange -> {
            chainCalled.set(true);
            return Mono.empty();
        };

        filter.filter(exchange, chain).block();

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
        assertEquals(false, chainCalled.get());
    }

    @Test
    void shouldRejectAdminRequestWhenRoleIsNotAdmin() {
        AdminAuthorizationFilter filter = createFilter();
        AtomicBoolean chainCalled = new AtomicBoolean(false);

        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/admin/users")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateToken("student"))
                        .build()
        );

        GatewayFilterChain chain = serverWebExchange -> {
            chainCalled.set(true);
            return Mono.empty();
        };

        filter.filter(exchange, chain).block();

        assertEquals(HttpStatus.FORBIDDEN, exchange.getResponse().getStatusCode());
        assertEquals(false, chainCalled.get());
    }

    @Test
    void shouldIgnoreNonAdminRoutes() {
        AdminAuthorizationFilter filter = createFilter();
        AtomicBoolean chainCalled = new AtomicBoolean(false);

        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/groups").build()
        );

        GatewayFilterChain chain = serverWebExchange -> {
            chainCalled.set(true);
            return Mono.empty();
        };

        filter.filter(exchange, chain).block();

        assertTrue(chainCalled.get());
    }

    private AdminAuthorizationFilter createFilter() {
        AdminAuthorizationFilter filter = new AdminAuthorizationFilter();
        ReflectionTestUtils.setField(filter, "secretKey", SECRET_KEY);
        return filter;
    }

    private String generateToken(String role) {
        Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .claim("role", role)
                .claim("userId", 1L)
                .setSubject("admin@studymatch.com")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 60000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
