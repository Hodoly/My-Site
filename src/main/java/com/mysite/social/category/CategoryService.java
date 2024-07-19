package com.mysite.social.category;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CategoryService {
	private final CategoryRepository categoryRepository;

	public Category create(String name, Boolean useAnswer) {
		Category category = new Category();
		category.setName(name);
		category.setUseAnswer(useAnswer);
		this.categoryRepository.save(category);
		return category;
	}

	public List getCategory() {
		List<Category> category = categoryRepository.findAll();
		return category;
	}
}
