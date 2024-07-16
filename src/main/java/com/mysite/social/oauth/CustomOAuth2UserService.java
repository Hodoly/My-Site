package com.mysite.social.oauth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.mysite.social.DataNotFoundException;
import com.mysite.social.user.GoogleUserDetails;
import com.mysite.social.user.KakaoUserDetails;
import com.mysite.social.user.NaverUserDetails;
import com.mysite.social.user.OAuth2UserInfo;
import com.mysite.social.user.SiteUser;
import com.mysite.social.user.UserRepository;
import com.mysite.social.user.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
	private final UserRepository userRepository;
	private final UserService userService;
	
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oAuth2User = super.loadUser(userRequest);
		String registrationId = userRequest.getClientRegistration().getRegistrationId();
		OAuth2UserInfo oAuth2UserInfo = null;
		if(registrationId.equals("google")) {
			oAuth2UserInfo = new GoogleUserDetails(oAuth2User.getAttributes());
		}else if(registrationId.equals("naver")) {
			oAuth2UserInfo = new NaverUserDetails(oAuth2User.getAttributes());
		}else if(registrationId.equals("kakao")) {
			oAuth2UserInfo = new KakaoUserDetails(oAuth2User.getAttributes());
		}

		SiteUser user;
		if (getUserCheckOauth(oAuth2UserInfo.getEmail(),oAuth2UserInfo.getProvider())) {
			user = userService.getUser(oAuth2UserInfo.getProviderId());
		} else {
			user = userService.create(oAuth2UserInfo.getName(), oAuth2UserInfo.getProviderId(), oAuth2UserInfo.getEmail(), "", oAuth2UserInfo.getProvider());
		}
		
		List<GrantedAuthority> authorities = new ArrayList<>();
		if(oAuth2UserInfo.getEmail().equals("choihoyeon30@gmail.com")) {
			authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
		}else {
			authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
		}
		
		// CustomOAuth2User 객체 생성
		CustomOAuth2User customOAuth2User = new CustomOAuth2User(oAuth2User, oAuth2UserInfo.getProvider(), oAuth2UserInfo.getProviderId());
		
		// 인증 토큰 생성
	    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null, authorities);
	    SecurityContextHolder.getContext().setAuthentication(authenticationToken);

	    return customOAuth2User;
	}

	public boolean getUserCheckOauth(String email, String provider) {
		Optional<SiteUser> SiteUser = this.userRepository.findByEmailAndProvider(email, provider);
		if (SiteUser.isPresent()) {
			return true;
		} else {
			return false;
		}
	}

}
