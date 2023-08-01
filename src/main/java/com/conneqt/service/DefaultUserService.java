package com.conneqt.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.conneqt.DTO.UserRegisteredDTO;
import com.conneqt.model.User;




public interface DefaultUserService extends UserDetailsService{

	User save(UserRegisteredDTO userRegisteredDTO);
	
    List<User> fetchUserAccounts();


    User getUserById(Integer id);

    boolean saveOrUpdateAnime(User user);
	
	
	
}