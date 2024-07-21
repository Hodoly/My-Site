package com.mysite.social.calendar;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import com.mysite.social.reservation.ReservationEvent;
import com.mysite.social.reservation.Reservation;
import com.mysite.social.reservation.ReservationForm;
import com.mysite.social.resource.Resource;
import com.mysite.social.user.UserService;

import jakarta.validation.Valid;
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
	public String calendarHome(Authentication authentication, Model model) {
		String provider = "";
		String providerid = "";
		if (authentication != null) {
			provider = userService.getProvider(authentication);
			providerid = userService.getProviderId(authentication);
		}
		model.addAttribute("provider", provider);
		model.addAttribute("providerid", providerid);
		return "calendar_home";
	}

	@GetMapping("/check")
	public String calendarChk(Authentication authentication, Model model) {
		model.addAttribute("providerid", userService.getProviderId(authentication));
		return "calendar_check";
	}

	@GetMapping("/get/google")
	public ResponseEntity<String> getGoogleCalendar(Authentication authentication) {
		return calendarService.getGoogleCalendarList(authentication);
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/create")
	public String createCalendar(CalendarForm calendarForm, Authentication authentication, Model model) {
		model.addAttribute("calendar", new Calendar());
		model.addAttribute("layout", "layout");
		return "calendar_form";
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/create")
	public String createReservation(Model model, @Valid CalendarForm calendarForm, BindingResult bindingResult,
			Authentication authentication) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("layout", "layout");
			return "calendar_form";
		}

		String name = userService.getUserName(authentication);
		String providerid = userService.getProviderId(authentication);
		String subject = calendarForm.getSubject();
		String color = calendarForm.getColor();
		Boolean allday = null;
		if (calendarForm.getAllday() != null) {
			allday = calendarForm.getAllday();
		} else {
			allday = false;
		}
		LocalDateTime startDateTime = calendarService.setStartDateTime(calendarForm, allday);
		LocalDateTime endDateTime = calendarService.setEndDateTime(calendarForm, allday);

		if (calendarService.validation(startDateTime, endDateTime)) {
			model.addAttribute("errorMessage", "시작 시간이 종료 시간보다 늦거나 같을 수 없습니다.");
			model.addAttribute("layout", "layout");
			return "calendar_form";
		}

		calendarService.create(startDateTime, endDateTime, providerid, name, subject, allday, color);
		return "redirect:/calendar/home";
	}

	@GetMapping("/get/schedule")
	@ResponseBody
	public List<CalendarEvent> getSchedule(@RequestParam(required = false) String id) {
		List<Calendar> calendars = calendarService.getCalendar(id);
		return calendars.stream().map(calendar -> {
			String start;
			String end;

			if (calendar.isAllday()) {
				// 종일 이벤트인 경우 LocalDate로 변환
				start = calendar.getStartDateTime().toLocalDate().toString();
				end = calendar.getEndDateTime().toLocalDate().toString();
			} else {
				// 시간 기반 이벤트인 경우 LocalDateTime 그대로 사용
				start = calendar.getStartDateTime().toString();
				end = calendar.getEndDateTime().toString();
			}
			String role = "mysite";
			return new CalendarEvent(start, end, calendar.getSubject(), calendar.getColor(), calendar.isAllday(),
					calendar.getId(), role);
		}).collect(Collectors.toList());
	}

	@GetMapping(value = "/detail/{id}")
	public String detail(Model model, @PathVariable("id") Integer id, Authentication authentication) {
		Calendar calendar = this.calendarService.getCalendar(id);
		String start;
		String end;
		if(calendar.isAllday()) {
			start = calendarService.setAlldayStartDateTime(calendar.getStartDateTime());
			end = calendarService.setAlldayEndDateTime(calendar.getEndDateTime());
		}else {
			start = calendarService.setDetailStartDateTime(calendar.getStartDateTime());
			end = calendarService.setDetailEndDateTime(calendar.getEndDateTime());
		}
		if (authentication != null) {
			String providerid = userService.getProviderId(authentication);
			String provider = userService.getProvider(authentication);
			model.addAttribute("providerid", providerid);
			model.addAttribute("provider", provider);
		}
		model.addAttribute("start", start);
		model.addAttribute("end", end);
		model.addAttribute("calendar", calendar);
		
		return "calendar_detail";
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/delete/{id}")
	@ResponseBody
	public ResponseEntity<String> calendarDelete(Model model, @PathVariable("id") Integer id,
			Authentication authentication) {
		Calendar calendar = this.calendarService.getCalendar(id);
		String providerid = userService.getProviderId(authentication);
		if (!calendar.getAuthor().equals(providerid)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
		}
		this.calendarService.delete(calendar);
		return ResponseEntity.ok("삭제 성공");
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/modify/{id}")
	public String calendarModify(Model model, CalendarForm calendarForm, @PathVariable("id") Integer id,
			Authentication authentication) {
		Calendar calendar = this.calendarService.getCalendar(id);

		String providerid = userService.getProviderId(authentication);

		if (!calendar.getAuthor().equals(providerid)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
		}

		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

		calendarForm.setSubject(calendar.getSubject());
		calendarForm.setStartDate(calendar.getStartDateTime().format(dateFormatter));
		calendarForm.setEndDate(calendar.getEndDateTime().format(dateFormatter));
		calendarForm.setStartTime(calendar.getStartDateTime().format(timeFormatter));
		calendarForm.setEndTime(calendar.getEndDateTime().format(timeFormatter));

		model.addAttribute("calendar", calendar);
		model.addAttribute("layout", "popup_layout");
		return "calendar_form";
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/modify/{id}")
	public String calendarModify(Model model, @Valid CalendarForm calendarForm, BindingResult bindingResult,
			@PathVariable("id") Integer id, Authentication authentication) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("layout", "popup_layout");
			return "calendar_form";
		}
		Calendar calendar = this.calendarService.getCalendar(id);
		String name = userService.getUserName(authentication);
		String providerid = userService.getProviderId(authentication);
		String subject = calendarForm.getSubject();
		String color = calendarForm.getColor();
		Boolean allday = null;
		if (calendarForm.getAllday() != null) {
			allday = calendarForm.getAllday();
		} else {
			allday = false;
		}
		LocalDateTime startDateTime = calendarService.setStartDateTime(calendarForm, allday);
		LocalDateTime endDateTime = calendarService.setEndDateTime(calendarForm, allday);

		if (calendarService.validation(startDateTime, endDateTime)) {
			model.addAttribute("errorMessage", "시작 시간이 종료 시간보다 늦거나 같을 수 없습니다.");
			model.addAttribute("layout", "popup_layout");
			return "calendar_form";
		}

		this.calendarService.modify(calendar, startDateTime, endDateTime, providerid, name, subject, allday, color);
		return String.format("redirect:/calendar/detail/%s", id);
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/push/{id}")
	public String calendarPush(Model model, @PathVariable("id") Integer id, Authentication authentication) {
		Calendar calendar = this.calendarService.getCalendar(id);
		String start;
		String end;
		if(calendar.isAllday()) {
			start = calendarService.setAlldayStartDateTime(calendar.getStartDateTime());
			end = calendarService.setAlldayEndDateTime(calendar.getEndDateTime());
		}else {
			start = calendarService.setDetailStartDateTime(calendar.getStartDateTime());
			end = calendarService.setDetailEndDateTime(calendar.getEndDateTime());
		}
		model.addAttribute("allday", calendar.isAllday());
		model.addAttribute("start", start);
		model.addAttribute("end", end);
		model.addAttribute("calendar", calendar);
		return "calendar_push";
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/push/{id}")
	public String calendarPush(Model model, @Valid CalendarPushForm calendarPushForm, @PathVariable("id") Integer id, Authentication authentication) {
		Calendar calendar = this.calendarService.getCalendar(id);
		ResponseEntity<String> result = calendarService.pushGoogleCalendar(authentication, calendar, calendarPushForm.getCalendar());
		if(result.getStatusCode() != HttpStatus.OK) {
			model.addAttribute("errorMessage", "오류가 발생하였습니다.");
		}
		String start = calendarService.setDetailStartDateTime(calendar.getStartDateTime());
		String end = calendarService.setDetailEndDateTime(calendar.getEndDateTime());
		if (authentication != null) {
			String providerid = userService.getProviderId(authentication);
			model.addAttribute("providerid", providerid);
		}
		model.addAttribute("start", start);
		model.addAttribute("end", end);
		model.addAttribute("calendar", calendar);
		return "calendar_detail";
	}
}
