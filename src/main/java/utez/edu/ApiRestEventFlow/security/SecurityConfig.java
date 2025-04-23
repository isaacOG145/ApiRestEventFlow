package utez.edu.ApiRestEventFlow.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;

    @Autowired
    public SecurityConfig(JwtRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/activity/findActiveEvents").permitAll()
                        .requestMatchers("/profile").hasAnyAuthority("ROLE_SUPERADMIN", "ROLE_ADMIN", "ROLE_USER", "ROLE_CHECKER")
                        .requestMatchers("/user/findId/**").hasAnyAuthority("ROLE_SUPERADMIN", "ROLE_ADMIN", "ROLE_USER", "ROLE_CHECKER")
                        .requestMatchers("/user/updateUser").hasAnyAuthority("ROLE_SUPERADMIN", "ROLE_ADMIN", "ROLE_USER", "ROLE_CHECKER")
                        .requestMatchers("/user/updatePassword").hasAnyAuthority("ROLE_SUPERADMIN", "ROLE_ADMIN", "ROLE_USER", "ROLE_CHECKER")
                        .requestMatchers("/activity/saveEvent").hasAnyAuthority("ROLE_SUPERADMIN", "ROLE_ADMIN")
                        .requestMatchers("/activity/updateEvent").hasAnyAuthority("ROLE_SUPERADMIN", "ROLE_ADMIN")
                        .requestMatchers("/activity/saveWorkshop").hasAnyAuthority("ROLE_SUPERADMIN", "ROLE_ADMIN")
                        .requestMatchers("/activity/updateWorkshop").hasAnyAuthority("ROLE_SUPERADMIN", "ROLE_ADMIN")
                        .requestMatchers("/activity/assignment-status/owner/**").hasAnyAuthority("ROLE_SUPERADMIN", "ROLE_ADMIN")
                        .requestMatchers("/assignment/saveAssignment").hasAnyAuthority("ROLE_SUPERADMIN", "ROLE_ADMIN")
                        .requestMatchers("/assignment/update").hasAnyAuthority("ROLE_ADMIN", "ROLE_SUPERADMIN")
                        .requestMatchers("/assignment/change-status/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_SUPERADMIN")
                        .requestMatchers("/user/findByBoss/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_SUPERADMIN")
                        .requestMatchers("/user/findByBoss/**").hasAnyAuthority("ROLE_SUPERADMIN", "ROLE_ADMIN")
                        .requestMatchers("/user/saveChecker").hasAnyAuthority("ROLE_SUPERADMIN", "ROLE_ADMIN")
                        .requestMatchers("/user/updateChecker").hasAnyAuthority("ROLE_SUPERADMIN", "ROLE_ADMIN")
                        .requestMatchers("/user-activities/confirm").hasAnyAuthority("ROLE_CHECKER")
                        .requestMatchers("/user-activities/findByActivity/**").hasAnyAuthority("ROLE_SUPERADMIN", "ROLE_ADMIN","ROLE_CHECKER")


                        .anyRequest().permitAll()

                ).sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*")); // Permitir todos los orígenes mediante patrones
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("*")); // Permitir todos los encabezados
        configuration.setAllowCredentials(true); // Permitir credenciales
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}