package com.planit.planit.global.security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider tokenProvider;
	private final UserDetailsService userDetailsService;

	public JwtAuthenticationFilter(JwtTokenProvider tokenProvider, UserDetailsService uds) {
		this.tokenProvider = tokenProvider;
		this.userDetailsService = uds;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
		throws ServletException, IOException {

		String bearer = req.getHeader("Authorization");
		if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
			String token = bearer.substring(7);
			if (tokenProvider.validate(token)) {
				String username = tokenProvider.getUsername(token);
				UserDetails user = userDetailsService.loadUserByUsername(username);
				var auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
				SecurityContextHolder.getContext().setAuthentication(auth);
			}
		}
		chain.doFilter(req, res);
	}
}
