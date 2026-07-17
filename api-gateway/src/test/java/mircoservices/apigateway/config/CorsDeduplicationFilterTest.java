package mircoservices.apigateway.config;

import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CorsDeduplicationFilterTest {

    @Test
    void shouldPreferConcreteRequestOriginOverWildcardWhenDeduplicating() {
        CorsDeduplicationFilter filter = new CorsDeduplicationFilter();
        String origin = "https://studymatch-frontend-36417266010.asia-southeast1.run.app";

        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.post("https://api-gateway-36417266010.asia-southeast1.run.app/api/auth/google")
                        .header(HttpHeaders.ORIGIN, origin)
                        .build()
        );

        GatewayFilterChain chain = serverWebExchange -> {
            serverWebExchange.getResponse().getHeaders().addAll(
                    HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,
                    List.of("*", origin)
            );
            serverWebExchange.getResponse().getHeaders().addAll(
                    HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS,
                    List.of("true", "true")
            );
            return Mono.empty();
        };

        filter.filter(exchange, chain).block();

        assertEquals(
                origin,
                exchange.getResponse().getHeaders().getFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN)
        );
        assertEquals(
                List.of(origin),
                exchange.getResponse().getHeaders().get(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN)
        );
        assertEquals(
                List.of("true"),
                exchange.getResponse().getHeaders().get(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS)
        );
    }
}
