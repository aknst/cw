package ru.mirea.cw.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SpringSecurity {
    @Autowired
    private UserDetailsService userDetailsService;
    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.headers(headers -> headers.frameOptions().disable())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorize) ->
                        authorize.requestMatchers("/register/**").anonymous()
                                .requestMatchers("/index", "/", "/error", "/error/**").permitAll()
                                .requestMatchers("/db-console/**").permitAll()
                                .requestMatchers("/shop/**").permitAll()
                                .requestMatchers("/resources/**", "/static/**", "/productImages", "/productImages/**", "/css/**").permitAll()
                                .requestMatchers("/admin/users").hasRole("ADMIN")
                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                .requestMatchers("/addToCart", "/profile", "/profile/**").hasAnyAuthority( "ROLE_USER", "ROLE_ADMIN")
                                .requestMatchers("/addToCart/**").hasAnyAuthority( "ROLE_USER", "ROLE_ADMIN")
                                .requestMatchers("/cart").hasAnyAuthority( "ROLE_USER", "ROLE_ADMIN")
                                .requestMatchers("/cart/**").hasAnyAuthority( "ROLE_USER", "ROLE_ADMIN")
                                .requestMatchers("/checkout").hasAnyAuthority( "ROLE_USER", "ROLE_ADMIN")
                ).formLogin(
                        form -> form
                                .loginPage("/login")
                                .loginProcessingUrl("/login")
                                .defaultSuccessUrl("/shop")
                                .permitAll()
                ).logout(
                        logout -> logout
                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                                .permitAll()
                ).exceptionHandling(
                        exceptionHandling -> exceptionHandling
                                .accessDeniedPage("/error/403")
                );
        return http.build();
    }
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {

        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }
}