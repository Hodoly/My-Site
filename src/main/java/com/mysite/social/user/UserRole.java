package com.mysite.social.user;

import lombok.Getter;

@Getter
public enum UserRole {
	ADMIN("ROLE_ADMIN"),
//	GUEST("ROLE_GUEST"),
	USER("ROLE_USER");
	
	UserRole(String value){
		this.value = value;
	}
	
	private String value;
}
