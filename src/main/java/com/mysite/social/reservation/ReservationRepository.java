package com.mysite.social.reservation;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mysite.social.resource.Resource;

public interface ReservationRepository extends JpaRepository<Reservation, Integer>{
	List<Reservation> findByResource(Resource r);
}
