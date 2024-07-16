package com.mysite.social.category;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mysite.social.answer.Answer;
import com.mysite.social.board.Board;
public interface CategoryRepository extends JpaRepository<Category, Integer> {

}
