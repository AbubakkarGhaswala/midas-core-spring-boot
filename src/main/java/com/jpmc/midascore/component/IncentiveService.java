package com.jpmc.midascore.component;

import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Component
public class IncentiveService {
    private static final Logger logger = LoggerFactory.getLogger(IncentiveService.class);

    private final RestTemplate restTemplate;
    private final String incentiveApiUrl;

    public IncentiveService(RestTemplateBuilder builder,
                            @Value("${incentive.api.url:http://localhost:33433/incentive}") String incentiveApiUrl) {
        this.restTemplate = builder
                .setConnectTimeout(Duration.ofMillis(1000))
                .setReadTimeout(Duration.ofMillis(1000))
                .build();
        this.incentiveApiUrl = incentiveApiUrl;
    }

    public Incentive getIncentive(Transaction transaction) {
        try {
            Incentive incentive = restTemplate.postForObject(incentiveApiUrl, transaction, Incentive.class);
            return incentive != null ? incentive : new Incentive(0f);
        } catch (Exception e) {
            logger.debug("Incentive API at {} returned error or is unreachable: {}", incentiveApiUrl, e.getMessage());
            return new Incentive(0f);
        }
    }
}
