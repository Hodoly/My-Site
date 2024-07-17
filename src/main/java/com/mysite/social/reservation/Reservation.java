package com.mysite.social.reservation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.mysite.social.answer.Answer;
import com.mysite.social.category.Category;
import com.mysite.social.comment.Comment;
import com.mysite.social.resource.Resource;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Reservation {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private LocalDateTime createDate;
	
	private String author;

	private String authorName;

	@ManyToOne
	private Resource resource;

	private LocalDateTime modifyDate;
	
	private LocalDateTime startDateTime;

	private LocalDateTime endDateTime;
	
	@Column(length = 200)
	private String subject;
	
	private boolean allday;
}
