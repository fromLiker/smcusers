package com.ibm.users.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ibm.users.filters.JwtAuthenticationTokenFilter;
import com.ibm.users.handler.SmcAccessDeniedHandler;
import com.ibm.users.handler.SmcAuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
public class SmcSecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private SmcAuthenticationEntryPoint smcAuthenticationEntryPoint;
  @Autowired
  private SmcAccessDeniedHandler smcAccessDeniedHandler;
  @Autowired
  private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Override
  protected void configure(HttpSecurity httpSecurity) throws Exception {
      httpSecurity.csrf().disable() // diable csrf
	    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // use JWT，don't create session
	    .and().exceptionHandling().accessDeniedHandler(smcAccessDeniedHandler).authenticationEntryPoint(smcAuthenticationEntryPoint) //
	    .and().authorizeRequests() // enable authorize HttpServletRequest
	    .antMatchers("/login").permitAll() // permit for login
	    .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()  // 给所有预请求方法放行
	    .antMatchers("/admin/**").hasRole("admin") // only allowed for role "admin" case-sensitive
//	    .antMatchers("api/smc/secure/user/**").hasAnyRole("admin", "user") // only allowed for roles "admin", "user" case-sensitive
	    .antMatchers("/signup").permitAll() // permit for sign up
	    .antMatchers("/confirmed/**").permitAll() // permit for confirm user
	    .anyRequest().authenticated() // need authorize for all the others
	    .and().addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class) // JWT based security filter
	    .headers().cacheControl(); // disable page caching
  }

}
