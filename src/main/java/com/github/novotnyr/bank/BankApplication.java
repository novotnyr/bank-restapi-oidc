package com.github.novotnyr.bank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    public static void main(String[] args) {
        SpringApplication.run(BankApplication.class, args);
    }

}
