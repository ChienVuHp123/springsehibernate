package com.example.springsehibernate.Service;


import com.example.springsehibernate.Config.DropboxConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


@Service
public class DropboxService {

    private final RestTemplate restTemplate;
    private final DropboxConfig dropboxConfig;
    public DropboxService(RestTemplate restTemplate, DropboxConfig dropboxConfig) {
        this.restTemplate = restTemplate;
        this.dropboxConfig = dropboxConfig;
    }

    public String refreshAccessToken() {
        String tokenUrl = "https://api.dropboxapi.com/oauth2/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "refresh_token");
        map.add("refresh_token", dropboxConfig.getRefreshToken());
        map.add("client_id", dropboxConfig.getAppKey());
        map.add("client_secret", dropboxConfig.getAppSecret());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(tokenUrl, request, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                String newAccessToken = rootNode.path("access_token").asText();
                dropboxConfig.setAccessToken(newAccessToken); // Cập nhật access-token
                return newAccessToken;
            } else {
                // Xử lý phản hồi không thành công
            }
        } catch (Exception e) {
            // Xử lý ngoại lệ
        }
        return null; // Trường hợp lỗi hoặc không lấy được token
    }
}



