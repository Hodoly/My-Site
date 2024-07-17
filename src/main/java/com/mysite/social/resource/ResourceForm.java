package com.mysite.social.resource;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResourceForm {
	@NotEmpty(message="명칭은 필수입니다.")
	private String name;
	
	private String kind;
	
	private String color;
}
