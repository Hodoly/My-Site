package com.mysite.social.resource;

import org.springframework.data.domain.Page;
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

import com.mysite.social.user.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/resource")
@RequiredArgsConstructor
@Controller
public class ResourceController {
	private final UserService userService;
	private final ResourceService resourceService;

	@GetMapping("/list")
	public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "kw", defaultValue = "") String kw,
			@RequestParam(value = "kd", defaultValue = "0") int kd, Authentication authentication) {
		Page<Resource> paging = resourceService.getList(page, kw, kd);
		if (authentication != null) {
			String name = userService.getUserName(authentication);
			model.addAttribute("name", name);
		}
		model.addAttribute("paging", paging);
		model.addAttribute("kw", kw);
		model.addAttribute("kd", kd);
		return "resource_list";
	}

	@GetMapping(value = "/detail/{id}")
	public String detail(Model model, @PathVariable("id") Integer id, Authentication authentication) {
		Resource resource = this.resourceService.getResource(id);
		if (authentication != null) {
			String name = userService.getUserName(authentication);
			model.addAttribute("name", name);
		}
		
		model.addAttribute("resource", resource);
		model.addAttribute("kind", resource.getKind());
		return "resource_detail";
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/create")
	public String resourceCreate(Model model, ResourceForm resourceForm, Authentication authentication) {
		if (authentication != null) {
			String name = userService.getUserName(authentication);
			model.addAttribute("name", name);
		}
		return "resource_form";
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/create")
	public String resourceCreate(Model model, @Valid ResourceForm resourceForm, BindingResult bindingResult,
			Authentication authentication) {
		if (bindingResult.hasErrors()) {
			return "resource_form";
		}
		String name = userService.getUserName(authentication);
		model.addAttribute("name", name);
		this.resourceService.create(resourceForm.getName(), resourceForm.getKind(), resourceForm.getColor());
		return "redirect:/resource/list";
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/modify/{id}")
	public String resourceModify(Model model, ResourceForm resourceForm, @PathVariable("id") Integer id,
			Authentication authentication) {
		Resource resource = this.resourceService.getResource(id);
		String name = userService.getUserName(authentication);
		model.addAttribute("name", name);
		resourceForm.setName(resource.getName());
		resourceForm.setColor(resource.getColor());
		resourceForm.setKind(resource.getKind());
		return "resource_form";
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/modify/{id}")
	public String resourceModify(Model model, @Valid Resource resourceForm, BindingResult bindingResult,
			@PathVariable("id") Integer id, Authentication authentication) {
		if (bindingResult.hasErrors()) {
			return "resource_form";
		}
		Resource resource = this.resourceService.getResource(id);
		String name = userService.getUserName(authentication);
		model.addAttribute("name", name);
		this.resourceService.modify(resource, resourceForm.getName(), resourceForm.getKind(), resourceForm.getColor());
		return String.format("redirect:/resource/detail/%s", id);
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/delete/{id}")
	public String resourceDelete(Model model, @PathVariable("id") Integer id, Authentication authentication) {
		String name = userService.getUserName(authentication);
		model.addAttribute("name", name);
		Resource resource = this.resourceService.getResource(id);
		this.resourceService.delete(resource);
		return "redirect:/resource/list";
	}
}
