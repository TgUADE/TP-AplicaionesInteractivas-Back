package com.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.entity.Role;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthFilter;
        private final AuthenticationProvider authenticationProvider;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(AbstractHttpConfigurer::disable)
                                .cors(AbstractHttpConfigurer::disable)
                                .authorizeHttpRequests(req -> req
                                .requestMatchers("/api/v1/auth/**").permitAll()
                                .requestMatchers("/error/**").permitAll()
                                .requestMatchers(HttpMethod.GET,"/products/**").permitAll()
                                .requestMatchers(HttpMethod.GET,"/categories/**").permitAll()
                                .requestMatchers(HttpMethod.PUT,"/products/**").hasAuthority(Role.ADMIN.name())
                                .requestMatchers(HttpMethod.DELETE,"/products/**").hasAuthority(Role.ADMIN.name())
                                .requestMatchers(HttpMethod.POST,"/products/**").hasAuthority(Role.ADMIN.name())
                                .requestMatchers(HttpMethod.PUT,"/categories/**").hasAuthority(Role.ADMIN.name())
                                .requestMatchers(HttpMethod.DELETE,"/categories/**").hasAuthority(Role.ADMIN.name())
                                .requestMatchers(HttpMethod.POST,"/categories/**").hasAuthority(Role.ADMIN.name())
                                .requestMatchers(HttpMethod.GET, "/promotions/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/promotions/**").hasAuthority(Role.ADMIN.name())
                                .requestMatchers(HttpMethod.PUT, "/promotions/**").hasAuthority(Role.ADMIN.name())
                                .requestMatchers(HttpMethod.DELETE, "/promotions/**").hasAuthority(Role.ADMIN.name())
                                .requestMatchers(HttpMethod.GET, "/carts/my-carts").permitAll()
                                .requestMatchers(HttpMethod.GET,"/carts/{cartId}").hasAuthority(Role.ADMIN.name())
                                .requestMatchers(HttpMethod.GET,"/carts/").authenticated()
                                .requestMatchers("/orders/user/**").hasAuthority(Role.ADMIN.name())
                                .requestMatchers(HttpMethod.PUT, "/orders/**").hasAuthority(Role.ADMIN.name())
                                .requestMatchers(HttpMethod.DELETE, "/orders/**").hasAuthority(Role.ADMIN.name())
                                .requestMatchers("/orders").hasAuthority(Role.ADMIN.name())
                                .requestMatchers("/orders/**").authenticated()
                                // Operaciones del usuario actual
                                .requestMatchers(HttpMethod.GET, "/api/users/me").authenticated()
                                .requestMatchers(HttpMethod.PUT, "/api/users/me").authenticated()
                                .requestMatchers(HttpMethod.DELETE, "/api/users/me").authenticated()
                                // Operaciones de admin sobre usuarios
                                .requestMatchers("/api/users/**").hasAuthority(Role.ADMIN.name())
                                
                                .requestMatchers(HttpMethod.GET, "/api/favorites/**").authenticated()
                                .requestMatchers(HttpMethod.POST, "/api/favorites/**").authenticated()
                                .requestMatchers(HttpMethod.DELETE, "/api/favorites/**").authenticated()
                                .anyRequest()
                                                .authenticated())
                                .exceptionHandling(exceptions -> exceptions
                                                .accessDeniedHandler(accessDeniedHandler()))
                                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                                .authenticationProvider(authenticationProvider)
                                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        @Bean
        public AccessDeniedHandler accessDeniedHandler() {
                return (HttpServletRequest request, HttpServletResponse response, 
                        org.springframework.security.access.AccessDeniedException accessDeniedException) -> {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.setContentType("application/json");
                        response.setCharacterEncoding("UTF-8");
                        
                        String jsonResponse = """
                                {
                                    "timestamp": "%s",
                                    "status": 403,
                                    "error": "Forbidden",
                                    "message": "No tienes permisos para realizar esta acción. Se requiere rol de administrador.",
                                    "path": "%s"
                                }
                                """.formatted(java.time.LocalDateTime.now(), request.getRequestURI());
                        
                        response.getWriter().write(jsonResponse);
                };
        }
}
