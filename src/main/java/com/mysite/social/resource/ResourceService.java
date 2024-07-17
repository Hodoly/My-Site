package com.mysite.social.resource;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.mysite.social.DataNotFoundException;
import com.mysite.social.answer.Answer;
import com.mysite.social.board.Board;
import com.mysite.social.category.Category;
import com.mysite.social.category.CategoryRepository;
import com.mysite.social.reservation.Reservation;
import com.mysite.social.reservation.ReservationRepository;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ResourceService {
	private final ResourceRepository resourceRepository;

	public List getResource(String kind) {
		List<Resource> resource = null;
		if (kind.equals("room")) {
			resource = resourceRepository.findByKind("1");
		} else if (kind.equals("car")) {
			resource = resourceRepository.findByKind("2");
		}
		return resource;
	}

	public Resource getSpecificResource(Integer id) {
		Optional<Resource> resource = resourceRepository.findById(id);
		if (resource.isPresent()) {
			return resource.get();
		} else {
			throw new DataNotFoundException("Resource not found");
		}
	}

	public Resource getResourceById(Integer id) {
		Optional<Resource> resource = resourceRepository.findById(id);
		if (resource.isPresent()) {
			return resource.get();
		} else {
			throw new DataNotFoundException("Resource not found");
		}
	}

	private Specification<Resource> search(String kw, int kd) {
		return new Specification<>() {
			private static final long serialVersionUID = 1L;

			@Override
			public Predicate toPredicate(Root<Resource> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
				query.distinct(true);
				Predicate keywordPredicate = cb.or(cb.like(q.get("name"), "%" + kw + "%"));
				if (kd != 0) {
					Predicate categoryPredicate = cb.equal(q.get("kind"), kd);
					return cb.and(keywordPredicate, categoryPredicate);
				} else {
					return keywordPredicate;
				}
			}
		};
	}

	public Page<Resource> getList(int page, String kw, int kd) {
		List<Sort.Order> sorts = new ArrayList<>();
		sorts.add(Sort.Order.desc("createDate"));
		Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
		Specification<Resource> spec = search(kw, kd);
		return this.resourceRepository.findAll(spec, pageable);
	}

	public Resource getResource(Integer id) {
		Optional<Resource> Resource = this.resourceRepository.findById(id);
		if (Resource.isPresent()) {
			return Resource.get();
		} else {
			throw new DataNotFoundException("Resource not found");
		}
	}

	public Resource create(String name, String kind, String color) {
		Resource resource = new Resource();
		resource.setColor(color);
		resource.setName(name);
		resource.setKind(kind);
		resource.setCreateDate(LocalDateTime.now());
		this.resourceRepository.save(resource);
		return resource;
	}

	public void modify(Resource resource, String name, String kind, String color) {
		resource.setColor(color);
		resource.setName(name);
		resource.setKind(kind);
		resource.setModifyDate(LocalDateTime.now());
		this.resourceRepository.save(resource);
	}

	public void delete(Resource resource) {
		this.resourceRepository.delete(resource);
	}
}
