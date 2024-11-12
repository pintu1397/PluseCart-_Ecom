package com.pluse.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pluse.model.UserDetail;

public interface UserRepository extends JpaRepository<UserDetail, Integer>{

	public UserDetail findByEmail(String username);

}
