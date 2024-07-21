package com.mysite.social.reservation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import org.springframework.web.server.ResponseStatusException;

import com.mysite.social.answer.Answer;
import com.mysite.social.answer.AnswerForm;
import com.mysite.social.board.Board;
import com.mysite.social.board.BoardForm;
import com.mysite.social.category.Category;
import com.mysite.social.comment.Comment;
import com.mysite.social.reservation.ReservationForm;
import com.mysite.social.resource.Resource;
import com.mysite.social.resource.ResourceService;
import com.mysite.social.user.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/reservation")
@RequiredArgsConstructor
@Controller
public class ReservationController {
	private final UserService userService;
	private final ReservationService reservationService;
	private final ResourceService resourceService;

	@GetMapping("/home/{kind}")
	public String reservationHome(Model model, @PathVariable("kind") String kind) {
		return kind + "_home";
	}

	@GetMapping("/check")
	public String reservationChk() {
		return "reservation_check";
	}

	@GetMapping(value = "/detail/{kind}/{id}")
	public String detail(Model model, @PathVariable("kind") String kind, @PathVariable("id") Integer id,
			Authentication authentication) {
		Reservation reservation = this.reservationService.getReservation(id);
		if (authentication != null) {
			String providerid = userService.getProviderId(authentication);
			model.addAttribute("providerid", providerid);
		}
		Resource resource = reservation.getResource();

		String start = reservationService.setDetailStartDateTime(reservation.getStartDateTime());
		String end = reservationService.setDetailEndDateTime(reservation.getEndDateTime());

		model.addAttribute("start", start);
		model.addAttribute("end", end);
		model.addAttribute("reservation", reservation);
		model.addAttribute("kind", kind);
		model.addAttribute("resourcename", resource.getName());
		return "reservation_detail";
	}

