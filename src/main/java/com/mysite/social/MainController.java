package com.mysite.social;

import java.security.Principal;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.mysite.social.oauth.CustomOAuth2User;
import com.mysite.social.user.UserService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class MainController {
	private final UserService userService;

	@GetMapping("/")
	public String root(Model model, Authentication authentication) {
		return "home";
	}

	@GetMapping("/board")
	public String boardMain(Model model, Authentication authentication) {
		return "redirect:/board/list";
	}

	@GetMapping("/calendar_home")
	public String calendarMain(Model model, Authentication authentication) {
		return "redirect:/calendar/home";
	}
	
	@GetMapping("/modal/{uri}")
	public String modal(@PathVariable("uri") String uri ,Model model, Authentication authentication) {
		return uri;
	}

}
