package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class CatApiServiceImpl implements CatApiService {
    private final RestTemplate restTemplate;

    @Override
    public boolean checkApiConnection(int httpStatusPathVariable) {
        String catUrl = "https://http.cat/" + httpStatusPathVariable;
        ResponseEntity<String> response = restTemplate.exchange(catUrl, HttpMethod.GET, null, String.class);

        return response.getStatusCode().equals(HttpStatus.OK);
    }
}
