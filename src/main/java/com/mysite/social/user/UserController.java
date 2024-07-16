package com.mysite.social.user;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {
	private final UserService userService;

//	@Value("${spring.security.oauth2.client.registration.google.client-id}")
//	private String clientId;
//
//	@Value("${oauth2.google.redirect-uri}")
//	private String redirectUri;

	@GetMapping("/login")
	public String login() {
		return "login_form";
	}

//	@GetMapping("/google/login")
//	public String googleLogin() {
//		String _uri = "https://accounts.google.com/o/oauth2/auth?client_id=" + clientId + "&redirect_uri=" + redirectUri
//				+ "&response_type=code&scope=https://www.googleapis.com/auth/userinfo.email%20https://www.googleapis.com/auth/userinfo.profile";
//		return "redirect:" + _uri;
//	}
}
