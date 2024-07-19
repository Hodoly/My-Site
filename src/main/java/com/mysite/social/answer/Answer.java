package com.mysite.social.answer;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;

import java.util.List;
import java.util.Set;

import com.mysite.social.board.Board;
import com.mysite.social.comment.Comment;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Getter
@Setter
@Entity
public class Answer {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(columnDefinition = "TEXT")
	private String content;
	
	@CreatedDate
	private LocalDateTime createDate;
	
	@ManyToOne
	private Board board;
	
	@OneToMany(mappedBy = "answer", cascade = CascadeType.REMOVE)
	private List<Comment> commentList;
	
	private String author;
	
	private String authorname;
	
	private LocalDateTime modifyDate;
	
}
