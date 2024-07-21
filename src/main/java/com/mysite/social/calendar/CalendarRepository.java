package com.mysite.social.calendar;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mysite.social.resource.Resource;

public interface CalendarRepository extends JpaRepository<Calendar, Integer>{
	List<Calendar> findByAuthor(String providerid);
}
