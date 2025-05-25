package com.supplify.securityconfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.io.IOException;
import java.util.Collection;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean

    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/admin/**").permitAll()
                        .requestMatchers("/login", "/buyer", "/buyer/save", "/buyer/update/{id}","/seller/update/{id}","/sellerEdit", "/buyerEdit/{id}", "/register", "/register/save","/edit_profile","/seller/edit","/seller/update",
                                "/dashboard", "/ServiceProvider", "/serviceProvider/save","/sellerProfile","/ViewProfile","/addProduct","/editProduct","/listProduct","/css/signUpForm.css","/static/css/**","/messages/**","/templates/**","/seller_profile","/seller_docs","/update_sellerProfile","/chathello","/chat/sendMessage","/sellerDocumentImages/**","/checkout/**","/admin/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .successHandler(authenticationSuccessHandler())
                        .failureUrl("/login?error=true") // Redirect with error
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .permitAll()
                );

        return http.build();

    }


    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (HttpServletRequest request, HttpServletResponse response, Authentication authentication) -> {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            String redirectUrl = "/buyerdashboard"; // Default redirect URL

            // Redirect based on role
            for (GrantedAuthority authority : authorities) {
                System.out.println("Authority: " + authority.getAuthority());
                if (authority.getAuthority().equals("BUYER")) {
                    redirectUrl = "/buyerdashboard"; // Redirect to buyer dashboard
                    break;
                } else if (authority.getAuthority().equals("SELLER")) {
                    redirectUrl = "/sellerdashboard"; // Redirect to seller dashboard
                    break;
                }
                else if(authority.getAuthority().equals("ADMIN")) {
                    redirectUrl = "/admin/pending-sellers";
                    break;
                }
            }

            response.sendRedirect(redirectUrl);
        };
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }
}
