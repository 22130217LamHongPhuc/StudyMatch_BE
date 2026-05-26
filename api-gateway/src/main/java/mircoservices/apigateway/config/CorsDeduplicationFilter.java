package mircoservices.apigateway.config;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class CorsDeduplicationFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            HttpHeaders headers = exchange.getResponse().getHeaders();
            
            if (headers.containsKey(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN)) {
                List<String> origins = headers.get(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
                if (origins != null && origins.size() > 1) {
                    headers.put(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, List.of(origins.get(0)));
                }
            }

            if (headers.containsKey(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS)) {
                List<String> credentials = headers.get(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS);
                if (credentials != null && credentials.size() > 1) {
                    headers.put(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, List.of(credentials.get(0)));
                }
            }
        }));
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
