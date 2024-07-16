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
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public SiteUser create(String nickname, String username, String email, String password, String provider) {
		SiteUser user = new SiteUser();
		user.setNickname(nickname);
		user.setUsername(username);
		user.setEmail(email);
		user.setPassword(password);
		user.setProvider(provider);
		this.userRepository.save(user);
		return user;
	}

	public SiteUser getUser(String username) {
		Optional<SiteUser> siteUser = this.userRepository.findByUsername(username);
		if (siteUser.isPresent()) {
			return siteUser.get();
		} else {
			throw new DataNotFoundException("siteuser not found");
		}
	}

	public String getNickName() {
		String nickname = "";
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getPrincipal() instanceof CustomOAuth2User) {
			CustomOAuth2User customUser = (CustomOAuth2User) authentication.getPrincipal();

			SiteUser user = getUser(customUser.getProviderId());
			nickname = user.getNickname();
		}
		return nickname;
	}
	
	public String getUserName() {
		String username = "";
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getPrincipal() instanceof CustomOAuth2User) {
			CustomOAuth2User customUser = (CustomOAuth2User) authentication.getPrincipal();

			SiteUser user = getUser(customUser.getProviderId());
			username = user.getUsername();
		}
		return username;
	}
}
