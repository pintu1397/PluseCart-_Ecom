package com.pluse.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pluse.model.UserDetail;
import com.pluse.repository.UserRepository;
import com.pluse.service.UserService;

@Service
public class UserServiceImpl implements UserService{

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public UserDetail saveUser(UserDetail user) {
		user.setRole("ROLE_USER");

		String encodePassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodePassword);
		UserDetail saveUser = userRepository.save(user);
		return saveUser;
	}

	@Override
	public UserDetail getUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	


}
