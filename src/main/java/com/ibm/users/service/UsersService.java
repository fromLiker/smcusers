package com.ibm.users.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ibm.users.entity.Userinfolist;
import com.ibm.users.repository.UsersRepository;

@Service
public class UsersService {
	
	@Autowired
	private UsersRepository usersRepository;
	
	public Userinfolist getUserByUsername(String username) {
		return usersRepository.findByUsername(username);
	}
	
	// public Userinfolist getUserByEmail(String email) {
	//   return usersRepository.findByEmail(email);
	// }
	
	//    #########################################################################################
	
	public int setConfirmedByUsername(String username, String confirmed) {
		return usersRepository.saveUsersByUsernameAndConfirmed(username, "1");
	};
	
	public Userinfolist saveUsersInfo(Userinfolist users) {
		return usersRepository.save(users);
	};

	public int setLastupdateByUsername(String username, Date lastupdate) {
		return usersRepository.saveUsersByUsernameAndLastupdate(username, lastupdate);
	};

	public Userinfolist getUserByUsernameAndPassword(String username, String password) {
		return usersRepository.findByUsername(username);
	}
    
}