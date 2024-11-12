package com.pluse.service;

import com.pluse.model.UserDetail;

public interface UserService {
	public UserDetail saveUser(UserDetail user);

	public UserDetail getUserByEmail(String email);
}



