package com.mysite.social;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.mysite.social.oauth.CustomOAuth2UserService;

import groovyjarjarantlr4.v4.parse.ANTLRParser.finallyClause_return;

@Configuration
@EnableWebSecurity
@EnableAspectJAutoProxy
public class SecurityConfig {
	private final CustomOAuth2UserService customOAuth2UserService;
	private final CustomOAuth2AuthenticationFailureHandler customOAuth2AuthenticationFailureHandler;

	public SecurityConfig(CustomOAuth2UserService customOAuth2UserService,
			CustomOAuth2AuthenticationFailureHandler customOAuth2AuthenticationFailureHandler) {
		this.customOAuth2UserService = customOAuth2UserService;
		this.customOAuth2AuthenticationFailureHandler = customOAuth2AuthenticationFailureHandler;
	}

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeRequests(
				(authorizeRequests) -> authorizeRequests.requestMatchers(new AntPathRequestMatcher("/**")).permitAll())
				.csrf((csrf) -> csrf.ignoringRequestMatchers(new AntPathRequestMatcher("/h2-console/**")))
				.headers((headers) -> headers.addHeaderWriter(
						new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)))
//				.formLogin((formLogin) -> formLogin.loginPage("/user/login").defaultSuccessUrl("/"))
				.logout((logout) -> logout.logoutRequestMatcher(new AntPathRequestMatcher("/user/logout"))
						.logoutSuccessUrl("/").invalidateHttpSession(true))
				.oauth2Login(
						oauth2 -> oauth2.userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
								.failureHandler(customOAuth2AuthenticationFailureHandler));
		return http.build();
	}

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

}