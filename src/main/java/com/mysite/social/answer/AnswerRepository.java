package com.mysite.social.answer;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;

import com.mysite.social.board.Board;

public interface AnswerRepository extends JpaRepository<Answer,Integer>{
	Page<Answer> findByBoard(Board board, Pageable pageable);
}
