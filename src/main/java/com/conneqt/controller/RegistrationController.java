package com.conneqt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.conneqt.DTO.UserRegisteredDTO;
import com.conneqt.service.DefaultUserService;
import com.conneqt.service.EmailSenderService;

@Controller
@RequestMapping("/registration")
public class RegistrationController {

	 private DefaultUserService userService;

	 
	 @Autowired
	 private EmailSenderService email1;
	 
	    public RegistrationController(DefaultUserService userService) {
	        super();
	        this.userService = userService;
	    }

	    @ModelAttribute("user")
	    public UserRegisteredDTO userRegistrationDto() {
	        return new UserRegisteredDTO();
	    }

	    @GetMapping
	    public String showRegistrationForm() {
	        return "register";
	    }

	    @PostMapping
	    public String registerUserAccount(@ModelAttribute("user") 
	              UserRegisteredDTO registrationDto) {
	    	String name = registrationDto.getName();
	    	System.out.println("user Name: "+name);
	    	String password = registrationDto.getPassword();
	    	System.out.println("user password: "+password);
	    	String email = registrationDto.getEmail_id();
	    	System.out.println("user Email: "+email);
	    	String role = registrationDto.getRole();
	    	System.out.println("user role: "+role);
	    	
	    	String Subject ="Id Creation In IVR";
	    	
	    	String message = "Hi "+name+",\n\n"
	    		    + "Your User Name and password has Been Create Successfully.\n\n"
	    		    + "User Name: "+email+"\n"
	    		    + "Password: "+password+"\n\n\n\n\n"
	    		    + "Thanks & Regard\n"
	    		    + "Conneqt IVR Admin Team\n\n\n\n\n"
	    		    + "Don't reply to this email, it's a system-generated mail.";
	    	
	    	email1.sendSimpleEmail(email, Subject, message);
	    	
	        userService.save(registrationDto);
	        return "redirect:/login";
	    }
}