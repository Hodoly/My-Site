package com.mysite.social.reservation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.mysite.social.DataNotFoundException;
import com.mysite.social.board.Board;
import com.mysite.social.category.Category;
import com.mysite.social.category.CategoryRepository;
import com.mysite.social.reservation.Reservation;
import com.mysite.social.reservation.ReservationRepository;
import com.mysite.social.resource.Resource;
import com.mysite.social.resource.ResourceRepository;
import com.mysite.social.resource.ResourceService;
import com.mysite.social.user.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ReservationService {
	private final ResourceRepository resourceRepository;
	private final ReservationRepository reservationRepository;
	private final ResourceService resourceService;

	DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
	DateTimeFormatter detailFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd/HH:mm");
	
	public void create(LocalDateTime startDateTime, LocalDateTime endDateTime, String providerid, Resource resource,
			String name, String subject, Boolean allday) {
		Reservation r = new Reservation();
		r.setStartDateTime(startDateTime);
		r.setEndDateTime(endDateTime);
		r.setCreateDate(LocalDateTime.now());
		r.setResource(resource);
		r.setAuthor(providerid);
		r.setAuthorName(name);
		r.setSubject(subject);
		r.setAllday(allday);
		this.reservationRepository.save(r);
	}

	public Reservation getReservation(Integer id) {
		Optional<Reservation> reservation = reservationRepository.findById(id);
		if (reservation.isPresent()) {
			return reservation.get();
		} else {
			throw new DataNotFoundException("Reservation not found");
		}
	}

	public List<Reservation> getReservations(String kind) {
		List<Resource> resources = resourceService.getResource(kind);
		List<Reservation> reservations = new ArrayList<>();
		for (Resource resource : resources) {
			reservations.addAll(resource.getReservationList());
		}
		return reservations;
	}

	public List<Reservation> getSpecificReservations(Integer resourceId) {
		Resource resource = resourceService.getResource(resourceId);
		List<Reservation> reservations = new ArrayList<>();
		reservations.addAll(resource.getReservationList());
		return reservations;
	}

	public LocalDateTime setStartDateTime(ReservationForm reservationForm, boolean allday) {

		LocalDate startDate = LocalDate.parse(reservationForm.getStartDate(), dateFormatter);
		LocalTime startTime = null;

		if (Boolean.TRUE.equals(allday)) {
			startTime = LocalTime.parse("00:00", timeFormatter);
		} else {
			startTime = LocalTime.parse(reservationForm.getStartTime(), timeFormatter);
		}

		LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
		return startDateTime;
	}

	public LocalDateTime setEndDateTime(ReservationForm reservationForm, boolean allday) {

		LocalDate endDate = LocalDate.parse(reservationForm.getEndDate(), dateFormatter);
		LocalTime endTime = null;

		if (Boolean.TRUE.equals(allday)) {
			endTime = LocalTime.parse("23:59", timeFormatter);
		} else {
			endTime = LocalTime.parse(reservationForm.getEndTime(), timeFormatter);
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
	
	public boolean validation(LocalDateTime startDateTime, LocalDateTime endDateTime) {
		if (startDateTime.isEqual(endDateTime) || startDateTime.isAfter(endDateTime)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean overlap(Reservation res, Resource r, LocalDateTime s, LocalDateTime e, Boolean allDay) {
		List<Reservation> reservations = reservationRepository.findByResource(r);
		for (Reservation reservation : reservations) {
			// 수정일 경우...
			if(res != null && reservation.equals(res)){
				continue;
			}
			if (reservation.getStartDateTime().isBefore(e) && reservation.getEndDateTime().isAfter(s)) {
				return true; // 시간이 겹치는 예약이 있는 경우
			}
		}
		return false;
	}

	public void modify(Reservation r, LocalDateTime startDateTime, LocalDateTime endDateTime, String providerid, Resource resource,
			String name, String subject, Boolean allday) {
		r.setStartDateTime(startDateTime);
		r.setEndDateTime(endDateTime);
		r.setCreateDate(LocalDateTime.now());
		r.setResource(resource);
		r.setAuthor(providerid);
		r.setAuthorName(name);
		r.setSubject(subject);
		r.setAllday(allday);
		this.reservationRepository.save(r);
	}
	public void delete(Reservation reservation) {
		this.reservationRepository.delete(reservation);
	}

}
