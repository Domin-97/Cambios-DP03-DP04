package services;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.CategoryRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import domain.Category;

@Service
@Transactional
public class CategoryService {

	//Managed repositories

	@Autowired
	private CategoryRepository	categoryRepository;


	//Suporting services

	// Constructors

	public CategoryService() {
		super();
	}

	//Simple CRUD methods

	public Category create() {
		//Check if is an administrator
		UserAccount userAccount;
		userAccount = LoginService.getPrincipal();
		final Authority auth = new Authority();
		auth.setAuthority(Authority.ADMIN);
		Assert.isTrue(userAccount.getAuthorities().contains(auth));

		Category result;
		//We create the category
		result = new Category();
		result.setParentCategory(this.findByName("CATEGORY"));

		return result;

	}

	public Category save(final Category category) {
		Assert.notNull(category);
		Assert.isTrue(!this.findOne(category.getId()).getName().equals("CATEGORY"));

		Category result;
		UserAccount userAccount;
		//Check that is an administrator
		userAccount = LoginService.getPrincipal();
		final Authority auth = new Authority();
		auth.setAuthority(Authority.ADMIN);
		Assert.isTrue(userAccount.getAuthorities().contains(auth));
		//Check the parent of the category
		if (category.getParentCategory() == null)
			category.setParentCategory(this.findByName("CATEGORY"));

		result = this.categoryRepository.save(category);
		return result;
	}
	public Collection<Category> findAll() {
		Collection<Category> result;
		result = this.categoryRepository.findAll();
		return result;
	}

	public Category findOne(final int categoryId) {
		Assert.isTrue(categoryId != 0);
		Category result;
		result = this.categoryRepository.findOne(categoryId);
		return result;
	}

	public void delete(final Category category) {
		Assert.notNull(category);
		Assert.isTrue(category != this.findByName("CATEGORY"));
		//Check that is an administrator
		UserAccount userAccount;
		userAccount = LoginService.getPrincipal();
		final Authority auth = new Authority();
		auth.setAuthority(Authority.ADMIN);
		Assert.isTrue(userAccount.getAuthorities().contains(auth));
		//Check the parent of the category
		for (final Category c : this.findChildren(category)) {
			c.setParentCategory(category.getParentCategory());
			this.save(c);
		}

		this.categoryRepository.delete(category);

	}

	//Other methods
	//Find a category
	public Category findByName(final String name) {
		Category result;
		result = this.categoryRepository.getCategoryByName(name);
		return result;
	}
	//Find the children of the category
	public Collection<Category> findChildren(final Category parent) {
		return this.categoryRepository.getChildren(parent);
	}
	public void flush() {
		this.categoryRepository.flush();
	}

}
