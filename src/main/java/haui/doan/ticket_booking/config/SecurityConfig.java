package haui.doan.ticket_booking.config;

import haui.doan.ticket_booking.filter.JwtAuthorizationFilter;
import haui.doan.ticket_booking.util.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Configuration
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    @Autowired
    private JwtAuthorizationFilter jwtAuthorizationFilter;

    public SecurityConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/users/add",
                    "/api/cities",
                    "/api/users/login",
                    "/api/otp",
                    "/api/otp/verify-otp",
                    "/api/images/**",
                    "/api/movies/detail/{movieId}",
                    "/payment/payos_transfer_handler",
                    "/api/users/email"
                ).permitAll()
                .requestMatchers("/api/movie/add", 
                                 "/api/movie/simple",
                                 "/api/showtime/add")
                                .hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Sử dụng BCrypt để mã hóa mật khẩu
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return authentication -> {
            throw new UnsupportedOperationException("No authentication manager is required for JWT.");
        };
    }
}