package com.mysite.social.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mysite.social.answer.Answer;
import com.mysite.social.board.Board;
public interface ResourceRepository extends JpaRepository<Resource, Integer> {
	List<Resource> findByKind(String kind);
	Page<Resource> findAll(Specification<Resource> spec, Pageable pageable);
}
