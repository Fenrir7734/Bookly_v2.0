package com.fenrir.masterdetail.security;

import com.fenrir.masterdetail.security.jwt.JwtAuthenticationEntryPoint;
import com.fenrir.masterdetail.security.jwt.JwtTokenFilter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@AllArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private UserDetailsServiceImpl userDetailsService;
    private JwtAuthenticationEntryPoint unauthorizedHandler;
    private JwtTokenFilter jwtTokenFilter;
    private static final String[] SWAGGER = {
            "/v2/api-docs",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",
            "/v3/api-docs/**",
            "/swagger-ui/**"
    };

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                    .cors()
                .and()
                    .csrf().disable()
                    .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                    .exceptionHandling()
                    .authenticationEntryPoint(unauthorizedHandler)
                .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                    .authorizeRequests()
                    .antMatchers(
                            "/api/auth/**").permitAll()
                .antMatchers(SWAGGER).permitAll()
                .antMatchers("/api/users/{username}/grant/**").access("hasRole('ADMIN')")
                .antMatchers(HttpMethod.GET, "/api/users/**", "/api/books/**", "/api/reviews/**").permitAll()
                .antMatchers(HttpMethod.DELETE, "/api/books/**").access("hasRole('ADMIN')")
                .antMatchers(HttpMethod.POST, "/api/books/**").authenticated()
                .antMatchers("/api/users/{username}/**", "/api/reviews/{username}/**").access("hasRole('ADMIN') or @userSecurity.isResourceOwner(authentication, #username)")
                .antMatchers("/api/users/**", "/api/books/**").access("hasRole('ADMIN')")
                .anyRequest().authenticated();
    }
}
