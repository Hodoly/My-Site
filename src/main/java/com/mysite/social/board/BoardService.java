package com.mysite.social.board;

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
import com.mysite.social.category.Category;
import com.mysite.social.category.CategoryRepository;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class BoardService {
	private final BoardRepository boardRepository;
	private final CategoryRepository categoryRepository;

	private Specification<Board> search(String kw, int ct) {
		return new Specification<>() {
			private static final long serialVersionUID = 1L;

			@Override
			public Predicate toPredicate(Root<Board> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
				query.distinct(true);
				Join<Board, Answer> a = q.join("answerList", JoinType.LEFT);
				
				Predicate keywordPredicate = cb.or(cb.like(q.get("subject"), "%" + kw + "%"),
						cb.like(q.get("content"), "%" + kw + "%"), cb.like(q.get("authorname"), "%" + kw + "%"),
						cb.like(a.get("content"), "%" + kw + "%"), cb.like(a.get("authorname"), "%" + kw + "%"));
				if(ct!=0) {
					Category category = categoryRepository.getById(ct);
					Predicate categoryPredicate = cb.equal(q.get("category"), category);
					return cb.and(keywordPredicate, categoryPredicate);
				}else {
					return keywordPredicate;
				}
			}
		};
	}

	public Page<Board> getList(int page, String kw, int ct) {
		List<Sort.Order> sorts = new ArrayList<>();
		sorts.add(Sort.Order.desc("createDate"));
		Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
		Specification<Board> spec = search(kw, ct);
		return this.boardRepository.findAll(spec, pageable);
//		return this.BoardRepository.findAllByKeyword(kw, pageable);
	}

	public Board getBoard(Integer id) {
		Optional<Board> Board = this.boardRepository.findById(id);
		if (Board.isPresent()) {
			return Board.get();
		} else {
			throw new DataNotFoundException("Board not found");
		}
	}

	public void create(String subject, String content, String providerid, int cateId) {
		Board q = new Board();
		// TO-DO: 카테고리 가져오기
		Category category = categoryRepository.getById(cateId);
		q.setSubject(subject);
		q.setContent(content);
		q.setCreateDate(LocalDateTime.now());
		q.setCategory(category);
		q.setAuthor(providerid);
		this.boardRepository.save(q);
	}

	public void modify(Board board, String subject, String content) {
		board.setSubject(subject);
		board.setContent(content);
		board.setModifyDate(LocalDateTime.now());
		this.boardRepository.save(board);
	}

	public void delete(Board Board) {
		this.boardRepository.delete(Board);
	}

	public void vote(Board Board, String providerid) {
		Board.getVoter().add(providerid);
		this.boardRepository.save(Board);
	}
}
