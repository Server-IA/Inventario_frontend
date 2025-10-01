package com.coagronet.infrastructure.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.coagronet.user.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
public class JwtUtil {

	@Value("${jwt.secret}")
	private String secretKeyBase64;

	private SecretKey secretKey;

	@PostConstruct
	public void init() {
		byte[] keyBytes = java.util.Base64.getDecoder().decode(secretKeyBase64);
		secretKey = Keys.hmacShaKeyFor(keyBytes);
	}

	public String generateToken(User user, Long empresaId, Long rolId, Long estado) {
		return Jwts.builder()
			.subject(user.getUsername())
			.claim("empresaId", empresaId)
			.claim("rolId", rolId)
			.claim("tver", user.getTokenVersion())
			.claim("estado", estado)
			.issuedAt(new Date())
			.expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
			.signWith(secretKey)
			.compact();
	}

	public String generateToken(User user, Long rolId, Long estado) {
		return Jwts.builder()
			.subject(user.getUsername())
			.claim("rolId", rolId)
			.claim("tver", user.getTokenVersion())
			.claim("estado", estado)
			.issuedAt(new Date())
			.expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
			.signWith(secretKey)
			.compact();
	}

	public Claims extractAllClaims(String token) {
		return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
	}

	public String extractUsername(String token) {
		return extractAllClaims(token).getSubject();
	}

	public Integer extractTokenVersion(String token) {
		Object v = extractAllClaims(token).get("tver");
		return v == null ? null : ((Number) v).intValue();
	}

	public boolean isTokenExpired(String token) {
		return extractAllClaims(token).getExpiration().before(new Date());
	}

	public boolean validateToken(String token, String username) {
		final String extractedUsername = extractUsername(token);
		return (extractedUsername.equals(username) && !isTokenExpired(token));
	}

}
