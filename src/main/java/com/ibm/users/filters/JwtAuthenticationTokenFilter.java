package com.ibm.users.filters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ibm.users.entity.Userinfolist;
import com.ibm.users.service.UsersService;
import com.ibm.users.utils.JwtTokenUtil;

import io.jsonwebtoken.Claims;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

  private static Logger logger = LoggerFactory.getLogger(JwtAuthenticationTokenFilter.class);
  
  @Autowired
  private UserDetailsService userDetailsService;
  @Autowired
  UsersService usersService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String authToken = request.getHeader(JwtTokenUtil.TOKEN_HEADER);
    if (authToken != null && authToken.startsWith(JwtTokenUtil.TOKEN_PREFIX)) {
      authToken = authToken.substring(JwtTokenUtil.TOKEN_PREFIX.length());
      if (authToken.equals("null")) {
	      filterChain.doFilter(request, response);
	      return;
	    }
      logger.debug("JwtAuthenticationTokenFilter - authTokenHeader = {}", authToken);
    } else {
      authToken = request.getParameter("JWT-Tonken");
      logger.debug("JwtAuthenticationTokenFilter - authTokenParams = {}" + authToken);

      if (authToken == null) {
        filterChain.doFilter(request, response);
        return;
      }
    }

    try {
      String username = JwtTokenUtil.getUsername(authToken); // if token invalid, will get exception here
      
      // add start
      Claims claims = JwtTokenUtil.getTokenBody(authToken);
      if(claims == null ){
      	filterChain.doFilter(request, response);
      	return;
      }else{
    	  Userinfolist users = usersService.getUserByUsername(username);
          if(JwtTokenUtil.isTokenExpired(claims.getExpiration(), users.getLastupdate(), claims.getIssuedAt())){
          	filterChain.doFilter(request, response); 
          	return;
         }
      }
      // add end

      if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        logger.debug("JwtAuthenticationTokenFilter: checking authentication for user = {}", username);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (JwtTokenUtil.validateToken(authToken, userDetails)) {
          UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), "N/A",
                                                                                                       userDetails.getAuthorities());
          authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authentication);
        }
      }
    } catch (Exception e) {
     logger.debug("JwtAuthenticationTokenFilter:Exception");
     logger.error(e.getMessage(), e);
    }

    filterChain.doFilter(request, response);
  }

}
