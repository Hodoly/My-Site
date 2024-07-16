package com.mysite.social.comment;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.springframework.stereotype.Service;

import com.mysite.social.answer.Answer;
import com.mysite.social.answer.AnswerRepository;
import com.mysite.social.board.Board;
import com.mysite.social.board.BoardRepository;
import com.mysite.social.user.SiteUser;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommentService {
	private final CommentRepository commentRepository;
	private final BoardRepository questionRepository;
	private final AnswerRepository answerRepository;
	
	public Comment create(Board board, Answer answer, String content, SiteUser author, String kind) {
		Comment comment = new Comment();
		comment.setContent(content);
		comment.setCreateDate(LocalDateTime.now());
		comment.setAuthor(author);
		comment.setKind(kind);
		if(board != null) {
			comment.setBoard(board);
			this.commentRepository.save(comment);
		}else {
			comment.setAnswer(answer);
			this.commentRepository.save(comment);
		}
		return comment;
	}

//	public Page<Comment> getQuestionComment(Question question, int page) {
//		List<Sort.Order> sorts = new ArrayList<>();
//		sorts.add(Sort.Order.asc("createDate"));
//		Pageable pageable = PageRequest.of(page, 3, Sort.by(sorts));
//		return this.commentRepository.findByQuestion(question, pageable);
//	}
	
	public ArrayList<Comment> getQuestionComment(Board board) {
		return this.commentRepository.findByBoard(board);
	}
	
	
	public ArrayList<Comment> getAnswerComment(Answer answer) {
		return this.commentRepository.findByAnswer(answer);
	}

}
