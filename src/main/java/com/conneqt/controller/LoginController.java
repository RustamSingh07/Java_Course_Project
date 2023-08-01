package com.conneqt.controller;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.conneqt.DTO.UserLoginDTO;
import com.conneqt.config.CustomSuccessHandler;
import com.conneqt.model.User;
import com.conneqt.repository.UserRepository;
import com.conneqt.service.DefaultUserService;
import com.conneqt.service.EmailSenderService;

@Controller
@RequestMapping("/login")
public class LoginController {
	@Autowired
	private DefaultUserService userService;

	@Autowired
	UserRepository userRepo;
	
	 @Autowired
	 private EmailSenderService javaMailSender;
	
	@ModelAttribute("user")
	public UserLoginDTO userLoginDTO() {
		return new UserLoginDTO();
	}

	@GetMapping
	public String login() {
		return "login";
	}


	public void loginUser(@ModelAttribute("user") UserLoginDTO userLoginDTO) {
		String username = userLoginDTO.getUsername();

		System.out.println("User name " + username);
		userService.loadUserByUsername(username);
	}

	@GetMapping("/otpVerification")
	public String otpSent(Model model, UserLoginDTO userLoginDTO) {
		String user= returnUsername();
		System.out.println("User subEmail: "+user);
        model.addAttribute("userDetails", user);
	
		model.addAttribute("otpValue", userLoginDTO);
		
	
		return "otpScreen";

	}
	private String returnUsername() {
		SecurityContext securityContext = SecurityContextHolder.getContext();
        UserDetails user = (UserDetails) securityContext.getAuthentication().getPrincipal();
		User users = userRepo.findByEmail(user.getUsername());
		String email = users.getEmail();
		
		  String maskedEmail = maskEmail(email);
	        System.out.println("Masked email: "+maskedEmail);
		return maskedEmail;
	}
	

	@PostMapping("/otpVerification")
	public String otpVerification(@ModelAttribute("otpValue") UserLoginDTO userLoginDTO,Authentication authentication,HttpServletRequest request,HttpServletResponse response) throws IOException {
		SecurityContext securityContext = SecurityContextHolder.getContext();
		UserDetails user = (UserDetails) securityContext.getAuthentication().getPrincipal();
		User users = userRepo.findByEmail(user.getUsername());
		if (users.getOtp() == userLoginDTO.getOtp()) {
			users.setActive(true);
			userRepo.save(users);
			
			String redirectUrl = null;

			Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

			for (GrantedAuthority grantedAuthority : authorities) {
				if (grantedAuthority.getAuthority().equals("USER")) {
					redirectUrl = "redirect:/dashboard";
					break;
				} else if (grantedAuthority.getAuthority().equals("ADMIN")) {
					redirectUrl = "redirect:/adminScreen";
				}
				
			}
			if (redirectUrl == null) {
				throw new IllegalStateException();
			}
			 
			System.out.println("Redirect: "+redirectUrl);
			
		
			return 	redirectUrl;
					
		} else
			return "redirect:/login/otpVerification?error";
	}

	
	
	
	
	
	
	public String generateOtp(User user) {
		try {
			
		
			   int randomPIN = (int) (Math.random() * 900000) + 100000;
			
			
			user.setOtp(randomPIN);
			userRepo.save(user);
			
			String Name = user.getName();
			
			System.out.println("user Name: "+Name);
			
			String email = user.getEmail();
			System.out.println("Email OTP: "+email);
			
			String Subject ="IVR Verification OTP";
			
			System.out.println("Your Otp Is: "+randomPIN);

	    	String message = "Hi "+Name+",\n\n"
	    		    + "Your Login OTP :" + randomPIN 
	    		    + "\n .Please Verify. \n\n"	    		  
	    		    + "Thanks & Regard\n"
	    		    + "Conneqt IVR Admin Team\n\n\n\n\n"
	    		    + "Don't reply to this email, it's a system-generated mail.";
			
			
	    	javaMailSender.sendSimpleEmail(email, Subject, message);
			
			return "success";
			}catch (Exception e) {
				e.printStackTrace();
				return "error";
			}
	}
	 private String maskEmail(String email) {
	        int atIndex = email.indexOf('@');
	        if (atIndex > 3) {
	            String maskedPart = new String(new char[atIndex - 3]).replace('\0', '*');
	            return email.substring(0, 3) + maskedPart + email.substring(atIndex);
	        } else {
	            return email; // The email is too short to mask
	        }
	    }
	
}
