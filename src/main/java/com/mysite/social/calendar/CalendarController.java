package com.mysite.social.calendar;

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

	@GetMapping("/home")
	public String calendarHome(Model model) {
		String nickname = userService.getNickName();
		model.addAttribute("nickname", nickname);
		return "calendar_home";
	}
	
}
