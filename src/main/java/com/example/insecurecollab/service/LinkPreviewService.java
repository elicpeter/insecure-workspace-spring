package com.example.insecurecollab.service;

import com.example.insecurecollab.util.InsecureHttpClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class LinkPreviewService {

    private final InsecureHttpClient insecureHttpClient;

    public LinkPreviewService(InsecureHttpClient insecureHttpClient) {
        this.insecureHttpClient = insecureHttpClient;
    }

    public String fetch(String url) {
        RestTemplate restTemplate = insecureHttpClient.build();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        String body = response.getBody() == null ? "" : response.getBody();
        return body.substring(0, Math.min(body.length(), 500));
    }
}