	@GetMapping("/get/reservations")
	@ResponseBody
	public List<ReservationEvent> getReservations(@RequestParam(required = false) String kind,
			@RequestParam(required = false) Integer id) {
		List<Reservation> reservations = null;
		if (kind != null) {
			reservations = reservationService.getReservations(kind);
		} else if (id != null) {
			reservations = reservationService.getSpecificReservations(id);
		}
		return reservations.stream().map(reservation -> {
			String start;
			String end;

			if (reservation.isAllday()) {
				// 종일 이벤트인 경우 LocalDate로 변환
				start = reservation.getStartDateTime().toLocalDate().toString();
				end = reservation.getEndDateTime().toLocalDate().toString();
			} else {
				// 시간 기반 이벤트인 경우 LocalDateTime 그대로 사용
				start = reservation.getStartDateTime().toString();
				end = reservation.getEndDateTime().toString();
			}

			return new ReservationEvent(start, end, reservation.getSubject(), reservation.getResource().getColor(),
					reservation.isAllday(), reservation.getId());
		}).collect(Collectors.toList());
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/create/{kind}")
	public String createReservation(Model model, ReservationForm reservationFormm, @PathVariable("kind") String kind) {
		model.addAttribute("resource", resourceService.getResource(kind));
		model.addAttribute("reservation", new Reservation());
		model.addAttribute("layout", "layout");
		return kind + "_form";
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/create/{kind}")
	public String createReservation(Model model, @Valid ReservationForm reservationForm, BindingResult bindingResult,
			Authentication authentication, @PathVariable("kind") String kind) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("resource", resourceService.getResource(kind));
			model.addAttribute("layout", "layout");
			return kind + "_form";
		}

		String name = userService.getUserName(authentication);
		String providerid = userService.getProviderId(authentication);
		Resource resource = resourceService.getResourceById(reservationForm.getResource());
		String subject = reservationForm.getSubject();
		Boolean allday = null;
		if (reservationForm.getAllday() != null) {
			allday = reservationForm.getAllday();
		} else {
			allday = false;
		}
		LocalDateTime startDateTime = reservationService.setStartDateTime(reservationForm, allday);
		LocalDateTime endDateTime = reservationService.setEndDateTime(reservationForm, allday);

		if (reservationService.validation(startDateTime, endDateTime)) {
			model.addAttribute("resource", resourceService.getResource(kind));
			model.addAttribute("errorMessage", "예약 시작 시간이 종료 시간보다 늦거나 같을 수 없습니다.");
			model.addAttribute("layout", "layout");
			return kind + "_form";
		}

		if (reservationService.overlap(null, resource, startDateTime, endDateTime, allday)) {
			model.addAttribute("resource", resourceService.getResource(kind));
			model.addAttribute("errorMessage", "예약이 중복됩니다.");
			model.addAttribute("layout", "layout");
			return kind + "_form";
		}

		reservationService.create(startDateTime, endDateTime, providerid, resource, name, subject, allday);
		return "redirect:/reservation/home/" + kind;
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/delete/{id}")
	@ResponseBody
	public ResponseEntity<String> reservationDelete(Model model, @PathVariable("id") Integer id, Authentication authentication) {
		Reservation reservation = this.reservationService.getReservation(id);
		String providerid = userService.getProviderId(authentication);
		if (!reservation.getAuthor().equals(providerid)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
		}
		this.reservationService.delete(reservation);
		return ResponseEntity.ok("삭제 성공");
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/modify/{id}/{kind}")
	public String reservaitonModify(Model model, ReservationForm reservationForm, @PathVariable("id") Integer id,
			@PathVariable("kind") String kind, Authentication authentication) {
		Reservation reservation = this.reservationService.getReservation(id);

		String providerid = userService.getProviderId(authentication);

		if (!reservation.getAuthor().equals(providerid)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
		}

		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

		reservationForm.setSubject(reservation.getSubject());
		reservationForm.setStartDate(reservation.getStartDateTime().format(dateFormatter));
		reservationForm.setEndDate(reservation.getEndDateTime().format(dateFormatter));
		reservationForm.setStartTime(reservation.getStartDateTime().format(timeFormatter));
		reservationForm.setEndTime(reservation.getEndDateTime().format(timeFormatter));
		reservationForm.setResource(reservation.getResource().getId());

		model.addAttribute("reservation", reservation);
		model.addAttribute("resource", resourceService.getResource(kind));
		model.addAttribute("layout", "popup_layout");
		return kind + "_form";
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/modify/{id}/{kind}")
	public String reservaitonModify(Model model, @Valid ReservationForm reservationForm, BindingResult bindingResult,
			@PathVariable("id") Integer id, @PathVariable("kind") String kind, Authentication authentication) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("resource", resourceService.getResource(kind));
			model.addAttribute("layout", "popup_layout");
			return kind + "_form";
		}
		Reservation reservation = this.reservationService.getReservation(id);
		String name = userService.getUserName(authentication);
		String providerid = userService.getProviderId(authentication);
		Resource resource = resourceService.getResourceById(reservationForm.getResource());
		String subject = reservationForm.getSubject();
		Boolean allday = null;
		if (reservationForm.getAllday() != null) {
			allday = reservationForm.getAllday();
		} else {
			allday = false;
		}
		LocalDateTime startDateTime = reservationService.setStartDateTime(reservationForm, allday);
		LocalDateTime endDateTime = reservationService.setEndDateTime(reservationForm, allday);

		if (reservationService.validation(startDateTime, endDateTime)) {
			model.addAttribute("resource", resourceService.getResource(kind));
			model.addAttribute("errorMessage", "예약 시작 시간이 종료 시간보다 늦거나 같을 수 없습니다.");
			model.addAttribute("layout", "popup_layout");
			return kind + "_form";
		}

		if (reservationService.overlap(reservation, resource, startDateTime, endDateTime, allday)) {
			model.addAttribute("resource", resourceService.getResource(kind));
			model.addAttribute("errorMessage", "예약이 중복됩니다.");
			model.addAttribute("layout", "popup_layout");
			return kind + "_form";
		}

		this.reservationService.modify(reservation, startDateTime, endDateTime, providerid, resource, name, subject,
				allday);
		return String.format("redirect:/reservation/detail/%s/%s", kind, id);
	}
}
