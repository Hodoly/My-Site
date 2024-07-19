package com.mysite.social.user;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mysite.social.DataNotFoundException;
import com.mysite.social.oauth.CustomOAuth2User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {
	private final PasswordEncoder passwordEncoder;

	public String getProviderId(Authentication authentication) {
		Object principal = authentication.getPrincipal();
		String providerid = "";
		if (principal instanceof CustomOAuth2User) {
			CustomOAuth2User customOAuth2User = (CustomOAuth2User) principal;
			providerid = customOAuth2User.getProviderId();
		}
		return providerid;
	}
	public String getUserName(Authentication authentication) {
		Object principal = authentication.getPrincipal();
		String name = "";
		if (principal instanceof CustomOAuth2User) {
			CustomOAuth2User customOAuth2User = (CustomOAuth2User) principal;
			name = customOAuth2User.getName();
		}
		return name;
	}
	public String getProvider(Authentication authentication) {
		Object principal = authentication.getPrincipal();
		String provider = "";
		if (principal instanceof CustomOAuth2User) {
			CustomOAuth2User customOAuth2User = (CustomOAuth2User) principal;
			provider = customOAuth2User.getProvider();
		}
		return provider;
	}
}
