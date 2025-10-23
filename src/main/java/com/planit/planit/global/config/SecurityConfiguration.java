package com.planit.planit.global.config;

import com.google.firebase.auth.FirebaseAuth;
import com.planit.planit.domain.user.service.FirebaseAccountService;
import com.planit.planit.global.security.AuthenticationFilter;
import com.planit.planit.global.security.Json401EntryPoint;
import com.planit.planit.global.security.Json403AccessDeniedHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
@EnableMethodSecurity
public class SecurityConfiguration {

	private final ObjectProvider<FirebaseAccountService> accountServiceProvider;
	private final FirebaseAuth firebaseAuth;

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		var authenticationFilter = new AuthenticationFilter(accountServiceProvider, firebaseAuth);

		http
			.csrf(AbstractHttpConfigurer::disable)
			.cors(Customizer.withDefaults())
			.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.formLogin(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable)
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
				.requestMatchers("/", "/index.html", "/favicon.ico").permitAll()
				.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
				.requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
				.requestMatchers(HttpMethod.POST, "/api/v1/users").permitAll() // 회원가입 허용 용도, 추후 변경 가능
				.anyRequest().authenticated()
			)
			.exceptionHandling(ex -> ex
				.authenticationEntryPoint(new Json401EntryPoint())
				.accessDeniedHandler(new Json403AccessDeniedHandler())
			)
			.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}
