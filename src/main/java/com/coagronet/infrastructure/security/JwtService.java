package com.coagronet.infrastructure.security;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	@Value("${jwt.secret}")
	private String secret;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private MyUserDetailsService myUserDetailsService;

	public String createJwtToken(String username, Long empresaId, Long rolId) {
		UserDetails userDetails = myUserDetailsService.loadUserByUsername(username);

		return jwtUtil.generateToken(userDetails.getUsername(), empresaId, rolId);
	}

	private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
		final Claims claims = extractAllClaims(token);
		return claimsResolvers.apply(claims);
	}

	private Claims extractAllClaims(String token) {
		return (Claims) Jwts.parser()
			.verifyWith((SecretKey) getSigningKey())
			.build()
			.parseSignedClaims(token)
			.getPayload();
	}

	private Key getSigningKey() {
		byte[] keyBytes = Decoders.BASE64.decode(secret);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public boolean validateToken(String token, String username) {
		return jwtUtil.validateToken(token, username);
	}

	public boolean isTokenValid(String token, UserDetails userDetails) {
		final String userName = extractUsername(token);
		return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
	}

	private boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	private Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

}