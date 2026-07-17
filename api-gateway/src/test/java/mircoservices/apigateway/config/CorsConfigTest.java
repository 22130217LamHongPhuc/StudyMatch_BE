package mircoservices.apigateway.config;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.cors.reactive.CorsWebFilter;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class CorsConfigTest {

    @Test
    void shouldEchoRequestOriginForWildcardPatternPreflightRequests() {
        CorsConfig config = new CorsConfig();
        ReflectionTestUtils.setField(config, "allowedOrigins", "*");

        CorsWebFilter filter = config.corsWebFilter();
        AtomicBoolean chainCalled = new AtomicBoolean(false);
        String origin = "https://studymatch-frontend-36417266010.asia-southeast1.run.app";

        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.options("https://api-gateway-36417266010.asia-southeast1.run.app/api/auth/google")
                        .header(HttpHeaders.ORIGIN, origin)
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "content-type")
                        .build()
        );

        filter.filter(exchange, webExchange -> {
            chainCalled.set(true);
            return Mono.empty();
        }).block();

        assertFalse(chainCalled.get());
        assertEquals(origin, exchange.getResponse().getHeaders().getFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
        assertEquals("true", exchange.getResponse().getHeaders().getFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS));
    }

    @Test
    void shouldTrimConfiguredOriginPatternsBeforeMatching() {
        CorsConfig config = new CorsConfig();
        String origin = "https://studymatch-frontend-36417266010.asia-southeast1.run.app";
        ReflectionTestUtils.setField(config, "allowedOrigins", "http://localhost:3000, " + origin);

        CorsWebFilter filter = config.corsWebFilter();
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.options("https://api-gateway-36417266010.asia-southeast1.run.app/api/auth/google")
                        .header(HttpHeaders.ORIGIN, origin)
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "content-type")
                        .build()
        );

        filter.filter(exchange, webExchange -> Mono.empty()).block();

        assertEquals(origin, exchange.getResponse().getHeaders().getFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
    }
}
