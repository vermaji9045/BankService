package com.bank.gatewayserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import reactor.core.publisher.Mono;

import java.time.Duration;

@SpringBootApplication
public class GatewayserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayserverApplication.class, args);
	}


	@Bean
	public RouteLocator publicBankRouteConfig(RouteLocatorBuilder routeLocatorBuilder)
	{

return routeLocatorBuilder.routes()
		.route(p-> p
				 .path("/publicbank/accounts/**")
				.filters(f->f.rewritePath("/publicbank/accounts/(?<segment>.*)","/${segment}")
						.circuitBreaker(config -> config.setName("accountsCircuitBreaker").setFallbackUri("forward:/supportContact")))
				.uri("lb://ACCOUNTS"))

		.route(p-> p
				.path("/publicbank/cards/**")
				.filters(f->f.rewritePath("/publicbank/cards/(?<segment>.*)","/${segment}")
						.retry(retryConfig -> retryConfig.setRetries(3)
								.setMethods(HttpMethod.GET)
								.setBackoff(Duration.ofMillis(100),Duration.ofMillis(1000),2,true)))
				.uri("lb://CARDS"))
		.route(p-> p
				.path("/publicbank/loans/**")
				.filters(f->f.rewritePath("/publicbank/loans/(?<segment>.*)","/${segment}")
						.requestRateLimiter(config -> config.setRateLimiter(redisRateLimiter())
								.setKeyResolver(userKeyResolver())))
				.uri("lb://LOANS")).build();
	}


	@Bean
	public RedisRateLimiter redisRateLimiter()
	{
		return new RedisRateLimiter(1,1,1);
	}

	@Bean
	KeyResolver userKeyResolver()
	{
		return exchange -> Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst("user"))
				.defaultIfEmpty("anonymous");
	}


}
