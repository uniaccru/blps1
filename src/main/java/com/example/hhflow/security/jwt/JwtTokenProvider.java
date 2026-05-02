package com.example.hhflow.security.jwt;

import com.example.hhflow.model.Role;
import com.example.hhflow.model.UserAccount;
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
    private static final String CLAIM_PHONE = "phone";
    private static final String CLAIM_EMPLOYER_ID = "employerId";
    private static final String CLAIM_APPLICANT_ID = "applicantId";

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

    public String createAccessToken(UserAccount account) {
        CustomUserDetails principal = CustomUserDetails.fromUserAccount(account);
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtProperties.getExpiration().toMillis());

        io.jsonwebtoken.JwtBuilder builder = Jwts.builder()
                .setSubject(String.valueOf(principal.getAccountId()))
                .setIssuedAt(now)
                .setExpiration(expiry)
                .claim(CLAIM_TYP, ACCESS)
                .claim(CLAIM_ROLE, principal.getRole().name())
                .claim(CLAIM_PHONE, account.getPhone());

        if (principal.getEmployerId() != null) {
            builder.claim(CLAIM_EMPLOYER_ID, principal.getEmployerId());
        }
        if (principal.getApplicantId() != null) {
            builder.claim(CLAIM_APPLICANT_ID, principal.getApplicantId());
        }

        return builder.signWith(signingKey, SignatureAlgorithm.HS256).compact();
    }

    /** Минимальный refresh: только id аккаунта и тип (без ролей в claims). */
    public String createRefreshToken(UserAccount account) {
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
        Long accountId = Long.parseLong(claims.getSubject());
        Role role = Role.valueOf(claims.get(CLAIM_ROLE, String.class));
        String phone = claims.get(CLAIM_PHONE, String.class);
        Long employerId = claims.get(CLAIM_EMPLOYER_ID, Long.class);
        Long applicantId = claims.get(CLAIM_APPLICANT_ID, Long.class);

        return CustomUserDetails.fromJwt(accountId, phone, role, employerId, applicantId);
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
