package org.example.config;

import org.example.model.Student;
import org.example.repository.StudentRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/etudiant/**").hasRole("STUDENT")
                        .requestMatchers("/", "/login", "/login/admin", "/login/etudiant", "/css/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .logout(logout -> logout.logoutSuccessUrl("/login?logout"))
                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
                .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(StudentRepository studentRepository) {
        return username -> {
            if ("admin".equalsIgnoreCase(username)) {
                return User.withUsername("admin")
                        .password("{noop}admin123")
                        .roles("ADMIN")
                        .build();
            }

            Student student = studentRepository.findByStudentId(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable"));

            UserDetails studentUser = User.withUsername(student.getStudentId())
                    .password("{noop}" + student.getStudentId())
                    .roles("STUDENT")
                    .build();
            return studentUser;
        };
    }
}
