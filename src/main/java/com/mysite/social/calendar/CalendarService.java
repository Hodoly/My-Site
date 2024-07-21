package com.mysite.social.calendar;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.google.gson.JsonObject;
import com.mysite.social.DataNotFoundException;

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
	private final CalendarRepository calendarRepository;

	DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
	DateTimeFormatter detailFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd/HH:mm");

	public CalendarService(OAuth2AuthorizedClientService authorizedClientService,
			CalendarRepository calendarRepository) {
		this.restTemplate = new RestTemplate();
		this.authorizedClientService = authorizedClientService;
		this.calendarRepository = calendarRepository;
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

	public void create(LocalDateTime startDateTime, LocalDateTime endDateTime, String providerid, String name,
			String subject, Boolean allday, String color) {
		Calendar c = new Calendar();
		c.setStartDateTime(startDateTime);
		c.setEndDateTime(endDateTime);
		c.setCreateDate(LocalDateTime.now());
		c.setAuthor(providerid);
		c.setAuthorName(name);
		c.setSubject(subject);
		c.setAllday(allday);
		c.setColor(color);
		this.calendarRepository.save(c);
	}

	public LocalDateTime setStartDateTime(CalendarForm calendarForm, boolean allday) {

		LocalDate startDate = LocalDate.parse(calendarForm.getStartDate(), dateFormatter);
		LocalTime startTime = null;

		if (Boolean.TRUE.equals(allday)) {
			startTime = LocalTime.parse("00:00", timeFormatter);
		} else {
			startTime = LocalTime.parse(calendarForm.getStartTime(), timeFormatter);
		}

		LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
		return startDateTime;
	}

	public LocalDateTime setEndDateTime(CalendarForm calendarForm, boolean allday) {

		LocalDate endDate = LocalDate.parse(calendarForm.getEndDate(), dateFormatter);
		LocalTime endTime = null;

		if (Boolean.TRUE.equals(allday)) {
			endTime = LocalTime.parse("23:59", timeFormatter);
		} else {
			endTime = LocalTime.parse(calendarForm.getEndTime(), timeFormatter);
		}

		LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime);
		return endDateTime;
	}

	public String setDetailStartDateTime(LocalDateTime startDateTime) {
		return startDateTime.format(detailFormatter);
	}

	public String setDetailEndDateTime(LocalDateTime endDateTime) {
		return endDateTime.format(detailFormatter);
	}

	public String setAlldayStartDateTime(LocalDateTime startDateTime) {
		return startDateTime.format(dateFormatter);
	}

	public String setAlldayEndDateTime(LocalDateTime endDateTime) {
		return endDateTime.format(dateFormatter);
	}

	public boolean validation(LocalDateTime startDateTime, LocalDateTime endDateTime) {
		if (startDateTime.isEqual(endDateTime) || startDateTime.isAfter(endDateTime)) {
			return true;
		} else {
			return false;
		}
	}

	public List<Calendar> getCalendar(String providerid) {
		List<Calendar> calendars = calendarRepository.findByAuthor(providerid);
		return calendars;
	}

	public Calendar getCalendar(Integer id) {
		Optional<Calendar> calendar = calendarRepository.findById(id);
		if (calendar.isPresent()) {
			return calendar.get();
		} else {
			throw new DataNotFoundException("Calendar not found");
		}
	}

	public void modify(Calendar c, LocalDateTime startDateTime, LocalDateTime endDateTime, String providerid,
			String name, String subject, Boolean allday, String color) {
		c.setStartDateTime(startDateTime);
		c.setEndDateTime(endDateTime);
		c.setCreateDate(LocalDateTime.now());
		c.setAuthor(providerid);
		c.setAuthorName(name);
		c.setSubject(subject);
		c.setAllday(allday);
		c.setColor(color);
		this.calendarRepository.save(c);
	}

	public void delete(Calendar calendar) {
		this.calendarRepository.delete(calendar);
	}

	public ResponseEntity<String> pushGoogleCalendar(Authentication authentication, Calendar calendar,
			String googleCalId) {
		OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient("google",
				authentication.getName());
		OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
		String url = "https://www.googleapis.com/calendar/v3/calendars/" + googleCalId + "/events";
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(accessToken.getTokenValue());
		headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
		JsonObject event = new JsonObject();
		event.addProperty("summary", calendar.getSubject());
		JsonObject start = new JsonObject();
		JsonObject end = new JsonObject();
		ZonedDateTime sTmp = null;
		ZonedDateTime eTmp = null;
		String startDateTime;
		String endDateTime;
		if (calendar.isAllday()) {
			sTmp = ZonedDateTime.of(calendar.getStartDateTime(), ZoneId.of("Asia/Dili"));
			eTmp = ZonedDateTime.of(calendar.getEndDateTime(), ZoneId.of("Asia/Dili"));
			startDateTime = sTmp.format(dateFormatter);
			endDateTime = eTmp.format(dateFormatter);
			start.addProperty("date", startDateTime);
			end.addProperty("date", endDateTime);
		} else {
			sTmp = calendar.getStartDateTime().atZone(ZoneId.of("Asia/Dili"));
			eTmp = calendar.getEndDateTime().atZone(ZoneId.of("Asia/Dili"));
			startDateTime = sTmp.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
			endDateTime = eTmp.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
			start.addProperty("dateTime", startDateTime);
			end.addProperty("dateTime", endDateTime);
		}
		start.addProperty("timeZone", "Asia/Dili");
		end.addProperty("timeZone", "Asia/Dili");
		event.add("start", start);
		event.add("end", end);
		HttpEntity<String> requestEntity = new HttpEntity<>(event.toString(), headers);
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

		return response;
	}
}
