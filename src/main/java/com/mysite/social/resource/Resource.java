package com.mysite.social.resource;

import java.time.LocalDateTime;
import java.util.List;

import com.mysite.social.answer.Answer;
import com.mysite.social.board.Board;
import com.mysite.social.reservation.Reservation;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Getter
@Setter
@Entity
public class Resource {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(columnDefinition = "TEXT")
	private String name;
	
	private String kind;
	
	private String color;
	
	private LocalDateTime createDate;
	
	private LocalDateTime modifyDate;
	
	@OneToMany(mappedBy = "resource", cascade = CascadeType.REMOVE)
	private List<Reservation> reservationList;
}