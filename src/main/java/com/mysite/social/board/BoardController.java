package com.mysite.social.board;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import com.mysite.social.answer.Answer;
import com.mysite.social.answer.AnswerForm;
import com.mysite.social.answer.AnswerService;
import com.mysite.social.category.Category;
import com.mysite.social.category.CategoryService;
import com.mysite.social.comment.Comment;
import com.mysite.social.user.UserService;

import ch.qos.logback.core.recovery.ResilientSyslogOutputStream;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/board")
@RequiredArgsConstructor
@Controller
public class BoardController {
	private final BoardService boardService;
	private final AnswerService answerService;
	private final UserService userService;
	private final CategoryService categoryService;

	@GetMapping("/list")
	public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "kw", defaultValue = "") String kw,
			@RequestParam(value = "ct", defaultValue = "0") int ct, Authentication authentication) {
		Page<Board> paging = this.boardService.getList(page, kw, ct);
		List<Category> category = this.categoryService.getCategory();
		if (authentication != null) {
			String name = userService.getUserName(authentication);
			model.addAttribute("name", name);
		}
		model.addAttribute("category", category);
		model.addAttribute("paging", paging);
		model.addAttribute("kw", kw);
		model.addAttribute("ct", ct);

//		// 현재 인증된 사용자 가져오기
//		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//		boolean isAdmin = authentication.getAuthorities().stream()
//				.anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));

//		if (isAdmin) {
//			return "admin_board_list";
//		} else {
//			return "board_list";
//		}
		return "board_list";
	}

	@GetMapping(value = "/detail/{id}")
	public String detail(Model model, @PathVariable("id") Integer id, AnswerForm answerForm,
			@RequestParam(value = "page", defaultValue = "0") int page, Authentication authentication) {
		Board board = this.boardService.getBoard(id);
		Page<Answer> paging = this.answerService.getAnswer(board, page);
		Category category = board.getCategory();
		if (authentication != null) {
			String name = userService.getUserName(authentication);
			model.addAttribute("name", name);
		}
		model.addAttribute("category", category.getName());
		model.addAttribute("categoryid", category.getId());
		model.addAttribute("paging", paging);
		model.addAttribute("board", board);
		model.addAttribute("commentForm", new Comment());
		return "board_detail";
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/create")
	public String boardCreate(Model model, BoardForm boardForm, Authentication authentication) {
		List<Category> category = this.categoryService.getCategory();
		String name = userService.getUserName(authentication);
		model.addAttribute("name", name);
		model.addAttribute("category", category);
		return "board_form";
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/create")
	public String boardCreate(Model model, @Valid BoardForm boardForm, BindingResult bindingResult, Authentication authentication) {
		if (bindingResult.hasErrors()) {
			return "board_form";
		}
		String name = userService.getUserName(authentication);
		String providerid = userService.getProviderId(authentication);
		model.addAttribute("name", name);
		this.boardService.create(boardForm.getSubject(), boardForm.getContent(), providerid, boardForm.getCategory());
		return "redirect:/board/list";
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/modify/{id}")
	public String boardModify(Model model, BoardForm boardForm, @PathVariable("id") Integer id, Authentication authentication) {
		Board board = this.boardService.getBoard(id);
		String providerid = userService.getProviderId(authentication);
		if (!board.getAuthor().equals(providerid)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
		}
		String name = userService.getUserName(authentication);
		List<Category> category = this.categoryService.getCategory();
		model.addAttribute("name", name);
		model.addAttribute("category", category);
		boardForm.setSubject(board.getSubject());
		boardForm.setContent(board.getContent());
		return "board_form";
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/modify/{id}")
	public String boardModify(Model model, @Valid Board boardForm, BindingResult bindingResult,
			@PathVariable("id") Integer id, Authentication authentication) {
		if (bindingResult.hasErrors()) {
			return "board_form";
		}
		Board board = this.boardService.getBoard(id);
		String providerid = userService.getProviderId(authentication);
		String name = userService.getUserName(authentication);
		if (!board.getAuthor().equals(providerid)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
		}
		model.addAttribute("name", name);
		List<Category> category = this.categoryService.getCategory();
		model.addAttribute("category", category);
		this.boardService.modify(board, boardForm.getSubject(), boardForm.getContent());
		return String.format("redirect:/board/detail/%s", id);
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/delete/{id}")
	public String boardDelete(Model model, @PathVariable("id") Integer id, Authentication authentication) {
		String providerid = userService.getProviderId(authentication);
		String name = userService.getUserName(authentication);
		Board board = this.boardService.getBoard(id);
		if (!board.getAuthor().equals(providerid)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
		}
		this.boardService.delete(board);
		model.addAttribute("name", name);
		return "redirect:/";
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/vote/{id}")
	public String boardVote(Model model, @PathVariable("id") Integer id, Authentication authentication) {
		String name = userService.getUserName(authentication);
		model.addAttribute("name", name);
		Board board = this.boardService.getBoard(id);
		String providerid = userService.getProviderId(authentication);
		this.boardService.vote(board, providerid);
		return String.format("redirect:/board/detail/%s", id);
	}
}
