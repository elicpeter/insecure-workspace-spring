package com.example.insecurecollab.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class StartupWarning {

    private static final Logger log = LoggerFactory.getLogger(StartupWarning.class);

    @PostConstruct
    public void warn() {
        log.warn("============================================================");
        log.warn("INTENTIONALLY INSECURE APPLICATION FOR LOCAL TESTING ONLY");
        log.warn("AI-GENERATED DEMO. DO NOT DEPLOY OR EXPOSE TO REAL USERS.");
        log.warn("============================================================");
    }
}
