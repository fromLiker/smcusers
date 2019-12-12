package com.ibm.users.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.ibm.users.constant.Const;
import com.ibm.users.filters.SmcUserDetailsService;
import com.ibm.users.model.AuthRequest;
import com.ibm.users.model.AuthResponse;
import com.ibm.users.service.UsersService;
import com.ibm.users.utils.CommonResult;
import com.ibm.users.utils.JwtTokenUtil;
import com.ibm.users.utils.ResponseBean;

import java.util.Date;
import java.util.Set;
import static org.springframework.http.HttpStatus.*;

@CrossOrigin
@RestController
//@RequestMapping(value = "api/smc/secure", produces = MediaType.APPLICATION_JSON_VALUE) // 可访问api/smc/secure/login
public class AuthController {

  @Autowired
  private SmcUserDetailsService smcuserDetailsService;
  @Autowired
  private AuthenticationManager authenticationManager;
  @Autowired
  private UsersService usersService;

  @PostMapping("/login")
  public ResponseEntity<CommonResult> login(@RequestBody AuthRequest request) throws Exception {

    Authentication authentication = authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);

    // Reload password post-security so we can generate token
    UserDetails userDetails = smcuserDetailsService.loadUserByUsername(request.getUsername());
    
    // login, changepw, logout will update lastupdate column
    if(!(usersService.setLastupdateByUsername(request.getUsername(), new Date())>0)) {
    	return ResponseEntity.ok().body(CommonResult.build(Const.COMMONRESULT_ERROR_CODE, "database error, please wait a moment and retry or contact with system admin!"));
    }

    String jwtToken = JwtTokenUtil.generateToken(userDetails, false);
    System.out.println("jwtToken >>>>"+jwtToken);
    
    AuthResponse authResponse = new AuthResponse();
    // authResponse.setUsername(request.getUsername());
    authResponse.setUsername(userDetails.getUsername());
    Set<GrantedAuthority> authorities = (Set<GrantedAuthority>) userDetails.getAuthorities();
    authResponse.setUsertype(authorities.toArray()[0].toString());
    authResponse.setJwtToken(jwtToken);
    
//    return ResponseEntity.ok().header("JWT-Token", jwtToken).body(new ResponseBean(OK.value(), OK.getReasonPhrase()).data(authResponse)); 
    return ResponseEntity.ok().body(CommonResult.build(Const.COMMONRESULT_OK_CODE, "Login successfully!", authResponse));
  }

  // use for test
  @RequestMapping(value = "/authenticated", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<ResponseBean> authenticated() throws Exception {
      return ResponseEntity.ok().body(new ResponseBean(OK.value(), OK.getReasonPhrase()).data("AUTHENTICATED - hasToken VERIFIED"));
  }
  
  // use for test
  @RequestMapping(value = "/admin", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<ResponseBean> isAdmin() throws Exception {
      return ResponseEntity.ok().body(new ResponseBean(OK.value(), OK.getReasonPhrase()).data("AUTHENTICATED - isAdmin VERIFIED"));
  }

  @ExceptionHandler(AuthenticationException.class)
  @ResponseStatus(UNAUTHORIZED)
  public ResponseEntity<ResponseBean> handleAuthentication401Exception(AuthenticationException exception) throws Exception {
    return ResponseEntity.status(UNAUTHORIZED)
    	.body(new ResponseBean(UNAUTHORIZED.value(), UNAUTHORIZED.getReasonPhrase()).error(exception.getMessage()));
  }

  @ExceptionHandler(AuthenticationException.class)
  @ResponseStatus(FORBIDDEN)
  public ResponseEntity<ResponseBean> handleAuthentication403Exception(AuthenticationException exception) throws Exception {
    return ResponseEntity.status(FORBIDDEN)
    	.body(new ResponseBean(FORBIDDEN.value(), FORBIDDEN.getReasonPhrase()).error(exception.getMessage()));
  }

}
