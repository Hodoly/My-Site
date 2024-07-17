package com.mysite.social.calendar;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.mysite.social.user.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/calendar")
@RequiredArgsConstructor
@Controller
public class CalendarController {
	private final UserService userService;
	private final CalendarService calendarService;
	
	@GetMapping("/home")
	public String calendarHome(Model model, Authentication authentication) {
		if (authentication != null) {
			String name = userService.getUserName(authentication);
			model.addAttribute("name", name);
		}
		return "calendar_home";
	}
	
	@GetMapping("/get/google")
	public ResponseEntity<String> getGoogleCalendar(Authentication authentication){
		return calendarService.getGoogleCalendarList(authentication);
	}
}
