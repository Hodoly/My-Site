package com.mysite.social.answer;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import com.mysite.social.board.Board;
import com.mysite.social.board.BoardService;
import com.mysite.social.user.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/answer")
@RequiredArgsConstructor
@Controller
public class AnswerController {
	private final BoardService boardService;
	private final AnswerService answerService;
	private final UserService userService;

//	@PreAuthorize("isAuthenticated()")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping("/create/{id}")
	public String createAnswer(Model model, @PathVariable("id") Integer id, @Valid AnswerForm answerForm,
			BindingResult bindingResult, Principal principal, Authentication authentication) {
		Board board = this.boardService.getBoard(id);
		String providerid = userService.getProviderId(authentication);
		String name = userService.getUserName(authentication);
		if (bindingResult.hasErrors()) {
			model.addAttribute("board", board);
			return "board_detail";
		}
		Answer answer = this.answerService.create(board, answerForm.getContent(), providerid , name);
		return String.format("redirect:/board/detail/%s#answer_%s", answer.getBoard().getId(), answer.getId());
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/modify/{id}")
	public String ansewrModify(AnswerForm answerForm, Model model, @PathVariable("id") Integer id, Principal principal, Authentication authentication) {
		model.addAttribute("boadr_id", id);
		Answer answer = this.answerService.getAnswer(id);
		String providerid = userService.getProviderId(authentication);
		if (!answer.getAuthor().equals(providerid)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
		}
		answerForm.setContent(answer.getContent());
		return "answer_form";
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/modify/{id}")
	public String answerModify(@Valid AnswerForm answerForm, BindingResult bindingResult,
			@PathVariable("id") Integer id, Principal principal, Authentication authentication) {
		if (bindingResult.hasErrors()) {
			return "answer_form";
		}
		Answer answer = this.answerService.getAnswer(id);
		String providerid = userService.getProviderId(authentication);
		if (!answer.getAuthor().equals(providerid)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
		}
		this.answerService.modify(answer, answerForm.getContent());
		return String.format("redirect:/board/detail/%s", id);
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/delete/{id}")
	public String answerDelete(Principal principal, @PathVariable("id") Integer id, Authentication authentication) {
		Answer answer = this.answerService.getAnswer(id);
		String providerid = userService.getProviderId(authentication);
		if (!answer.getAuthor().equals(providerid)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
		}
		this.answerService.delete(answer);
		return String.format("redirect:/board/detail/%s", answer.getBoard().getId());
	}

}
