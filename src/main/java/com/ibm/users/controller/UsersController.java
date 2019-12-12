package com.ibm.users.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
// import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.ibm.users.constant.Const;
import com.ibm.users.entity.Userinfolist;
import com.ibm.users.model.UsersInfo;
import com.ibm.users.service.MailService;
import com.ibm.users.service.UsersService;
import com.ibm.users.utils.BeanUtilsCopy;
import com.ibm.users.utils.CommonResult;
import com.ibm.users.utils.ResponseBean;

import static org.springframework.http.HttpStatus.*;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@CrossOrigin
@RestController
//@RequestMapping("api/smc/users")
public class UsersController {

  private static Logger logger = LoggerFactory.getLogger(UsersController.class);

  @Value("${spring.mail.loginlink}")
  private String loginlink;
	
  @Autowired
  private UsersService usersService;
  
  // @Autowired
  // private PasswordEncoder passwordEncoder;
  
  @Autowired
  private MailService mailService;

  @PostMapping("/signup")
  public ResponseEntity<CommonResult> signup(@RequestBody UsersInfo usersInfo) throws Exception {
	Userinfolist users = new Userinfolist();
	usersInfo.setConfirmed("0");
	usersInfo.setUsertype("user");
    BeanUtilsCopy.copyPropertiesNoNull(usersInfo, users);
    
// a密码加密存储
//    String password = users.getPassword();
////  users.setPassword(passwordEncoder.encode(password));
//    users.setPassword(password);

    try {
    	usersService.saveUsersInfo(users);
    }catch (Exception e){
    	e.printStackTrace();
    	System.out.println("db error");
    	logger.error("db error >>> ", e.getMessage());
    	return ResponseEntity.ok().body(CommonResult.build(Const.COMMONRESULT_ERROR_CODE, "User sign up failed, please check your enters, user name may have been registered, you can try another one!"));
    }
    
    try {
	    // send email
	    mailService.sendHTMLMail(usersInfo.getEmail(), usersInfo.getUsername());
    }catch (Exception e){
    	e.printStackTrace();
    	System.out.println("发送html文本文件-发生异常");
    	logger.error("html email send failed!", e.getMessage());
		return ResponseEntity.ok().body(CommonResult.build(Const.COMMONRESULT_ERROR_CODE, "Email sent failed due to net issue! Pleasae re-signup later, Thanks."));
    }
    
    return ResponseEntity.ok().body(CommonResult.build(Const.COMMONRESULT_OK_CODE, "A confirmation email have sent to you, please go to your mailbox to confirm first!"));

  }

  @GetMapping("/confirmed/{username}")
  public String activeUserByUsername(@PathVariable("username") String username) throws Exception {
	  // user click this link from email received to confirmed user
	  if(usersService.setConfirmedByUsername(username, "1")>0) {
//		  return ResponseEntity.ok().body(CommonResult.build(Const.COMMONRESULT_OK_CODE, "User have confirmed!"));
		  System.out.println("发送html文本文件-发生成功");
		  logger.error("User have confirmed!");
		  return "<a href='"+ loginlink +"'>please click here to login SMC system</a>";
	  }
	  return "User confirm action failed!";
//	  return ResponseEntity.ok().body(CommonResult.build(Const.COMMONRESULT_ERROR_CODE, "User confirm action failed!"));
//	  return ResponseEntity.ok().body(new ResponseBean(OK.value(), OK.getReasonPhrase()).data("User have confirmed!"));
  }
  
  @PostMapping("/settings")
  public ResponseEntity<CommonResult> updateUsersInfo(@RequestBody UsersInfo usersInfo) throws Exception {
	  
	  String username = usersInfo.getUsername();
	  String oldpw = usersInfo.getPassword();
	  String newpw = usersInfo.getNewpassword();
	  if (StringUtils.isBlank(oldpw)) {
		  return ResponseEntity.ok().body(CommonResult.build(Const.COMMONRESULT_ERROR_CODE, "Please enter old password!"));
	  }
	  if (StringUtils.isBlank(newpw)) {
		  return ResponseEntity.ok().body(CommonResult.build(Const.COMMONRESULT_ERROR_CODE, "Please enter new password!"));
	  }

	  // validate old pw
	  Userinfolist oneuser = usersService.getUserByUsernameAndPassword(username, oldpw);
	  if (oneuser == null) {
	      return ResponseEntity.ok().body(CommonResult.build(Const.COMMONRESULT_ERROR_CODE, "Your old password is not correct !"));
	  }
	
	  // update pw
	  Userinfolist users = new Userinfolist();
	  BeanUtilsCopy.copyPropertiesNoNull(oneuser, users);
	  users.setPassword(newpw);

      try {
    	  usersService.saveUsersInfo(users);
      }catch (Exception e){
    	  e.printStackTrace();
    	  System.out.println("pw db error");
    	  logger.error("pw db error >>> ", e.getMessage());
  		  return ResponseEntity.ok().body(CommonResult.build(Const.COMMONRESULT_ERROR_CODE, "Password change failed, make sure you following the password change rules!"));
      }
	
	  // login, changepw, logout will update lastupdate column
	  if(!(usersService.setLastupdateByUsername(username, new Date())>0)) {
	    return ResponseEntity.ok().body(CommonResult.build(Const.COMMONRESULT_ERROR_CODE, "database error, please wait a moment and retry or contact with system admin!"));
	  }
	  
	  try {
		  // send email
		  String email = oneuser.getEmail();
		  mailService.sendNewPasswordEmail(email, newpw);
	  }catch (Exception e){
      	  e.printStackTrace();
          System.out.println("发送简单文本文件-发生异常"+e.getMessage());
	  }

      return ResponseEntity.ok().body(CommonResult.build(Const.COMMONRESULT_OK_CODE, "Password change successed, you can also find your new password in your mail box, please relogin with your new pasword!"));

  }

  @GetMapping("/logout/{username}")
  public ResponseEntity<CommonResult> logout(@PathVariable("username") String username) throws Exception {
    
    // login, changepw, logout will update lastupdate column
    if(usersService.setLastupdateByUsername(username, new Date())>0)
    	return ResponseEntity.ok().body(CommonResult.build(Const.COMMONRESULT_OK_CODE, "You have exited successfully!"));
	 
    return ResponseEntity.ok().body(CommonResult.build(Const.COMMONRESULT_ERROR_CODE, "Logout failed!"));
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
    return ResponseEntity.status(FORBIDDEN).body(new ResponseBean(FORBIDDEN.value(), FORBIDDEN.getReasonPhrase()).error(exception.getMessage()));
  }
	  
}
