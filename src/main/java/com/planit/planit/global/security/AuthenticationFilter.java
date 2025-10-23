package com.planit.planit.global.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.planit.planit.domain.user.service.FirebaseAccountService;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {

	private final ObjectProvider<FirebaseAccountService> accountServiceProvider;
	private final FirebaseAuth firebaseAuth;

	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
		throws ServletException, IOException {

		String tokenStr = resolveBearer(req.getHeader("Authorization"));

		if (tokenStr != null) {
			try {
				FirebaseToken decoded = firebaseAuth.verifyIdToken(tokenStr);

				FirebaseAccountService accountService = accountServiceProvider.getObject();
				UserDetails user = accountService.ensureAndLoad(decoded);

				var auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
				SecurityContextHolder.getContext().setAuthentication(auth);

			} catch (com.google.firebase.auth.FirebaseAuthException e) {
				String msg = String.format(
					"[AUTH] verifyIdToken failed code=%s msg=%s cause=%s",
					e.getAuthErrorCode(),
					e.getMessage(),
					(e.getCause() != null ? e.getCause().getMessage() : "n/a")
				);
				logger.warn(msg, e);
				SecurityContextHolder.clearContext();
			} catch (Exception e) {
				logger.warn("[AUTH] verifyIdToken failed (non-Firebase): " + e, e);
				SecurityContextHolder.clearContext();
			}
		}
		chain.doFilter(req, res);
	}

	private String resolveBearer(String header) {
		if (!StringUtils.hasText(header)) return null;
		if (!header.startsWith("Bearer ")) return null;
		return header.substring(7).trim();
	}
}
