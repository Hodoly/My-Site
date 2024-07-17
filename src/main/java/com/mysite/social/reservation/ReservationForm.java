package com.mysite.social.reservation;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservationForm {
	private int resource;
	
	private String startDate;
	
	private String endDate;
	
	private String startTime;
	
	private String endTime;
	
	@NotEmpty(message="제목은 필수 항목입니다.")
	@Size(max=200)
	private String subject;
	
	private Boolean allday;
	
//	private LocalDateTime startDateTime;
//	
//	private LocalDateTime endDateTime;
	
}
