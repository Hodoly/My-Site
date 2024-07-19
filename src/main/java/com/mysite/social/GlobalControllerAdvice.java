package com.mysite.social;

import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.mysite.social.user.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@ControllerAdvice
public class GlobalControllerAdvice {
	private final UserService userService;
	
    @ModelAttribute
    public void addAttributes(Model model, Authentication authentication) {
		if (authentication != null) {
			String name = userService.getUserName(authentication);
			model.addAttribute("name", name);
		}
    }
}