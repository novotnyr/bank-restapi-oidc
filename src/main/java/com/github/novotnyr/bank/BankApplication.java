package com.github.novotnyr.bank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@SpringBootApplication
@RestController
public class BankApplication {
    public static final Logger logger = LoggerFactory.getLogger(BankApplication.class);

    @GetMapping("/accounts/{accountId}/balance")
    public BigDecimal getBalance(@PathVariable String accountId, @AuthenticationPrincipal Jwt jwt) {
        String userId = (String) jwt.getClaims().getOrDefault("sub", "");
        logger.info("Retrieving bank account balance: account: {}, user {}", accountId, userId);
        return BigDecimal.TEN;
    }

    @PostMapping("/accounts/{accountId}/withdrawals")
    public BigDecimal withdrawTenCrowns(@PathVariable String accountId,
                                        @CurrentSecurityContext(expression = "authentication.name") String userName) {
        logger.info("Withdrawing 10 SKK: account: {}, user {}", accountId, userName);
        return BigDecimal.ZERO;
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        var authenticationConverter = new JwtAuthenticationConverter();
        authenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakAuthoritiesConverter());
        authenticationConverter.setPrincipalClaimName("preferred_username");
        return authenticationConverter;
    }

    public static void main(String[] args) {
        SpringApplication.run(BankApplication.class, args);
    }

}
