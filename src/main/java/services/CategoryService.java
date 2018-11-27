
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

		UserAccount userAccount;
		userAccount = LoginService.getPrincipal();
		Authority auth = new Authority();
		auth.setAuthority(Authority.ADMIN);
		Assert.isTrue(userAccount.getAuthorities().contains(auth));

		Category result;

		result = new Category();
		result.setParentCategory(this.findByName("CATEGORY"));

		return result;

	}

	public Category save(Category category) {
		Assert.notNull(category);
		Category result;
		UserAccount userAccount;
		userAccount = LoginService.getPrincipal();
		final Authority auth = new Authority();
		auth.setAuthority(Authority.ADMIN);
		Assert.isTrue(userAccount.getAuthorities().contains(auth));

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

	public Category findOne(int categoryId) {
		Assert.isTrue(categoryId != 0);
		Category result;
		result = this.categoryRepository.findOne(categoryId);
		return result;
	}

	public void delete(Category category) {
		Assert.notNull(category);
		Assert.isTrue(category!=this.findByName("CATEGORY"));
		UserAccount userAccount;
		userAccount = LoginService.getPrincipal();
		final Authority auth = new Authority();
		auth.setAuthority(Authority.ADMIN);
		Assert.isTrue(userAccount.getAuthorities().contains(auth));
		
		for (Category c : this.findAll())
			if (c.getParentCategory().equals(category)) {
				c.setParentCategory(this.findByName("CATEGORY"));
				this.save(c);
			}
		
		this.categoryRepository.delete(category);

	}

	//Other methods

	public Category findByName(final String name) {
		Category result;
		result = this.categoryRepository.getCategoryByName(name);
		return result;
	}

}
