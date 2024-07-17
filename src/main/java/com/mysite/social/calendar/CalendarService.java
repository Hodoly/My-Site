package com.mysite.social.calendar;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

@Service
public class CalendarService {
	private final RestTemplate restTemplate;
	private final OAuth2AuthorizedClientService authorizedClientService;

	public CalendarService(OAuth2AuthorizedClientService authorizedClientService) {
		this.restTemplate = new RestTemplate();
		this.authorizedClientService = authorizedClientService;
	}

	public ResponseEntity<String> getGoogleCalendarList(Authentication authentication) {
		OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient("google",
				authentication.getName());
		OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
		String url = "https://www.googleapis.com/calendar/v3/users/me/calendarList";
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(accessToken.getTokenValue());
		HttpEntity<String> entity = new HttpEntity<>(headers);
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
		return response;
	}

	public ResponseEntity<String> getNaverCalendarList(Authentication authentication) {
		OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient("naver",
				authentication.getName());
		OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
        String url = "https://apis.naver.com/calendar/v3/calendars";
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(accessToken.getTokenValue());
		HttpEntity<String> entity = new HttpEntity<>(headers);
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
		System.out.println("response>>>>>" + response);
		return response;
	}

	public ResponseEntity<String> getKakaoCalendarList(Authentication authentication) {
        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient("kakao", authentication.getName());
        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
        String url = "https://dapi.kakao.com/v2/calendar/calendarList"; // 가상의 엔드포인트
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken.getTokenValue());
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        return response;
	}
}
