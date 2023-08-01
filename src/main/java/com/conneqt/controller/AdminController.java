package com.conneqt.controller;

import java.util.List;

import com.conneqt.service.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.conneqt.DTO.UserRegisteredDTO;
import com.conneqt.model.User;
import com.conneqt.repository.UserRepository;
import com.conneqt.service.DefaultUserService;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/adminScreen")
public class AdminController {

	@Autowired
	UserRepository userRepository;

	@Autowired
	DefaultUserService userService;

	@GetMapping
	public String displayDashboard(Model model) {
		String user = returnUsername();
		System.out.println("NAME: " + user);
		model.addAttribute("userDetails", user);
		return "adminScreen";
	}

	private String returnUsername() {
		SecurityContext securityContext = SecurityContextHolder.getContext();
		UserDetails user = (UserDetails) securityContext.getAuthentication().getPrincipal();
		User users = userRepository.findByEmail(user.getUsername());
		return users.getName();
	}

	@GetMapping("/viewUser")
	public String displayAlluser(Model model) {
		List<User> users = userService.fetchUserAccounts();

		// Add the list of users to the Thymeleaf model
		model.addAttribute("users", users);

		return "viewUser";
	}

	@DeleteMapping("/delete/{id}")
	public String deleteUser(@PathVariable("id") int id) {

		return null;
	}


	@GetMapping("/editUser/{id}")
	public String getUserById(@PathVariable Integer id, Model model) {
		model.addAttribute("user", userService.getUserById(id));
		return "editUser";
	}

	@PostMapping("/editUser")
	public String editSaveUser(User user, RedirectAttributes redirectAttributes) {
		try {
			if (userService.saveOrUpdateAnime(user)) {
				redirectAttributes.addFlashAttribute("message", "Edit Success");
				return "redirect:/viewUser";
			}
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("message", "Something want wrong!!");
			return "redirect:/viewUser";
		}

		redirectAttributes.addFlashAttribute("message", "Something want wrong!!");
		return "redirect:/viewUser";
	}
}
