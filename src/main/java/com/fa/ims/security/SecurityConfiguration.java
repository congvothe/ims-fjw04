package com.fa.ims.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfiguration {

    private final AuthenticationSuccessHandler authenticationSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
        .and()
            .formLogin()
            .loginPage("/login")
            .loginProcessingUrl("/authentication")
            .successHandler(authenticationSuccessHandler)
            .permitAll()
        .and()
            .authorizeHttpRequests()
            .antMatchers("/login").permitAll()
            .antMatchers("/css/**").permitAll()
            .antMatchers("/image/**").permitAll()
//            .antMatchers("/user-management").hasRole(RoleUtil.ADMIN)
//            .antMatchers("/view-job", "/create-job", "/update-job/**").hasAnyRole(RoleUtil.MANAGER, RoleUtil.RECRUITER)
//            .antMatchers("/view-candidate").hasAnyRole(RoleUtil.MANAGER, RoleUtil.RECRUITER, RoleUtil.INTERVIEWER)
//            .antMatchers("/view-interview").hasAnyRole(RoleUtil.MANAGER, RoleUtil.RECRUITER, RoleUtil.INTERVIEWER)
//            .antMatchers("/create-candidate", "/update-candidate/**").hasAnyRole(RoleUtil.MANAGER, RoleUtil.RECRUITER)
//            .antMatchers("/result-interview/**").hasAnyRole(RoleUtil.MANAGER, RoleUtil.RECRUITER, RoleUtil.INTERVIEWER)
//            .antMatchers("/create-interview", "/update-interview/**").hasAnyRole(RoleUtil.MANAGER, RoleUtil.RECRUITER)
//            .antMatchers("/result-offer", "/select-offer-candidate", "/create-offer/**", "/view-offer").hasAnyRole(RoleUtil.MANAGER, RoleUtil.RECRUITER)
//            .antMatchers("/update-offer/**").hasRole(RoleUtil.RECRUITER)
//            .antMatchers("/delete-candidate/**", "/delete-interview/**").hasAnyRole(RoleUtil.MANAGER, RoleUtil.RECRUITER)
//            .anyRequest()
//            .authenticated()
        .and()
            .logout()
            .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
            .deleteCookies("JSESSIONID")
            .invalidateHttpSession(true)
        .and()
            .rememberMe()
            .key("uniqueAndSecret");
        return http.build();
    }
}
