package de.greenblood.tsbot.restservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

@Configuration
@EnableWebSecurity
public class SpringBootSecurityManager extends WebSecurityConfigurerAdapter {
    private static final String DEFAULT_ADMIN_USER = "admin";
    private static final CharSequence DEFAULT_ADMIN_PASSWORD = "admin";

    @Bean
    public UserDetailsService userDetailsService() {

        return new MyUserDetailsService();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/favicon.ico", "/static-css/**", "/locales/**", "/img/**", "/fonts/**", "/client.js", "/vendor.js", "/index.html", "/", "/odysseus/api/login").permitAll()
                .anyRequest().authenticated()
                .and()
                .csrf().disable()
                .logout().invalidateHttpSession(true)
                .and()
                .formLogin()
                .successHandler(new AuthenticationSuccessHandler())
                .failureHandler(new SimpleUrlAuthenticationFailureHandler())
                .and()
                .logout()
                .logoutSuccessHandler(new SPALogoutSuccessHandler())
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(new Http403ForbiddenEntryPoint());


    }

    @Override
    protected void configure(AuthenticationManagerBuilder builder) throws Exception {
        builder.userDetailsService(userDetailsService())
                .passwordEncoder(new BCryptPasswordEncoder());
    }
}