package com.pluse.config;

import java.security.PrivateKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.pluse.model.UserDetail;
import com.pluse.repository.UserRepository;

public class UserDetailsServiceImpl implements UserDetailsService{
	
	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		UserDetail user = userRepository.findByEmail(username);
		
		if (user == null) {
			throw new UsernameNotFoundException("user not found");
		}
		return new CustomUser(user);
	}

}
