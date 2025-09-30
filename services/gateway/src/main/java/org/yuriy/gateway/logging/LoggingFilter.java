package org.yuriy.gateway.logging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@Component
@Slf4j
public class LoggingFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long start = System.currentTimeMillis();
        return chain.filter(exchange)
                .doFinally(signalType -> {
                    long duration = System.currentTimeMillis() - start;

                    log.info("Request: {} {} -> status {} ({} ms) [signal: {}]",
                            exchange.getRequest().getMethod(),
                            exchange.getRequest().getURI(),
                            exchange.getResponse().getStatusCode(),
                            duration,
                            signalType.name());
                });
    }
}

