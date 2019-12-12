package com.ibm.users.handler;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.http.HttpStatus.FORBIDDEN;

@Component
public class SmcAccessDeniedHandler implements AccessDeniedHandler {
	
	private static Logger logger = LoggerFactory.getLogger(SmcAccessDeniedHandler.class);
	  
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
	
		logger.debug("AccessDeniedHandler: FOUND 403 Forbidden");
		response.sendError(FORBIDDEN.value(), FORBIDDEN.getReasonPhrase());
	    
	}

}
