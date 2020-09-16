package com.example.ethereumserviceapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
          .antMatchers("/**").hasIpAddress("127.0.0.1") //temporary ip, must use ssi app ip instead... (calls must come from this ip "localhost" won't work)
          .anyRequest().authenticated()
          .and()
          .csrf().disable();
    }
    
}
