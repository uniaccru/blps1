package com.example.hhflow.security.jwt;

import com.example.hhflow.model.Role;
import com.example.hhflow.model.User;
import com.example.hhflow.security.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private static final String CLAIM_TYP = "typ";
    private static final String ACCESS = "ACCESS";
    private static final String REFRESH = "REFRESH";

    private static final String CLAIM_ROLE = "role";
    private static final String CLAIM_EMAIL = "email";

    private final JwtProperties jwtProperties;

    private Key signingKey;

    @PostConstruct
    void initKey() {
        String secret = jwtProperties.getSecret();
        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(secret);
        } catch (IllegalArgumentException ex) {
            keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        }
        signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createAccessToken(User account) {
        CustomUserDetails principal = CustomUserDetails.fromUser(account);
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtProperties.getExpiration().toMillis());

        return Jwts.builder()
                .setSubject(String.valueOf(principal.getUserId()))
                .setIssuedAt(now)
                .setExpiration(expiry)
                .claim(CLAIM_TYP, ACCESS)
                .claim(CLAIM_ROLE, principal.getRole().name())
                .claim(CLAIM_EMAIL, account.getEmail())
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /** Минимальный refresh: только id аккаунта и тип (без ролей в claims). */
    public String createRefreshToken(User account) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtProperties.getRefreshExpiration().toMillis());
        return Jwts.builder()
                .setSubject(String.valueOf(account.getId()))
                .setIssuedAt(now)
                .setExpiration(expiry)
                .claim(CLAIM_TYP, REFRESH)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public CustomUserDetails parsePrincipal(String accessToken) {
        Claims claims = parseClaims(accessToken);
        if (REFRESH.equals(claims.get(CLAIM_TYP, String.class))) {
            throw new JwtException("Refresh token cannot be used as access token");
        }
        Long userId = Long.parseLong(claims.getSubject());
        Role role = Role.valueOf(claims.get(CLAIM_ROLE, String.class));
        String email = claims.get(CLAIM_EMAIL, String.class);

        return CustomUserDetails.fromJwt(userId, email, role);
    }

    public boolean isValidAccessToken(String token) {
        try {
            Claims claims = parseClaims(token);
            String typ = claims.get(CLAIM_TYP, String.class);
            return typ == null || ACCESS.equals(typ);
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    public boolean isValidRefreshToken(String token) {
        try {
            Claims claims = parseClaims(token);
            return REFRESH.equals(claims.get(CLAIM_TYP, String.class));
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    public long parseRefreshAccountId(String refreshToken) {
        Claims claims = parseClaims(refreshToken);
        if (!REFRESH.equals(claims.get(CLAIM_TYP, String.class))) {
            throw new JwtException("Not a refresh token");
        }
        return Long.parseLong(claims.getSubject());
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}
