package com.bank.gatewayserver.filtersUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Order(1)
@Component
public class RequestTraceFilter implements GlobalFilter {


    @Autowired
    FilterUtility filterUtility;
    private static Logger logger = LoggerFactory.getLogger(RequestTraceFilter.class);
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        HttpHeaders requestHeaders=exchange.getRequest().getHeaders();

        if(isCorrelationIdPresent(requestHeaders))
        {
            logger.debug("Public bank Correlation id found:{}",filterUtility.getCorrelationId(requestHeaders));
        }
        else
        {
            String correlationid=generateCorrelationId();
            exchange=filterUtility.setCorrelationId(exchange,correlationid);

            logger.debug("Public Bank correlationId :{}",correlationid);
        }
        return chain.filter(exchange);

    }

    public boolean isCorrelationIdPresent(HttpHeaders requestHeaders)
    {
        if(filterUtility.getCorrelationId(requestHeaders)!=null)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private String generateCorrelationId() {
        return java.util.UUID.randomUUID().toString();
    }
}
