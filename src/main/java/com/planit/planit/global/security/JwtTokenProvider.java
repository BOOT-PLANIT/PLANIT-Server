package com.planit.planit.global.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

	private final Key key;
	private final long validityInMs;

	public JwtTokenProvider(
		@Value("${security.jwt.secret:change-this-secret-to-32bytes-min}") String secret,
		@Value("${security.jwt.validity-ms:3600000}") long validityInMs // 1h
	) {
		this.key = Keys.hmacShaKeyFor(secret.getBytes());
		this.validityInMs = validityInMs;
	}

	public String createToken(String username) {
		Date now = new Date();
		Date exp = new Date(now.getTime() + validityInMs);

		return Jwts.builder()
			.setSubject(username)
			.setIssuedAt(now)
			.setExpiration(exp)
			.signWith(key)
			.compact();
	}

	public String getUsername(String token) {
		return Jwts.parser().verifyWith((SecretKey) key).build()
			.parseSignedClaims(token)
			.getPayload()
			.getSubject();
	}

	public boolean validate(String token) {
		try {
			Jwts.parser().verifyWith((SecretKey) key).build().parseSignedClaims(token);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			return false;
		}
	}
}
