package com.mysite.social.board;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.mysite.social.answer.Answer;
import com.mysite.social.category.Category;
import com.mysite.social.comment.Comment;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;import jakarta.transaction.Transactional;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Board {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(length = 200)
	private String subject;
	
	@Column(columnDefinition = "TEXT")
	private String content;
	
	private LocalDateTime createDate;
	
	@OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE)
	private List<Answer> answerList;
	
	@OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE)
	private List<Comment> commentList;
	
	private String author;
	
	private String authorname;
	
	@ManyToOne
	private Category category;
	
	private LocalDateTime modifyDate;
	
}
