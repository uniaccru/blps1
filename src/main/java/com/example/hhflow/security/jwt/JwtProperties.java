package com.example.hhflow.security.jwt;

import java.time.Duration;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "jwt")
@Validated
public class JwtProperties {

    /**
     * HS256 secret key, Base64-encoded (задаётся только в конфиге, см. {@code jwt.secret}).
     */
    @NotBlank
    private String secret;

    /** Срок жизни access JWT — только из конфига, например {@code 15m}. */
    @NotNull
    private Duration expiration;

    /** Срок жизни refresh JWT — только из конфига, например {@code 7d}. */
    @NotNull
    private Duration refreshExpiration;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Duration getExpiration() {
        return expiration;
    }

    public void setExpiration(Duration expiration) {
        this.expiration = expiration;
    }

    public Duration getRefreshExpiration() {
        return refreshExpiration;
    }

    public void setRefreshExpiration(Duration refreshExpiration) {
        this.refreshExpiration = refreshExpiration;
    }
}
