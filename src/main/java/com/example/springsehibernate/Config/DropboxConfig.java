package com.example.springsehibernate.Config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class DropboxConfig {

    @Value("${dropbox.app.key}")
    private String appKey;

    @Value("${dropbox.app.secret}")
    private String appSecret;

    @Value("${dropbox.access.token}")
    private String accessToken;

    private String refreshToken;
}
