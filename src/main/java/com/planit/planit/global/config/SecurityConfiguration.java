package com.planit.planit.global.config;

import java.util.List;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import com.google.firebase.auth.FirebaseAuth;
import com.planit.planit.domain.user.service.FirebaseAccountService;
import com.planit.planit.global.security.AuthenticationFilter;
import com.planit.planit.global.security.Json401EntryPoint;
import com.planit.planit.global.security.Json403AccessDeniedHandler;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
@EnableMethodSecurity
public class SecurityConfiguration {

  private final ObjectProvider<FirebaseAccountService> accountServiceProvider;
  private final FirebaseAuth firebaseAuth;

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config
        .setAllowedOrigins(List.of("http://localhost:3000", "https://planit-tau-seven.vercel.app"));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(List.of("*"));
    config.setAllowCredentials(true);
    config.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    var authenticationFilter = new AuthenticationFilter(accountServiceProvider, firebaseAuth);

    http.csrf(AbstractHttpConfigurer::disable).cors(Customizer.withDefaults())
        .headers(headers -> headers.addHeaderWriter((request, response) -> {
          response.setHeader("Cross-Origin-Opener-Policy", "unsafe-none");
        })).sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .formLogin(AbstractHttpConfigurer::disable).httpBasic(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            .requestMatchers("/", "/index.html", "/favicon.ico").permitAll()
            .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
            .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/v1/auth/logout").permitAll()// logout api는 인증
                                                                                // 안해도 가능하게
            .requestMatchers("/api/**").authenticated() // API 요청은 인증 처리
            .anyRequest().permitAll() // 나머지 요청 허용
        )
        .exceptionHandling(ex -> ex.authenticationEntryPoint(new Json401EntryPoint())
            .accessDeniedHandler(new Json403AccessDeniedHandler()))
        .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }
}
